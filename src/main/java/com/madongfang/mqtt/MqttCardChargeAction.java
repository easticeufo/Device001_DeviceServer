package com.madongfang.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.CardChargeNotifyApi;
import com.madongfang.api.CardChargeReturnApi;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.MqttUtil;

@Component
public class MqttCardChargeAction implements MqttAction {

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "cardCharge";
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
		String cardId = args[1];
		if (plugId <= 0)
		{
			logger.warn("mqtt args error: plugId={}", plugId);
			return;
		}
		
		CardChargeNotifyApi cardChargeNotifyApi = new CardChargeNotifyApi(getCommand(), 
				commonUtil.getRandomStringByLength(32), deviceId, plugId, cardId);
		cardChargeNotifyApi.setSign(commonUtil.getSign(cardChargeNotifyApi, password));
		
		new NotifyDeamon(notifyUrl, cardChargeNotifyApi, new NotifyReturnProcess() {
			
			@Override
			public boolean process(String returnString) {
				// TODO Auto-generated method stub
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					CardChargeReturnApi cardChargeReturnApi = objectMapper.readValue(returnString, CardChargeReturnApi.class);
					if (cardChargeReturnApi == null)
					{
						logger.warn("http return string error: returnString=" + returnString);
						return false;
					}
					
					StringBuilder stringBuilder = new StringBuilder(getCommand());
					stringBuilder.append("#");
					stringBuilder.append(cardId);
					stringBuilder.append("#");
					stringBuilder.append(cardChargeReturnApi.getReturnCode());
					if (cardChargeReturnApi.getReturnCode() >= 0)
					{
						stringBuilder.append("#");
						stringBuilder.append(plugId);
						stringBuilder.append("#");
						stringBuilder.append(cardChargeReturnApi.getLimitPower());
						if (cardChargeReturnApi.getLimitTime() != null)
						{
							stringBuilder.append("#");
							stringBuilder.append(cardChargeReturnApi.getLimitTime());
						}
					}
					else 
					{
						logger.warn("returnCode={},returnMsg={}", 
								cardChargeReturnApi.getReturnCode(), cardChargeReturnApi.getReturnMsg());
					}
					mqttUtil.publish(deviceId, stringBuilder.toString());
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.warn("catch Exception:", e);
					return false;
				}
			}
		}).start();
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${api.password}")
	private String password;
	
	@Value("${api.notifyUrl}")
	private String notifyUrl;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private MqttUtil mqttUtil;
}
