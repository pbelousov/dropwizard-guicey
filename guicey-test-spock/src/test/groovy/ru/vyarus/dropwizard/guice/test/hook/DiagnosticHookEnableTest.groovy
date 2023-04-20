package ru.vyarus.dropwizard.guice.test.hook

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.hook.ConfigurationHooksSupport
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 18.08.2019
 */
@UseGuiceyApp(App)
class DiagnosticHookEnableTest extends Specification {

    void cleanup() {
        ConfigurationHooksSupport.reset()
    }

    def "Diagnostic hook enable"() {

        expect:
        true
    }

    static class App extends Application<Configuration> {
        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            System.setProperty(ConfigurationHooksSupport.HOOKS_PROPERTY, "diagnostic")
            bootstrap.addBundle(GuiceBundle.builder().build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }
}
