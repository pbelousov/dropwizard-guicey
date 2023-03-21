package ru.vyarus.dropwizard.guice.config.unique

import com.google.inject.Inject
import io.dropwizard.core.Application
import io.dropwizard.core.Configuration
import io.dropwizard.core.ConfiguredBundle
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import ru.vyarus.dropwizard.guice.AbstractTest
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.module.GuiceyConfigurationInfo
import ru.vyarus.dropwizard.guice.module.context.info.DropwizardBundleItemInfo
import ru.vyarus.dropwizard.guice.module.context.info.ItemId
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBootstrap
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle
import ru.vyarus.dropwizard.guice.test.jupiter.TestGuiceyApp

/**
 * @author Vyacheslav Rusakov
 * @since 24.07.2019
 */
@TestGuiceyApp(App)
class MultipleDropwizardBundlesTest extends AbstractTest {

    @Inject
    GuiceyConfigurationInfo info

    def "Check duplicates allowed and equals handling"() {


        expect: "bundle registered 3 times"
        DBundle.executed == 3
        List<DropwizardBundleItemInfo> bundles = info.getInfos(DBundle)
        bundles.size() == 3
        with(bundles[0]) {
            registrationScope == ItemId.from(Application)
            registeredBy == [ItemId.from(Application)] as Set
            registrationAttempts == 1

            getInstance() instanceof DBundle
            getInstanceCount() == 1

            getIgnoresByScope(Application) == 0
            getIgnoresByScope(MiddleBundle) == 0
        }
        with(bundles[1]) {
            registrationScope == ItemId.from(Application)
            registeredBy == [ItemId.from(Application), ItemId.from(MiddleBundle)] as Set
            registrationAttempts == 2

            getInstance() instanceof DBundle
            getInstanceCount() == 2

            getIgnoresByScope(Application) == 0
            getIgnoresByScope(MiddleBundle) == 1
        }
        with(bundles[2]) {
            registrationScope == ItemId.from(MiddleBundle)
            registeredBy == [ItemId.from(MiddleBundle)] as Set
            registrationAttempts == 1

            getInstance() instanceof DBundle
            getInstanceCount() == 3

            getIgnoresByScope(Application) == 0
            getIgnoresByScope(MiddleBundle) == 0
        }
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder()
                    .dropwizardBundles(new DBundle(1), new DBundle(2))
                    .bundles(new MiddleBundle())
                    .build()
            );
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {
        }
    }


    static class DBundle implements ConfiguredBundle {

        static int executed

        int num

        DBundle(int num) {
            this.num = num
        }

        @Override
        void initialize(Bootstrap bootstrap) {
            executed++
        }

        boolean equals(o) {
            return o instanceof DBundle && num == o.num
        }
    }

    static class MiddleBundle implements GuiceyBundle {
        @Override
        void initialize(GuiceyBootstrap bootstrap) {
            bootstrap.dropwizardBundles(new DBundle(2), new DBundle(3))
        }
    }
}
