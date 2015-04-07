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

package br.gov.siop.gradle.thrift.plugin.tasks
import br.gov.siop.gradle.thrift.plugin.ThriftPlugin
import br.gov.siop.gradle.thrift.plugin.extensions.ScroogeOptions
import org.gradle.api.GradleException
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
/**
 * The Scrooge compiler task.
 *
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 */
class ScroogeCompile extends SourceTask {

    @Nested
    ScroogeOptions options = new ScroogeOptions()

    private void addOutputDirectoryToSourceSet() {

    }

    @TaskAction
    public void compile() {
        logger.info("Compiling Thrift using the scrooge compiler.")

        if (!project.thrift.generators.containsKey('scala'))
            return

        if (!options.outputDir.exists() && !options.outputDir.mkdirs())
            throw new GradleException(
                    "Could not create scrooge output directory: ${options.outputDir.absolutePath}")

        compile(source.toList())
    }

    public void compile(List<File> files) {
        logger.info("Compiling ${files.size()} thrift files.")

        List<String> argsList = ['-d', options.outputDir.absolutePath]

        options.includeDirs.each {
            argsList << '-i' << it.absolutePath
        }

        options.namespaceMaps.each { entry ->
            argsList << '-n' << "${entry.key}=${entry.value}"
        }

        argsList << '--default-java-namespace' << options.defaultJavaNamespace

        if (options.disableStrict) argsList << '--disable-strict'
        if (options.genFileMap) argsList << '--gen-file-map' << options.genFileMap.absolutePath
        if (options.dryRun) argsList << '--dry-run'
        if (options.skipUnchanged) argsList << '--skip-unchanged'
        if (options.scalaWarnOnJavaNsFallback) argsList << '--scala-warn-on-java-ns-fallback'
        if (options.finagle) argsList << '--finagle'

        argsList << '--language' << 'scala'

        files.each {
            argsList << it.absolutePath
        }

        String[] args = new String[argsList.size()]
        argsList.eachWithIndex { String entry, int i ->
            args[i] = entry
        }

        logger.info("Compiling ${files.size()} thrift files.")

        //noinspection UnnecessaryQualifiedReference
        com.twitter.scrooge.Main.main(args)

        if (project.configurations.find({ it.name == 'compile' })) {
            def dependsOnScrooge = project.configurations.compile.allDependencies.find {
                it.group == 'com.twitter' && it.name.startsWith('scrooge-core')
            }

            if (!dependsOnScrooge)
                logger.warn('Your project does not depend on "com.twitter:scrooge-core". ' +
                        'This artifact is required to compile Scrooge generated code.')
        }

        project.plugins.getPlugin(ThriftPlugin).configureIDEs()
    }
}
