package com.madongfang.mqtt;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttStartAction implements MqttAction {

	public List<MessageUnit> getMessageUnitList() {
		return messageUnitList;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "start";
	}

	@Override
	public void doAction(String deviceId, String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 2)
		{
			logger.warn("mqtt args error");
			return;
		}
		
		int plugId = Integer.valueOf(args[0]);
		int returnCode = Integer.valueOf(args[1]);
		for (MessageUnit messageUnit : messageUnitList) {
			if (deviceId.equals(messageUnit.getDeviceId()) && plugId == messageUnit.getPlugId())
			{
				messageUnit.getMessageQueue().offer(returnCode);
				break;
			}
		}
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<MessageUnit> messageUnitList = new LinkedList<MessageUnit>();
}
