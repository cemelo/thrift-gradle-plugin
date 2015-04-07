/*
 * Copyright (c) 2015
 * Ministério do Planejamento, Orçamento e Gestão
 * República Federativa do Brasil
 *
 * Este programa é um software livre; você pode redistribuí-lo
 * e/ou modificá-lo sob os termos da Licença de Software SIOP; O
 * texto da licença pode ser consultado no arquivo anexo a esta
 * distribuição ou em <http://www.siop.gov.br/licença>.
 */

package br.gov.siop.gradle.thrift.plugin
import br.gov.siop.gradle.thrift.plugin.extensions.ThriftOptions
import br.gov.siop.gradle.thrift.plugin.internal.tasks.DefaultThriftSourceSet
import br.gov.siop.gradle.thrift.plugin.tasks.ScroogeCompile
import br.gov.siop.gradle.thrift.plugin.tasks.ThriftCompile
import groovy.io.FileType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

/**
 * The thrift compiler plugin.
 *
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 * @since 0.1, 06/04/2015
 */
class ThriftPlugin implements Plugin<Project> {

    public static final String COMPILE_THRIFT_TASK_NAME = 'compileThrift'

    public static final String COMPILE_SCROOGE_TASK_NAME = 'compileScrooge'

    private final FileResolver fileResolver

    private Project project

    @Inject
    ThriftPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    @Override
    void apply(Project project) {
        this.project = project

        createPluginExtensions()
        configureCompileTask()
        configureIDEs()
    }

    public void configureIDEs() {
        configureIdea()
    }

    private void configureIdea() {
        if (project.plugins.hasPlugin('idea') &&
                project.file("${project.buildDir}/generated-sources/").exists()) {
            project.idea.module.excludeDirs -= project.buildDir

            project.file("${project.buildDir}/generated-sources/").eachFile(FileType.DIRECTORIES) {
                if (project.file("${it}/thrift/").exists()) {
                    project.file("${it}/thrift/").eachFile(FileType.DIRECTORIES) {
                        project.idea.module.generatedSourceDirs += it
                    }
                }
            }
        }
    }

    private void createPluginExtensions() {
        project.extensions.create(ThriftOptions.EXTENSION_NAME, ThriftOptions)
    }

    private void configureCompileTask() {
        if (project.plugins.hasPlugin('java')) {
            configureSourceSetDefaults()
        } else {
            createCompileTask()
        }
    }

    private void createCompileTask() {
        FileTree fileTree = project.fileTree("src/main/thrift")
        fileTree.include("**/*.thrift")

        ThriftCompile thriftCompile = project.tasks.create(COMPILE_THRIFT_TASK_NAME, ThriftCompile)
        thriftCompile.setSource(fileTree)
        thriftCompile.setDescription("Compiles the Thrift source.")
        thriftCompile.options = project.thrift

        if (project.plugins.hasPlugin('scala')) {
            ScroogeCompile scroogeCompile = project.tasks.create(COMPILE_SCROOGE_TASK_NAME, ScroogeCompile)
            scroogeCompile.setSource(fileTree)
            scroogeCompile.setDescription("Compiles the Thrift source using the scrooge compiler.")

            thriftCompile.dependsOn(scroogeCompile)
        }
    }

    private void configureSourceSetDefaults() {
        project.convention.getPlugin(JavaPluginConvention).sourceSets.all { SourceSet sourceSet ->
            final DefaultThriftSourceSet thriftSourceSet =
                    new DefaultThriftSourceSet((sourceSet as DefaultSourceSet).displayName, fileResolver)
            new DslObject(sourceSet).convention.plugins << ['thrift': thriftSourceSet]

            thriftSourceSet.thrift.srcDir("src/${sourceSet.name}/thrift")
            sourceSet.resources.filter.exclude { element ->
                thriftSourceSet.thrift.contains(element.file)
            }

            String thriftCompileTaskName = sourceSet.getCompileTaskName('thrift')

            if (project.thrift.outputDir == null) {
                project.thrift.outputDir = project.file(
                        "${project.buildDir}/generated-sources/${sourceSet.name}/thrift")
            }

            ThriftCompile thriftCompile = project.tasks.create(thriftCompileTaskName, ThriftCompile)
            thriftCompile.description = "Compiles the ${sourceSet.name} Thrift source."
            thriftCompile.source = thriftSourceSet.allThrift
            thriftCompile.options = project.thrift

            sourceSet.java.srcDirs += project.file("${project.thrift.outputDir}/gen-java")

            if (project.plugins.hasPlugin('scala')) {
                String scroogeCompileTaskName = sourceSet.getCompileTaskName('scrooge')

                ScroogeCompile scroogeCompile = project.tasks.create(scroogeCompileTaskName, ScroogeCompile)
                scroogeCompile.description = "Compiles the ${sourceSet.name} Thrift source using the scrooge compiler."
                scroogeCompile.source = thriftSourceSet.allThrift
                scroogeCompile.options.outputDir = project.file("${project.thrift.outputDir}/gen-scala")

                sourceSet.scala.srcDirs += project.file("${project.thrift.outputDir}/gen-scala")

                thriftCompile.dependsOn(scroogeCompile)
            }
        }
    }
}
