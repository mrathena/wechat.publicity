package com.mrathena.wechat.publicity.entity.response;

/**
 * 回复视频消息
 */
public class VideoMessage extends BaseMessage {

	// 视频
	private Video Video;

	public Video getVideo() {
		return Video;
	}

	public void setVideo(Video video) {
		Video = video;
	}

}