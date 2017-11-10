package com.github.logback.nifi.failure;

import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Fallback {

	public void flush(List<ILoggingEvent> eventsList);
}
