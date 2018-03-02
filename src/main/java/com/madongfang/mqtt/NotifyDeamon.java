package com.madongfang.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.NotifyApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.util.HttpUtil;

public class NotifyDeamon extends Thread {
	
	public NotifyDeamon(String url, NotifyApi notifyApi) {
		super();
		this.url = url;
		this.notifyApi = notifyApi;
	}

	public NotifyDeamon(String url, NotifyApi notifyApi, NotifyReturnProcess notifyReturnProcess) {
		super();
		this.url = url;
		this.notifyApi = notifyApi;
		this.notifyReturnProcess = notifyReturnProcess;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectMapper objectMapper = new ObjectMapper();
		
		int notifyInterval[]  = {1,1,2}; // 发出通知的时间间隔，单位为分钟
		
		int i = 0;
		while (true)
		{
			try {
				String returnString = new HttpUtil().postToString(url, objectMapper.writeValueAsString(notifyApi));
				logger.debug("returnString=" + returnString);
				if (notifyReturnProcess != null)
				{
					if (notifyReturnProcess.process(returnString))
					{
						break;
					}
				}
				else
				{
					ReturnApi returnApi = objectMapper.readValue(returnString, ReturnApi.class);
					if (returnApi != null && returnApi.getReturnCode() == 0)
					{
						break;
					}
					else {
						logger.warn("http return string error: returnString=" + returnString);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("catch Exception:", e);
			}
			
			if (i < notifyInterval.length)
			{
				try {
					sleep(60 * 1000 * notifyInterval[i]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.warn("sleep interrupted:", e);
				}
				i++;
			}
			else
			{
				logger.warn("get notify response failed");
				break;
			}
		}
		
		logger.info("NotifyDeamon stopped");
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String url;
	
	private NotifyApi notifyApi;
	
	private NotifyReturnProcess notifyReturnProcess;
}
