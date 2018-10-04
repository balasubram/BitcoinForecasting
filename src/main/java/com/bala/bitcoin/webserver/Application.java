package com.bala.bitcoin.webserver;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

public class Application implements ApplicationInterface {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	private static Injector injector = null;

	private static Application application;

	private void registerShutdownhook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (application != null) {
					application.stop();
				}
			}
		});
	}

	@Override
	public void initializeServices() {
		for (Key<?> key : injector.getAllBindings().keySet()) {
			if (ApplicationInterface.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
				ApplicationInterface applicationService = (ApplicationInterface) injector.getInstance(key);
				applicationService.initializeServices();
			}
		}
	}

	@Override
	public void startProcess() {
		for (Key<?> key : injector.getAllBindings().keySet()) {
			if (ApplicationInterface.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
				ApplicationInterface applicationService = (ApplicationInterface) injector.getInstance(key);
				applicationService.startProcess();
			}
		}
	}

	@Override
	public void stop() {
		for (Key<?> key : injector.getAllBindings().keySet()) {
			if (ApplicationInterface.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
				ApplicationInterface applicationService = (ApplicationInterface) injector.getInstance(key);
				LOG.info("Stopping {}", applicationService);
				applicationService.stop();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		LOG.info("!!!!!!!!!!!!!!!!!!!! Running Curreny Forcasting WebServer !!!!!!!!!!!!!!!!!!!!");
		application = new Application();

		try {
			URL config = ClassLoader.getSystemClassLoader().getResource("log4j.properties");
			PropertyConfigurator.configure(config);
			injector = Guice.createInjector(new Modules());
			application.initializeServices();
			application.startProcess();
			application.registerShutdownhook();
		} catch (Exception e) {
			LOG.error("Exception Occurred while starting the application server {}", e);
			e.printStackTrace();
			if (application != null) {
				application.stop();
			}
		}
	}

	public static <T> T getClassInstance(Class<T> clasz) {
		return injector.getInstance(clasz);
	}

}