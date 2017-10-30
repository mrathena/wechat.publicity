package com.mrathena.wechat.publicity.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mrathena.wechat.publicity.common.tool.WeChatKit;
import com.mrathena.wechat.publicity.entity.response.TextMessage;

@Service
public class WeChatService {

	Logger log = LoggerFactory.getLogger(WeChatService.class);

	public String process(HttpServletRequest request) {
		String resXml = null;
		try {
			Map<String, String> requestMap = WeChatKit.toMap(request);
			log.info("Request: \n{}", requestMap.toString());
			String fromUserName = requestMap.get("FromUserName");
			String toUserName = requestMap.get("ToUserName");
			String msgType = requestMap.get("MsgType");

			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(WeChatKit.RESPONSE_MESSAGE_TYPE_TEXT);

			String resContent = "未知的消息类型！";
			if (WeChatKit.REQUEST_MESSAGE_TYPE_TEXT.equals(msgType)) {
				// 文本消息
				resContent = "文本消息: " + requestMap.get("Content");
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_IMAGE.equals(msgType)) {
				// 图片消息
				resContent = "图片消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_VOICE.equals(msgType)) {
				// 语音消息
				resContent = "语音消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_VIDEO.equals(msgType)) {
				// 视频消息
				resContent = "视频消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_SHORTVIDEO.equals(msgType)) {
				// 小视频消息
				resContent = "小视频消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_LOCATION.equals(msgType)) {
				// 位置消息
				resContent = "位置消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_LINK.equals(msgType)) {
				// 链接消息
				resContent = "链接消息";
			} else if (WeChatKit.REQUEST_MESSAGE_TYPE_EVENT.equals(msgType)) {
				// 事件消息
				String eventType = requestMap.get("Event");
				if (eventType.equals(WeChatKit.EVENT_TYPE_SUBSCRIBE)) {
					// 关注
					resContent = "谢谢您的关注！";
				} else if (eventType.equals(WeChatKit.EVENT_TYPE_UNSUBSCRIBE)) {
					// 取消关注
					resContent = "取消关注";
				} else if (eventType.equals(WeChatKit.EVENT_TYPE_SCAN)) {
					// 扫描带参数二维码
					resContent = "扫描带参数二维码";
				} else if (eventType.equals(WeChatKit.EVENT_TYPE_LOCATION)) {
					// 上报地理位置
					resContent = "上报地理位置";
				} else if (eventType.equals(WeChatKit.EVENT_TYPE_CLICK)) {
					// 自定义菜单
					resContent = "自定义菜单点击";
				} else if (eventType.equals(WeChatKit.EVENT_TYPE_VIEW)) {
					// 自定义菜单
					resContent = "自定义菜单点击";
				}
			} else {
				// 未知类型消息
				resContent = "未知类型消息";
			}
			textMessage.setContent(resContent);
			resXml = WeChatKit.toXml(textMessage);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Response: \n{}", resXml);
		return resXml;
	}

}