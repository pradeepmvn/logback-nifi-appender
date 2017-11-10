package com.github.logback.config;

import java.util.Iterator;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.remote.protocol.SiteToSiteTransportProtocol;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

public abstract class NifiAppenderConfigurations<E> extends UnsynchronizedAppenderBase<E>
		implements AppenderAttachable<E> {

	private String url;
	private String receiverPortName;
	private String receiverPortId;

	private String siteToSiteTransportProtocol;

	private String fallbackFilePath;
	private String fallbackFileName;

	private int pushInterval;

	/**
	 * @return the url
	 */
	public String getUrl() {

		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the siteToSiteTransportProtocol
	 */
	public SiteToSiteTransportProtocol getSiteToSiteTransportProtocol() {
		return validateSiteToSiteProtocol(siteToSiteTransportProtocol);
	}

	/**
	 * @param siteToSiteTransportProtocol
	 *            the siteToSiteTransportProtocol to set
	 */
	public void setSiteToSiteTransportProtocol(String siteToSiteTransportProtocol) {
		this.siteToSiteTransportProtocol = siteToSiteTransportProtocol;
	}

	/**
	 * @return the receiverPortName
	 */
	public String getReceiverPortName() {
		return receiverPortName;
	}

	/**
	 * @param receiverPortName
	 *            the receiverPortName to set
	 */
	public void setReceiverPortName(String receiverPortName) {
		this.receiverPortName = receiverPortName;
	}

	/**
	 * @return the receiverPortId
	 */
	public String getReceiverPortId() {
		return receiverPortId;
	}

	/**
	 * @param receiverPortId
	 *            the receiverPortId to set
	 */
	public void setReceiverPortId(String receiverPortId) {
		this.receiverPortId = receiverPortId;
	}

	/**
	 * @return the fallbackFilePath
	 */
	public String getFallbackFilePath() {
		return fallbackFilePath;
	}

	/**
	 * @param fallbackFilePath
	 *            the fallbackFilePath to set
	 */
	public void setFallbackFilePath(String fallbackFilePath) {
		this.fallbackFilePath = fallbackFilePath;
	}

	/**
	 * @return the fallbackFileName
	 */
	public String getFallbackFileName() {
		return fallbackFileName;
	}

	/**
	 * @param fallbackFileName
	 *            the fallbackFileName to set
	 */
	public void setFallbackFileName(String fallbackFileName) {
		this.fallbackFileName = fallbackFileName;
	}

	/**
	 * Validates Protocol with Enum value.
	 * 
	 * @param inputProtocol
	 * @return HTTP by default
	 */
	private SiteToSiteTransportProtocol validateSiteToSiteProtocol(String inputProtocol) {
		if (EnumUtils.isValidEnum(SiteToSiteTransportProtocol.class, inputProtocol)) {
			return SiteToSiteTransportProtocol.valueOf(inputProtocol);
		} else {
			// Protocol not validaed. Using HTTP by default
			return SiteToSiteTransportProtocol.HTTP;
		}
	}

	/**
	 * Preliminary Configuration validation happens here. URL, recieverPortId
	 * and name are the only required params.
	 * 
	 * @return
	 */
	protected boolean isConfigurationValid() {
		boolean noError = true;
		if (StringUtils.isBlank(this.url)) {
			addError("No URL is set for the appender named [\"" + name + "\"].");
			noError = false;
		}
		if (StringUtils.isBlank(this.receiverPortId)) {
			addError("No Receiver PortId (receiverPortId) is set for the appender named [\"" + name + "\"].");
			noError = false;
		}

		if (StringUtils.isBlank(this.receiverPortName)) {
			addError("No Receiver Port Name (receiverPortName) is set for the appender named [\"" + name + "\"].");
			noError = false;
		}
		return noError;
	}

	@Override
	public void detachAndStopAllAppenders() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean detachAppender(Appender<E> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean detachAppender(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Appender<E> getAppender(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttached(Appender<E> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Appender<E>> iteratorForAppenders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAppender(Appender<E> newAppender) {
		// TODO Auto-generated method stub

	}

	/**
	 * Validates run schedule and returns 30 sec (30,000 ms) by default
	 * 
	 * @return the runSchedule
	 */
	public int getPushInterval() {
		return (pushInterval == 0) ? 30000 : pushInterval;
	}

	/**
	 * @param pushInterval
	 *            the pushInterval to set
	 */
	public void setPushInterval(int pushInterval) {
		this.pushInterval = pushInterval;
	}
}
