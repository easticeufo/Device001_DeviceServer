package com.madongfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.ReturnApi;
import com.madongfang.api.StopApi;
import com.madongfang.util.MqttUtil;

@RestController
@RequestMapping(value="/api/stop")
public class StopController {

	@PostMapping
	public ReturnApi stop(@RequestBody StopApi stopApi)
	{
		mqttUtil.publish(stopApi.getDeviceId(), String.format("stop#%d", stopApi.getPlugId()));
		
		return new ReturnApi(0, "OK");
	}
	
	@Autowired
	private MqttUtil mqttUtil;
}
