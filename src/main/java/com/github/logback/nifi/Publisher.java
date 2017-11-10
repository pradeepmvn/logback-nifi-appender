package com.github.logback.nifi;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Publisher extends Runnable{

	public void publishToNifi(ILoggingEvent event) throws Exception;
	public void publishToNifi(String message) throws Exception;
}
