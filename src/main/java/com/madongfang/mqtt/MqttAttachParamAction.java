package com.madongfang.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.AttachParamNotifyApi;
import com.madongfang.api.AttachParamReturnApi;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.MqttUtil;

@Component
public class MqttAttachParamAction implements MqttAction {

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "attachParam";
	}

	@Override
	public void doAction(String deviceId, String[] args) {
		// TODO Auto-generated method stub
		AttachParamNotifyApi attachParamNotifyApi = new AttachParamNotifyApi(getCommand(), 
				commonUtil.getRandomStringByLength(32), deviceId);
		attachParamNotifyApi.setSign(commonUtil.getSign(attachParamNotifyApi, password));
		
		new NotifyDeamon(notifyUrl, attachParamNotifyApi, new NotifyReturnProcess() {
			
			@Override
			public boolean process(String returnString) {
				// TODO Auto-generated method stub
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					AttachParamReturnApi attachParamReturnApi = objectMapper.readValue(returnString, AttachParamReturnApi.class);
					if (attachParamReturnApi == null)
					{
						logger.warn("http return string error: returnString=" + returnString);
						return false;
					}
					if (attachParamReturnApi.getReturnCode() == 0)
					{	
						mqttUtil.publish(deviceId, String.format("%s#%d", getCommand(), attachParamReturnApi.getFloatChargeTime()));
					}
					else 
					{
						logger.warn("returnCode={},returnMsg={}", 
								attachParamReturnApi.getReturnCode(), attachParamReturnApi.getReturnMsg());
					}
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
