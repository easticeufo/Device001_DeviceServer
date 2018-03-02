package com.madongfang.mqtt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.StatusNotifyApi;
import com.madongfang.api.StatusNotifyApi.PlugStatus;
import com.madongfang.api.StatusReturnApi;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.MqttUtil;

@Component
public class MqttStatusAction implements MqttAction {

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "status";
	}

	@Override
	public void doAction(String deviceId, String[] args) {
		// TODO Auto-generated method stub
		if (args.length % 2 != 0)
		{
			logger.warn("mqtt args error");
			return;
		}
		
		List<PlugStatus> plugStatusList = new ArrayList<PlugStatus>(args.length/2);
		for (int i = 0; i < args.length/2; i++)
		{
			int k = args[2*i].indexOf(",");
			int remain = 0;
			Integer remainTime = null;
			if (k == -1)
			{
				remain = Integer.valueOf(args[2*i]);
			}
			else
			{
				remain = Integer.valueOf(args[2*i].substring(0, k));
				remainTime = Integer.valueOf(args[2*i].substring(k + 1));
			}
			int power = Integer.valueOf(args[2*i+1]);
			if (power < 0)
			{
				logger.warn("mqtt args error: power={}", power);
				return;
			}
			
			plugStatusList.add(new PlugStatus(remain, power, remainTime));
		}
		
		StatusNotifyApi statusNotifyApi = new StatusNotifyApi(getCommand(), 
				commonUtil.getRandomStringByLength(32), deviceId, plugStatusList);
		statusNotifyApi.setSign(commonUtil.getSign(statusNotifyApi, password));
		new NotifyDeamon(notifyUrl, statusNotifyApi, new NotifyReturnProcess() {
			
			@Override
			public boolean process(String returnString) {
				// TODO Auto-generated method stub
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					StatusReturnApi statusReturnApi = objectMapper.readValue(returnString, StatusReturnApi.class);
					if (statusReturnApi == null 
							|| (statusReturnApi.getReturnCode() == 0 && statusReturnApi.getStatusList() == null))
					{
						logger.warn("http return string error: returnString=" + returnString);
						return false;
					}
					if (statusReturnApi.getReturnCode() == 0)
					{
						StringBuilder stringBuilder = new StringBuilder(getCommand());
						for (String status : statusReturnApi.getStatusList()) {
							stringBuilder.append("#");
							stringBuilder.append(status);
						}
						mqttUtil.publish(deviceId, stringBuilder.toString());
					}
					else {
						logger.warn("returnCode={},returnMsg={}", 
								statusReturnApi.getReturnCode(), statusReturnApi.getReturnMsg());
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
