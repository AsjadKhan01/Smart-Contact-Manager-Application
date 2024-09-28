package com.smart.helper;


public class SmartMessage {

	private String content;
	private String type;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public SmartMessage(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public SmartMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "SmartMessage [content=" + content + ", type=" + type + "]";
	}
}
