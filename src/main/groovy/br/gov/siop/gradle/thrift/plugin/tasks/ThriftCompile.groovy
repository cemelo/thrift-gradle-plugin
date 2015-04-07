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
import br.gov.siop.gradle.thrift.plugin.extensions.ThriftOptions
import org.gradle.api.GradleException
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
/**
 * Compilation task that uses the thrift compiler.
 *
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 * @since 0.1, 06/04/2015
 */
class ThriftCompile extends SourceTask {

    @Nested
    ThriftOptions options = new ThriftOptions()

    @TaskAction
    public void compile() {
        if (!options.outputDir.exists() && !options.outputDir.mkdirs())
            throw new GradleException(
                    "Could not create thrift output directory: ${options.outputDir.absolutePath}")

        compileAll()
    }

    public void compileAll() {
        if (options.outputDir.exists()) {
            options.outputDir.eachFile { it.delete() }
        } else if (!options.outputDir.mkdirs()) {
            throw new GradleException(
                    "Could not create thrift output directory: ${options.outputDir.absolutePath}")
        }

        source.each { compile(it) }
    }

    public void compile(File sourceFile) {
        logger.info 'Compiling thrift files'

        if ((options.generators - [scala: '']).isEmpty())
            return

        def cmd = [options.compiler, '-o', options.outputDir.absolutePath]
        (options.generators - [scala: '']).each { generator ->
            cmd << '--gen'
            if (!generator.value.trim().empty)
                cmd << generator.key.trim() + ':' + generator.value.trim()
            else
                cmd << generator.key.trim()
        }

        options.includeDirs.each {
            cmd << '-I' << it.absolutePath
        }

        if (options.nowarn) cmd << '-nowarn'
        if (options.verbose) cmd << '-verbose'
        if (options.recurse) cmd << '-recurse'
        if (options.debug) cmd << '-debug'
        if (options.allowNegKeys) cmd << '--allow-neg-keys'
        if (options.allow64bitConsts) cmd << '--allow-64bit-consts'

        cmd << sourceFile.absolutePath

        String command = cmd.join(' ')
        project.logger.info("Compiling thrift interfaces:")
        project.logger.info(command)

        logger.debug "Running the Thrift compiler: ${command}"

        Process process = command.execute()

        if (project.logger.quietEnabled) {
            process.consumeProcessOutput()
            process.waitFor()
        } else {
            process.waitForProcessOutput(System.out as OutputStream, System.err)
        }

        if (process.exitValue() != 0)
            throw new GradleException("Failed to compile ${source}, exit code ${process.exitValue()}")

        (options.generators - [scala: '']).each { generator ->
            File dir = options.outputDir.toPath()
                .resolve("gen-${generator.key}").toFile()
        }

        project.plugins.getPlugin(ThriftPlugin).configureIDEs()
    }
}
