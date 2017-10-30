package com.mrathena.wechat.publicity.entity.response;

/**
 * 回复语音消息
 */
public class VoiceMessage extends BaseMessage {

	// 语音
	private Voice Voice;

	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}

}