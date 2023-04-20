package ru.vyarus.guicey.eventbus

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.test.jupiter.TestGuiceyApp
import ru.vyarus.guicey.eventbus.report.EventSubscribersReporter
import ru.vyarus.guicey.eventbus.service.EventSubscribersInfo
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2016
 */
@TestGuiceyApp(App)
class RenderEmptyTest extends Specification {
    @Inject
    EventSubscribersInfo info
    EventSubscribersReporter reporter

    void setup() {
        reporter = new EventSubscribersReporter(info)
    }

    def "Check print"() {

        expect: "not reported"
        reporter.renderReport() == null

    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .bundles(new EventBusBundle().noReport())
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }
}