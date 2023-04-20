package ru.vyarus.dropwizard.guice.cases.hkscope.support.hk

import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyManaged

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.ext.Provider

/**
 * @author Vyacheslav Rusakov
 * @since 19.01.2016
 */
@Provider
@JerseyManaged
class HKContainerRequestFilter implements ContainerRequestFilter {

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {

    }
}
