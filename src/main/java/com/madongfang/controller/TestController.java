package com.madongfang.controller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.AttachParamReturnApi;
import com.madongfang.api.CardBalanceReturnApi;
import com.madongfang.api.CardChargeReturnApi;
import com.madongfang.api.NotifyApi;
import com.madongfang.api.ParamReturnApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.api.StatusReturnApi;
import com.madongfang.util.HttpUtil;

@RestController
@RequestMapping(value="/api/test")
public class TestController {

	@PostMapping(value="/notify")
	public ReturnApi testNotify(HttpServletRequest request) {
		ObjectMapper objectMapper = new ObjectMapper();
		ReturnApi returnApi = new ReturnApi(0, "OK");
		
		try {
			String httpBody = httpUtil.getBody(request);
			logger.debug("httpBody=" + httpBody);
			
			NotifyApi notifyApi = objectMapper.readValue(httpBody, NotifyApi.class);
			
			switch (notifyApi.getType()) {
			case "stop":
				break;
				
			case "status":
				returnApi = new StatusReturnApi(0, "OK", Arrays.asList("F", "U", "R"));
				break;
				
			case "param":
				returnApi = new ParamReturnApi(0, "OK", 600, 1500, "FFFFFFFFFFFF", 1000, Arrays.asList(0, 33, 100));
				break;
				
			case "cardBalance":
				returnApi = new CardBalanceReturnApi(0, "OK", 1024);
				break;
				
			case "cardCharge":
				returnApi = new CardChargeReturnApi(0, "OK", 2000, 60 * 12);
				break;
				
			case "attachParam":
				returnApi = new AttachParamReturnApi(0, "OK", 60);
				break;
				
			default:
				logger.warn("unknown notify type:" + notifyApi.getType());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("catch Exception:", e);
		}
		
		return returnApi;
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	HttpUtil httpUtil;
}
