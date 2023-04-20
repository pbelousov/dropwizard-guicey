package ru.vyarus.guicey.gsp.ext

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBootstrap
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle
import ru.vyarus.dropwizard.guice.test.jupiter.TestDropwizardApp
import ru.vyarus.guicey.gsp.AbstractTest
import ru.vyarus.guicey.gsp.ServerPagesBundle

/**
 * @author Vyacheslav Rusakov
 * @since 06.02.2019
 */
@TestDropwizardApp(value = App, config = 'src/test/resources/conf.yml')
class ExtendedFromGuiceyTest extends AbstractTest {

    def "Check app mapped"() {

        when: "accessing app"
        String res = getHtml("/")
        then: "index page"
        res.contains("overridden sample page")

        when: "accessing direct file"
        res = getHtml("/index.html")
        then: "index page"
        res.contains("overridden sample page")

        when: "accessing resource"
        res = get("/css/style.css")
        then: "css"
        res.contains("/* sample page css */")

        when: "accessing direct template"
        res = getHtml("/template.ftl")
        then: "rendered template"
        res.contains("page: /template.ftl")

        when: "accessing direct ext template"
        res = getHtml("/ext.ftl")
        then: "rendered template"
        res.contains("ext template")
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .bundles(
                            ServerPagesBundle.builder().build(),
                            ServerPagesBundle.app("app", "/app", "/")
                                    .indexPage("index.html")
                                    .build(),
                            new GuiceyBundle() {
                                @Override
                                void initialize(GuiceyBootstrap gb) {
                                    gb.bundles(ServerPagesBundle.extendApp("app")
                                            .attachAssets("/ext")
                                            .build())
                                }
                            })
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }
}
