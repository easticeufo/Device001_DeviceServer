package com.madongfang.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CardChargeReturnApi extends ReturnApi {

	public CardChargeReturnApi() {
		super();
	}

	public CardChargeReturnApi(int returnCode, String returnMsg, Integer limitPower, Integer limitTime) {
		super(returnCode, returnMsg);
		this.limitPower = limitPower;
		this.limitTime = limitTime;
	}

	public Integer getLimitPower() {
		return limitPower;
	}

	public void setLimitPower(Integer limitPower) {
		this.limitPower = limitPower;
	}

	public Integer getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(Integer limitTime) {
		this.limitTime = limitTime;
	}

	private Integer limitPower;
	
	private Integer limitTime;
}
