package com.wackadoo.wackadoo_client.model;

public class ResponseResult {
	private String request;
	private boolean success;
	private int httpStatusCode;
	private String message;
	
	public ResponseResult(String request, boolean success, String responseLine) {
		this.request = request;
		this.success = success;
		
		// get just the status code of the response line
		String[] responseParts = responseLine.split(" ");
		httpStatusCode = Integer.valueOf(responseParts[1]);
	}
	
	public ResponseResult(String request, boolean success, String responseLine, String message) {
		this.request = request;
		this.success = success;
		this.message = message;
		
		// get just the status code of the response line
		String[] responseParts = responseLine.split(" ");
		httpStatusCode = Integer.valueOf(responseParts[1]);
	}

	public String getRequest() {
		return request;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getMessage() {
		return message;
	}
}
