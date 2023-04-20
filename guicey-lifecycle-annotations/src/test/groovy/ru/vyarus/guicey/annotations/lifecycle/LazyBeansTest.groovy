package ru.vyarus.guicey.annotations.lifecycle

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.test.TestSupport
import spock.lang.Specification

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Singleton

/**
 * @author Vyacheslav Rusakov
 * @since 27.11.2018
 */
class LazyBeansTest extends Specification {


    def "Check lazy initialization"() {

        when: "bean created with JIT and so being initialized AFTER events firing"
        SampleBean bean = TestSupport.runWebApp(App, null) {
            it.getInstance(SampleBean)
        }

        then:
        bean.initialized
        bean.started
        bean.destroyed
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap
                    .addBundle(GuiceBundle.builder().build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    @Singleton
    static class SampleBean {
        boolean initialized
        boolean started
        boolean destroyed

        @PostConstruct
        private void start() {
            initialized = true
        }

        @PostStartup
        private void afterStartup() {
            started = true
        }

        @PreDestroy
        private void stop() {
            destroyed = true
        }
    }
}
