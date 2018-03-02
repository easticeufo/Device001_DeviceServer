package com.madongfang.controller;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.ReturnApi;
import com.madongfang.api.StartApi;
import com.madongfang.exception.HttpInternalServerErrorException;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.mqtt.MessageUnit;
import com.madongfang.mqtt.MqttStartAction;
import com.madongfang.util.MqttUtil;

@RestController
@RequestMapping(value="/api/start")
public class StartController {

	@PostMapping
	public ReturnApi start(@RequestBody StartApi startApi)
	{
		String deviceId = startApi.getDeviceId();
		int plugId = startApi.getPlugId();
		int limitPower = 12 * 1000;// 最大12度电
		if (startApi.getLimitPower() != null)
		{
			limitPower = startApi.getLimitPower();
		}
		int limitTime = 12 * 60; // 最大12小时
		if (startApi.getLimitTime() != null)
		{
			limitTime = startApi.getLimitTime();
		}
		
		ReturnApi returnApi = new ReturnApi(0, "OK");
		
		MessageUnit messageUnit = new MessageUnit(deviceId, plugId, new LinkedBlockingQueue<Object>());
		List<MessageUnit> messageUnitList = mqttStartAction.getMessageUnitList();
		messageUnitList.add(messageUnit);
		
		mqttUtil.publish(deviceId, String.format("start#%d#%d#%d", plugId, limitPower, limitTime));
		try {
			Object returnMessage = messageUnit.getMessageQueue().poll(10, TimeUnit.SECONDS); // 阻塞等待10s，若mqtt有对应的响应，则立刻返回
			if (null == returnMessage)
			{
				logger.warn("mqtt响应超时!");
				returnApi.setReturnCode(-10);
				returnApi.setReturnMsg("设备响应超时");
				throw new HttpNotAcceptableException(returnApi);
			}
			else
			{
				int returnCode = (Integer)returnMessage;
				if (returnCode < 0)
				{
					logger.warn("设备返回returnCode=" + returnCode);
					returnApi.setReturnCode(returnCode);
					if (returnCode == -1)
					{
						returnApi.setReturnMsg("超出最大功率");
					}
					else {
						returnApi.setReturnMsg("未知的设备错误");
					}
					throw new HttpNotAcceptableException(returnApi);
				}
				else
				{
					return returnApi;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
			returnApi.setReturnCode(-11);
			returnApi.setReturnMsg("服务器异常");
			throw new HttpInternalServerErrorException(returnApi);
		} finally {
			messageUnitList.remove(messageUnit);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MqttStartAction mqttStartAction;
	
	@Autowired
	private MqttUtil mqttUtil;
}
