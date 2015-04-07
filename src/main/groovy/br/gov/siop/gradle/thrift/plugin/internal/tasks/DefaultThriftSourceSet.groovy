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

package br.gov.siop.gradle.thrift.plugin.internal.tasks
import br.gov.siop.gradle.thrift.plugin.tasks.ThriftSourceSet
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

/**
 * @author carlos.e.melo@planejamento.gov.br[Carlos Eduardo Melo]
 */
class DefaultThriftSourceSet implements ThriftSourceSet {

    final SourceDirectorySet thrift

    final SourceDirectorySet allThrift

    DefaultThriftSourceSet(String displayName, FileResolver fileResolver) {
        thrift = new DefaultSourceDirectorySet("${displayName} Thrift source", fileResolver)
        thrift.filter.include("**/*.thrift")

        allThrift = new DefaultSourceDirectorySet("${displayName} Thrift source", fileResolver)
        allThrift.source(thrift)
        allThrift.filter.include("**/*.thrift")
    }

    @Override
    ThriftSourceSet thrift(Closure configureThrift) {
        ConfigureUtil.configure(configureThrift, getThrift())
        return this
    }
}
