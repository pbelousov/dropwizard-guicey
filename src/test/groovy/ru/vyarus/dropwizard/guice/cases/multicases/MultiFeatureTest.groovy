package ru.vyarus.dropwizard.guice.cases.multicases

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.AbstractTest
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.module.GuiceyConfigurationInfo
import ru.vyarus.dropwizard.guice.module.context.info.ExtensionItemInfo
import ru.vyarus.dropwizard.guice.module.installer.CoreInstallersBundle
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.eager.EagerSingleton
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 01.08.2016
 */
@UseGuiceyApp(App)
class MultiFeatureTest extends AbstractTest {

    @Inject
    GuiceyConfigurationInfo info

    def "Check multifeature installation"() {

        expect: "feature installed by only 1 installer"
        def info = info.getData().<ExtensionItemInfo> getInfo(MultiExtension)
        info.getRegisteredBy() == [Application] as Set
        info.installedBy == ManagedInstaller

    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .bundles(new CoreInstallersBundle())
                    .extensions(MultiExtension)
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {

        }
    }

    @EagerSingleton
    static class MultiExtension implements Managed {

        @Override
        void start() throws Exception {

        }

        @Override
        void stop() throws Exception {

        }
    }
}