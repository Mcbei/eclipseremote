package com.qmzy;

public class MessageBean {
	private String name;
	private String content;
	private int type;//0正常的一个字符串，1则是语音
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
