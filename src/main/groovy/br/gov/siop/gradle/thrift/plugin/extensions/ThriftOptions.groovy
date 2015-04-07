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
 * Defines options for the Thrift compiler.
 *
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 */
class ThriftOptions {

    public static final EXTENSION_NAME = 'thrift'

    /**
     * Generators to use.
     *
     * @see http://thrift.apache.org[Thrift]
     */
    Map<String, String> generators = [:]

    /**
     * List of directories to search for included files.
     */
    List<File> includeDirs = []

    /**
     * Output directory for generated files.
     */
    File outputDir

    /**
     * The compiler executable path.
     */
    String compiler = '/usr/local/bin/thrift'

    /**
     * Whether to suppress all compiler warnings.
     */
    boolean nowarn = false

    /**
     * Whether to activate verbose mode.
     */
    boolean verbose = false

    /**
     * Whether to generate included files also.
     */
    boolean recurse = false

    /**
     * Whether to activate debug mode.
     */
    boolean debug = false

    /**
     * Whether to allow negative field keys. It is used to preserve protocol
     * compatibility with older `.thrift` files.
     */
    boolean allowNegKeys = false

    /**
     * Whether to print warnings about using 64-bit constants.
     */
    boolean allow64bitConsts = false
}
