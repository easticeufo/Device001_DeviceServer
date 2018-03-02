package com.madongfang.mqtt;

public interface MqttAction {

	public String getCommand();
	
	public void doAction(String deviceId, String[] args);
}
