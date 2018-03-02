package com.madongfang.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.madongfang.api.StopNotifyApi;
import com.madongfang.util.CommonUtil;

@Component
public class MqttStopAction implements MqttAction {
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "stop";
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
		int remain = Integer.valueOf(args[1]);
		
		if (plugId <= 0 || remain < 0)
		{
			logger.warn("mqtt args error: plugId={}, remain={}", plugId, remain);
			return;
		}
		
		StopNotifyApi stopNotifyApi = new StopNotifyApi(getCommand(), commonUtil.getRandomStringByLength(32), deviceId, plugId, remain);
		if (args.length >= 3)
		{
			stopNotifyApi.setStopReason(Integer.valueOf(args[2]));
		}
		if (args.length >= 4)
		{
			stopNotifyApi.setStopPower(Integer.valueOf(args[3]));
		}
		if (args.length >= 5)
		{
			stopNotifyApi.setRemainTime(Integer.valueOf(args[4]));
		}
		stopNotifyApi.setSign(commonUtil.getSign(stopNotifyApi, password));
		new NotifyDeamon(notifyUrl, stopNotifyApi).start();

	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${api.password}")
	private String password;
	
	@Value("${api.notifyUrl}")
	private String notifyUrl;
	
	@Autowired
	private CommonUtil commonUtil;
}
