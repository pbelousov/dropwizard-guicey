package ru.vyarus.guicey.gsp.error

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp
import ru.vyarus.guicey.gsp.AbstractTest
import ru.vyarus.guicey.gsp.ServerPagesBundle
import ru.vyarus.guicey.gsp.views.template.Template

import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * @author Vyacheslav Rusakov
 * @since 29.01.2019
 */
@TestDropwizardApp(value = App, restMapping = "/rest/*")
class RestPathAsErrorPageTest extends AbstractTest {

    def "Check index page as path"() {

        when: "accessing not existing asset"
        def res = getHtml("/notexisting.html")
        then: "error page"
        res.contains("error page from rest")
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .extensions(ErrorPage)
                    .bundles(
                            ServerPagesBundle.builder().build(),
                            ServerPagesBundle.app("app", "/app", "/")
                                    .errorPage("/error/")
                                    .build())
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    @Template
    @Path("app/error")
    static class ErrorPage {

        @GET
        String get() {
            return "error page from rest"
        }

    }
}
