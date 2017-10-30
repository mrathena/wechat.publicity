package com.mrathena.wechat.publicity.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrathena.wechat.publicity.common.tool.Kit;
import com.mrathena.wechat.publicity.common.tool.WeChatKit;
import com.mrathena.wechat.publicity.service.WeChatService;

@RestController
public class WeChatController {

	@Autowired
	private WeChatService service;

	Logger log = LoggerFactory.getLogger(WeChatController.class);

	@RequestMapping(value = "entrance", method = RequestMethod.GET)
	public Object entrance(String signature, String timestamp, String nonce, String echostr) {
		if (!Kit.areNotNull(signature, timestamp, nonce, echostr)) {
			return "failure: parameters not incomplete";
		}
		try {
			log.info("Request: signature:{}, timestamp:{}, nonce:{}, echostr:{}", signature, timestamp, nonce, echostr);
			return WeChatKit.checkSignatrue(signature, timestamp, nonce) ? echostr : "failure: signature check failure";
		} catch (Exception e) {
			return "error: " + e.getMessage();
		}
	}

	@RequestMapping(value = "entrance", method = RequestMethod.POST)
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("UTF-8");
		return service.process(request);
	}

}