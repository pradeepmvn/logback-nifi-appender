package com.github.logback;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import static org.junit.Assert.assertFalse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.status.ErrorStatus;

public class TestNifiAppender {

	private final NifiAppender<ILoggingEvent> nifiAppender = new NifiAppender<ILoggingEvent>();
	private final LoggerContext loggerContext = new LoggerContext();

	@Before
	public void before() {
		loggerContext.setName("testctx");
		loggerContext.setStatusManager(new BasicStatusManager());
		nifiAppender.setContext(loggerContext);
		nifiAppender.setName("NifiAppender");
		nifiAppender.setUrl("http://localhost:8000/nifi/");
		nifiAppender.setReceiverPortName("FromLogback");
		nifiAppender.setReceiverPortId("3f5c5ece-4e8f-1c7f-ffff-ffff8a1f130b");
		nifiAppender.setPushInterval(10);
		nifiAppender.setSiteToSiteTransportProtocol("HTTP");
		loggerContext.start();
	}

	@Test
	public void testPerfectStartAndStop() {
		nifiAppender.start();
		assertTrue("isStarted", nifiAppender.isStarted());
		nifiAppender.stop();
		assertFalse("isStopped", nifiAppender.isStarted());
	}

	@Test
	public void testInvalidNifiURL() {
		nifiAppender.setUrl("http://localhost:1234/nifi/");
		nifiAppender.start();
		assertTrue("isStarted", nifiAppender.isStarted());
	}

	@Test
	public void testAppendOneEvent() {
		nifiAppender.start();
		final LoggingEvent evt = new LoggingEvent("fqcn", loggerContext.getLogger("logger"), Level.ALL,
				"Test MessageToNifi", null, new Object[0]);
		nifiAppender.append(evt);
	}

	@Test
	public void testMEssagesStream() {
		nifiAppender.start();
		int i = 0;
		boolean run=true;
		while (run) {
			LoggingEvent event = new LoggingEvent();
			event.setMessage("{\"message\":\"rest in peace for " + i + " \" }");
			nifiAppender.append(event);
			i++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i > 10)
				run=false;
		}
	}
}
