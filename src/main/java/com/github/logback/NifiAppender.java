package com.github.logback;

import java.util.ArrayList;
import java.util.List;

import org.apache.nifi.remote.client.SiteToSiteClient;

import com.github.logback.config.NifiAppenderConfigurations;
import com.github.logback.nifi.NifiPublisher;
import com.github.logback.nifi.failure.DefaultFallBackImpl;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class NifiAppender<E extends ILoggingEvent> extends NifiAppenderConfigurations<E> {

	private SiteToSiteClient client;
	private volatile List<ILoggingEvent> messagesList;

	@Override
	protected void append(E eventObject) {
		messagesList.add(eventObject);
	}

	@Override
	public void start() {
		//validate configuration
		if (!isConfigurationValid())
			return;

		client = new SiteToSiteClient.Builder().url(getUrl()).portIdentifier(getReceiverPortId())
				.portName(getReceiverPortName()).transportProtocol(getSiteToSiteTransportProtocol()).build();
		messagesList = new ArrayList<ILoggingEvent>();
		new Thread(new NifiPublisher(client, messagesList,getPushInterval(),new DefaultFallBackImpl())).start();
		super.start();
	}

}
