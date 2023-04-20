package ru.vyarus.guicey.gsp

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp
import ru.vyarus.guicey.gsp.views.template.Template

import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * @author Vyacheslav Rusakov
 * @since 29.01.2019
 */
@TestDropwizardApp(value = App, restMapping = "/rest/*")
class RestPathAsIndexPageTest extends AbstractTest {

    def "Check index page as path"() {

        when: "accessing app"
        String res = getHtml("/")
        then: "index page"
        res.contains("root page from rest")
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .extensions(RootPage)
                    .bundles(
                            ServerPagesBundle.builder().build(),
                            ServerPagesBundle.app("app", "/app", "/")
                                    .indexPage("/root/")
                                    .build())
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    @Template
    @Path("app/root")
    static class RootPage {

        @GET
        String get() {
            return "root page from rest"
        }

    }
}
