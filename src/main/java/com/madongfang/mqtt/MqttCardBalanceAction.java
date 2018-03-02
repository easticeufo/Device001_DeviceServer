package com.madongfang.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.CardBalanceNotifyApi;
import com.madongfang.api.CardBalanceReturnApi;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.MqttUtil;

@Component
public class MqttCardBalanceAction implements MqttAction {

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "cardBalance";
	}

	@Override
	public void doAction(String deviceId, String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 1)
		{
			logger.warn("mqtt args error");
			return;
		}
		
		String cardId = args[0];
		CardBalanceNotifyApi cardBalanceNotifyApi = new CardBalanceNotifyApi(getCommand(), 
				commonUtil.getRandomStringByLength(32), deviceId, cardId);
		cardBalanceNotifyApi.setSign(commonUtil.getSign(cardBalanceNotifyApi, password));
		
		new NotifyDeamon(notifyUrl, cardBalanceNotifyApi, new NotifyReturnProcess() {
			
			@Override
			public boolean process(String returnString) {
				// TODO Auto-generated method stub
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					CardBalanceReturnApi cardBalanceReturnApi = objectMapper.readValue(returnString, CardBalanceReturnApi.class);
					if (cardBalanceReturnApi == null)
					{
						logger.warn("http return string error: returnString=" + returnString);
						return false;
					}
					
					StringBuilder stringBuilder = new StringBuilder(getCommand());
					stringBuilder.append("#");
					stringBuilder.append(cardId);
					if (cardBalanceReturnApi.getReturnCode() >= 0)
					{
						stringBuilder.append("#");
						stringBuilder.append(cardBalanceReturnApi.getBalance());
					}
					else 
					{
						stringBuilder.append("#");
						stringBuilder.append(cardBalanceReturnApi.getReturnCode());
						logger.warn("returnCode={},returnMsg={}", 
								cardBalanceReturnApi.getReturnCode(), cardBalanceReturnApi.getReturnMsg());
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
