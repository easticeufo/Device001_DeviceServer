package com.madongfang.mqtt;

import java.util.concurrent.BlockingQueue;

public class MessageUnit {

	public MessageUnit() {
		super();
	}

	public MessageUnit(String deviceId, int plugId, BlockingQueue<Object> messageQueue) {
		super();
		this.deviceId = deviceId;
		this.plugId = plugId;
		this.messageQueue = messageQueue;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getPlugId() {
		return plugId;
	}

	public void setPlugId(int plugId) {
		this.plugId = plugId;
	}

	public BlockingQueue<Object> getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(BlockingQueue<Object> messageQueue) {
		this.messageQueue = messageQueue;
	}

	private String deviceId;
	
	private int plugId;
	
	private BlockingQueue<Object> messageQueue;
}
