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

import org.gradle.api.file.SourceDirectorySet

/**
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 */
interface ThriftSourceSet {

    SourceDirectorySet getThrift()

    ThriftSourceSet thrift(Closure configureThrift)

    SourceDirectorySet getAllThrift()
}
