package com.madongfang.api;

import java.util.List;

public class StatusReturnApi extends ReturnApi {

	public StatusReturnApi() {
		super();
	}

	public StatusReturnApi(int returnCode, String returnMsg, List<String> statusList) {
		super(returnCode, returnMsg);
		this.statusList = statusList;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	private List<String> statusList;
}
