package com.github.logback.nifi.failure;

import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class DefaultFallBackImpl implements Fallback {

	@Override
	public void flush(List<ILoggingEvent> eventsList) {
		System.out.println(eventsList.toString());
	}
}
