package com.madongfang.mqtt;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttService implements MqttCallback {
	
	@Autowired
	public MqttService(MqttStartAction mqttStartAction, MqttStopAction mqttStopAction, 
			MqttStatusAction mqttStatusAction, MqttParamAction mqttParamAction, 
			MqttCardBalanceAction mqttCardBalanceAction, 
			MqttCardChargeAction mqttCardChargeAction, 
			MqttAttachParamAction mqttAttachParamAction) 
	{
		mqttActions = new LinkedList<MqttAction>();
		mqttActions.add(mqttStartAction);
		mqttActions.add(mqttStopAction);
		mqttActions.add(mqttStatusAction);
		mqttActions.add(mqttParamAction);
		mqttActions.add(mqttCardBalanceAction);
		mqttActions.add(mqttCardChargeAction);
		mqttActions.add(mqttAttachParamAction);
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		logger.warn("mqtt connection lost");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		try {
			String payloadString = new String(message.getPayload());
			logger.info(String.format("mqtt receive: topic=%s, payload=%s", topic, payloadString));
		
			String[] params = payloadString.split("#");
			if (params.length < 2)
			{
				logger.warn("mqtt payload error: payload=" + payloadString);
				return;
			}
			
			String deviceId = params[0];
			String command = params[1];
			String[] args = new String[params.length - 2];
			
			for (int i = 0; i < args.length; i++) {
				args[i] = params[i + 2];
			}
			
			for (MqttAction mqttAction : mqttActions) {
				if (mqttAction.getCommand().equals(command))
				{
					mqttAction.doAction(deviceId, args);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("catch Exception:", e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		try {
			logger.info(String.format("mqtt send complete: topic=%s", token.getTopics()[0]));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<MqttAction> mqttActions;
}
