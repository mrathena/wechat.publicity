package com.mrathena.wechat.publicity.entity.response;

/**
 * 回复图片消息
 */
public class ImageMessage extends BaseMessage {

	private Image Image;

	public Image getImage() {
		return Image;
	}

	public void setImage(Image image) {
		Image = image;
	}

}