package com.mrathena.wechat.publicity.entity.request.event;

/**
 * 自定义菜单事件, 分拉取消息/跳转链接两种情况
 * */
public class MenuEvent extends BaseEvent {

	private String EventKey;

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

}