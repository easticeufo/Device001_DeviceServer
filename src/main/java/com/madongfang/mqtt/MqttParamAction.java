package com.madongfang.mqtt;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.ParamNotifyApi;
import com.madongfang.api.ParamReturnApi;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.MqttUtil;

@Component
public class MqttParamAction implements MqttAction {

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "param";
	}

	@Override
	public void doAction(String deviceId, String[] args) {
		// TODO Auto-generated method stub
		ParamNotifyApi paramNotifyApi = new ParamNotifyApi(getCommand(), 
				commonUtil.getRandomStringByLength(32), deviceId);
		paramNotifyApi.setSign(commonUtil.getSign(paramNotifyApi, password));
		
		new NotifyDeamon(notifyUrl, paramNotifyApi, new NotifyReturnProcess() {
			
			@Override
			public boolean process(String returnString) {
				// TODO Auto-generated method stub
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					ParamReturnApi paramReturnApi = objectMapper.readValue(returnString, ParamReturnApi.class);
					if (paramReturnApi == null 
							|| (paramReturnApi.getReturnCode() == 0 && paramReturnApi.getRemainList() == null))
					{
						logger.warn("http return string error: returnString=" + returnString);
						return false;
					}
					if (paramReturnApi.getReturnCode() == 0)
					{
						StringBuilder stringBuilder = new StringBuilder(getCommand());
						stringBuilder.append("#");
						stringBuilder.append(paramReturnApi.getMaxPlugPower());
						stringBuilder.append("#");
						stringBuilder.append(paramReturnApi.getMaxDevicePower());
						stringBuilder.append("#");
						stringBuilder.append(paramReturnApi.getCardPassword());
						stringBuilder.append("#");
						stringBuilder.append(paramReturnApi.getFactor());
						List<Integer> remainList = paramReturnApi.getRemainList();
						List<Integer> remainTimeList = paramReturnApi.getRemainTimeList();
						for (int i = 0; i < remainList.size(); i++) {
							stringBuilder.append("#");
							stringBuilder.append(remainList.get(i));
							if (remainTimeList != null && i < remainTimeList.size())
							{
								stringBuilder.append(",");
								stringBuilder.append(remainTimeList.get(i));
							}
						}
						mqttUtil.publish(deviceId, stringBuilder.toString());
					}
					else {
						logger.warn("returnCode={},returnMsg={}", 
								paramReturnApi.getReturnCode(), paramReturnApi.getReturnMsg());
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
