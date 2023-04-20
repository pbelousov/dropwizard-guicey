package ru.vyarus.dropwizard.guice.examples.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.examples.service.SampleService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;


/**
 * @author Vyacheslav Rusakov
 * @since 31.12.2019
 */
@WebListener
@Singleton
public class SampleRequestListener implements ServletRequestListener {

    private final Logger logger = LoggerFactory.getLogger(SampleRequestListener.class);
    private int calls;

    @Inject
    private SampleService service;

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        logger.info("{} request destroyed", service.listenerPart());
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        logger.info("{} request initiated", service.listenerPart());
        calls++;
    }

    public int getCalls() {
        return calls;
    }
}
