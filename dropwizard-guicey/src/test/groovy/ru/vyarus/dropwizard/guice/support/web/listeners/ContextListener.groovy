package ru.vyarus.dropwizard.guice.support.web.listeners

import ru.vyarus.dropwizard.guice.module.installer.feature.web.AdminContext

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * @author Vyacheslav Rusakov
 * @since 09.08.2016
 */
@AdminContext
@WebListener
class ContextListener implements ServletContextListener{

    @Override
    void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {

    }
}
