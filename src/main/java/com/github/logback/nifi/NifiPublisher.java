package com.github.logback.nifi;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.util.StandardDataPacket;

import com.github.logback.nifi.failure.Fallback;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class NifiPublisher implements Publisher {

	private final SiteToSiteClient client;
	private volatile List<ILoggingEvent> messagesList;
	private final Fallback fallbackForError;
	private int pushInterval;

	public NifiPublisher(final SiteToSiteClient client, List<ILoggingEvent> messagesList, int pushInterval,
			Fallback fallbackForError) {
		this.client = client;
		this.messagesList = messagesList;
		this.pushInterval = pushInterval;
		this.fallbackForError = fallbackForError;
	}

	public static DataPacket createDataPacket(String contents) {
		try {
			byte[] bytes = contents.getBytes("UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			return new StandardDataPacket(new HashMap<>(), is, bytes.length);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void publishToNifi(ILoggingEvent event) throws Exception {
		Transaction transaction = client.createTransaction(TransferDirection.SEND);

		final Map<String, String> attrs = new HashMap<>();
		attrs.put("site-to-site", "yes, please!");
		final byte[] bytes = event.getMessage().getBytes();
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		final DataPacket packet = new StandardDataPacket(attrs, bais, bytes.length);
		transaction.send(packet);
		transaction.confirm();
		transaction.complete();
	}

	public void publishToNifi(String message) throws Exception {
		Transaction transaction = client.createTransaction(TransferDirection.SEND);

		final Map<String, String> attrs = new HashMap<>();
		attrs.put("site-to-site", "yes, please!");
		final byte[] bytes = message.getBytes();
		final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		final DataPacket packet = new StandardDataPacket(attrs, bais, bytes.length);
		transaction.send(packet);
		transaction.confirm();
		transaction.complete();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {

			if (messagesList.size() > 0) {
				List<ILoggingEvent> messagesListCopy = new ArrayList<ILoggingEvent>(messagesList) {

					private static final long serialVersionUID = 13255475697462L;
					StringBuilder sb = new StringBuilder();

					@Override
					public String toString() {
						// Append a new line for each logging message.
						this.forEach(event -> {
							sb.append(event.getMessage() + "\n");
						});
						return sb.toString();
					}
				};
				messagesList.clear();
				try {
					publishToNifi(messagesListCopy.toString());
					messagesListCopy.clear();
				} catch (Exception e) {
					// nifi down or port not running exceptions here
					fallbackForError.flush(messagesListCopy);
					e.printStackTrace();
					// need to move to primary and secondary fall back
					// processors
					// primary to a file of specified size
					// if the size is reached and the nifi is still not up, just
					// return true

				}
			}
			try {
				Thread.sleep(pushInterval);
			} catch (Exception e) {
				// TODO need to add fallback
				e.printStackTrace();
			}
		}
	}
}
