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

package br.gov.siop.gradle.thrift.plugin.extensions

/**
 * Defines options for the Scrooge compiler.
 *
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 */
class ScroogeOptions {

    public static final EXTENSION_NAME = 'scrooge'

    /**
     * Output directory for generated files.
     */
    File outputDir

    /**
     * List of directories to search for included files.
     */
    List<File> includeDirs = []

    Map<String, String> namespaceMaps = new HashMap<>()

    /**
     * The default namespace if the thrift file doesn't define its own namespace.
     */
    String defaultJavaNamespace = 'thrift'

    /**
     * Issue warnings on non-severe parse errors instead of aborting.
     */
    boolean disableStrict = false

    /**
     * Path to generate map.txt in to specify the mapping from input thrift files to output Scala/Java files.
     */
    File genFileMap

    /**
     * Parses and validates source thrift files, reporting any errors, but does not emit any generated source code.
     * Can be used with {@link #genFileMap} to get the file mapping.
     */
    boolean dryRun = false

    /**
     * Don't re-generate if the target is newer than the input.
     */
    boolean skipUnchanged = true

    /**
     * Print a warning when the scala generator falls back to the java namespace.
     */
    boolean scalaWarnOnJavaNsFallback = true

    /**
     * Generate finagle classes.
     */
    boolean finagle = false

    def namespaceMap(String oldNamespace, String newNamespace) {
        namespaceMaps << ["${oldNamespace}": newNamespace]
    }
}
