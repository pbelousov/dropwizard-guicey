package ru.vyarus.dropwizard.guice.yaml

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.module.yaml.ConfigPath
import ru.vyarus.dropwizard.guice.module.yaml.ConfigTreeBuilder
import ru.vyarus.dropwizard.guice.module.yaml.ConfigurationTree
import ru.vyarus.dropwizard.guice.test.jupiter.TestGuiceyApp
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 04.05.2018
 */
@TestGuiceyApp(App)
class ConfigAccessorsTest extends Specification {

    @Inject
    Bootstrap bootstrap

    def "Check properties visibility"() {

        when: "config without annotations, but with getters"
        def res = ConfigTreeBuilder.build(bootstrap, create(NoAnnsConfig))
        then:
        check(res, 'foo', String)
        check(res, 'bar', Boolean)

        when: "config with inconsistent annotations"
        res = ConfigTreeBuilder.build(bootstrap, create(SimpleConfiguration))
        then:
        check(res, 'foo', String)
        check(res, 'bar', Boolean)

        when: "config with getters only"
        res = ConfigTreeBuilder.build(bootstrap, create(GetterOnlyConfiguration))
        then:
        check(res, 'foo', String)
        check(res, 'bar', Boolean)

        when: "config with setters only"
        res = ConfigTreeBuilder.build(bootstrap, create(SetterOnlyConfiguration))
        then:
        check(res, 'foo', String)
        res.findByPath('bar') == null  // not annotated property not visible
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder().build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {

        }
    }

    private <T extends Configuration> T create(Class<T> type) {
        bootstrap.configurationFactoryFactory
                .create(type, bootstrap.validatorFactory.validator, bootstrap.objectMapper, "dw").build()
    }

    private boolean check(ConfigurationTree config, String path, Class type) {
        ConfigPath item = config.findByPath(path)
        assert item != null
        assert item.valueType == type
        true
    }


    static class NoAnnsConfig extends Configuration {
        String foo
        boolean bar
    }

    static class SimpleConfiguration extends Configuration {

        private String foo;
        private boolean bar;

        String getFoo() {
            return foo
        }

        @JsonProperty
        void setFoo(String foo) {
            this.foo = foo
        }

        @JsonProperty
        boolean getBar() {
            return bar
        }

        void setBar(boolean bar) {
            this.bar = bar
        }
    }

    static class GetterOnlyConfiguration extends Configuration {
        @JsonProperty
        private String foo
        @JsonProperty
        private boolean bar

        String getFoo() {
            return foo
        }

        boolean getBar() {
            return bar
        }
    }

    static class SetterOnlyConfiguration extends Configuration {

        @JsonProperty
        private String foo
        private boolean bar // invisible, because not annotated

        void setFoo(String foo) {
            this.foo = foo
        }

        void setBar(boolean bar) {
            this.bar = bar
        }
    }
}
