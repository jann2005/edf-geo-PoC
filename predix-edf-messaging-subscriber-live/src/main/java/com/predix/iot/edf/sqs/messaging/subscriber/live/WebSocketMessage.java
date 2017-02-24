package com.predix.iot.edf.sqs.messaging.subscriber.live;

public class WebSocketMessage {

	//private String temperature;
	private Long electricValue;
	private Long gasValue;
	private Long timeStamp;
	
	public Long getElectricValue() {
		return electricValue;
	}
	public void setElectricValue(Long electricValue) {
		this.electricValue = electricValue;
	}
	public Long getGasValue() {
		return gasValue;
	}
	public void setGasValue(Long gasValue) {
		this.gasValue = gasValue;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
