package ru.vyarus.guicey.admin.support

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.guicey.admin.AdminRestBundle

/**
 * @author Vyacheslav Rusakov 
 * @since 08.08.2015
 */
class ManualAdminRestPathApp extends Application<Configuration> {

    @Override
    void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().package.name)
                .bundles(new AdminRestBundle("/rest/*"))
                .build()
        );
    }

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
    }
}
