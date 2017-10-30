package com.mrathena.wechat.publicity.common.tool;

import java.io.InputStream;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrathena.wechat.publicity.entity.response.Article;
import com.mrathena.wechat.publicity.entity.response.ImageMessage;
import com.mrathena.wechat.publicity.entity.response.MusicMessage;
import com.mrathena.wechat.publicity.entity.response.NewsMessage;
import com.mrathena.wechat.publicity.entity.response.TextMessage;
import com.mrathena.wechat.publicity.entity.response.VideoMessage;
import com.mrathena.wechat.publicity.entity.response.VoiceMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class WeChatKit {

	static Logger log = LoggerFactory.getLogger(WeChatKit.class);

	// token, 公众号页面上配置的
	private static final String TOKEN = "fafc240eded1486f8ef9e7c6040910f4";

	// 请求消息类型：文本
	public static final String REQUEST_MESSAGE_TYPE_TEXT = "text";
	// 请求消息类型：图片
	public static final String REQUEST_MESSAGE_TYPE_IMAGE = "image";
	// 请求消息类型：语音
	public static final String REQUEST_MESSAGE_TYPE_VOICE = "voice";
	// 请求消息类型：视频
	public static final String REQUEST_MESSAGE_TYPE_VIDEO = "video";
	// 请求消息类型：小视频
	public static final String REQUEST_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";
	// 请求消息类型：地理位置
	public static final String REQUEST_MESSAGE_TYPE_LOCATION = "location";
	// 请求消息类型：链接
	public static final String REQUEST_MESSAGE_TYPE_LINK = "link";
	// 请求消息类型：事件推送
	public static final String REQUEST_MESSAGE_TYPE_EVENT = "event";

	// 事件类型：subscribe(订阅)
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	// 事件类型：unsubscribe(取消订阅)
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	// 事件类型：scan(用户已关注时的扫描带参数二维码)
	public static final String EVENT_TYPE_SCAN = "SCAN";
	// 事件类型：LOCATION(上报地理位置)
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	// 事件类型：CLICK(自定义菜单)
	public static final String EVENT_TYPE_CLICK = "CLICK";
	// 事件类型：VIEW(自定义菜单)
	public static final String EVENT_TYPE_VIEW = "VIEW";

	// 响应消息类型：文本
	public static final String RESPONSE_MESSAGE_TYPE_TEXT = "text";
	// 响应消息类型：图片
	public static final String RESPONSE_MESSAGE_TYPE_IMAGE = "image";
	// 响应消息类型：语音
	public static final String RESPONSE_MESSAGE_TYPE_VOICE = "voice";
	// 响应消息类型：视频
	public static final String RESPONSE_MESSAGE_TYPE_VIDEO = "video";
	// 响应消息类型：音乐
	public static final String RESPONSE_MESSAGE_TYPE_MUSIC = "music";
	// 响应消息类型：图文
	public static final String RESPONSE_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 检查消息来源是否为微信公众号官方
	 */
	public static boolean checkSignatrue(String signature, String timestamp, String nonce) {
		// 1.将token、timestamp、nonce三个参数进行字典序排序
		String[] array = new String[] { TOKEN, timestamp, nonce };
		Arrays.sort(array);
		// 2. 将三个参数字符串拼接成一个字符串进行sha1加密
		String sortedStr = array[0] + array[1] + array[2];
		MessageDigest md = null;
		String newSignature = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(sortedStr.getBytes());
			newSignature = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}
		// 3.将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return newSignature != null ? newSignature.equals(signature.toUpperCase()) : false;
	}

	/**
	 * 解析微信发来的请求（XML）
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> toMap(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		// 释放资源
		inputStream.close();
		inputStream = null;

		return map;
	}

	/**
	 * 扩展xstream使其支持CDATA
	 */
	private static XStream xstream = new XStream(new XppDriver() {

		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {

				// 对那些xml节点的转换增加CDATA标记 true增加 false反之
				boolean cdata = false;

				@SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {
					if (!name.equals("xml")) {
						char[] arr = name.toCharArray();
						if (arr[0] >= 'a' && arr[0] <= 'z') {
							// arr[0] -= 'a' - 'A';
							// ASCII码，大写字母和小写字符之间数值上差32
							arr[0] = (char) ((int) arr[0] - 32);
						}
						name = new String(arr);
					}

					super.startNode(name, clazz);

				}

				@Override
				public void setValue(String text) {
					if (text != null && !"".equals(text)) {
						// 浮点型判断
						Pattern patternInt = Pattern.compile("[0-9]*(\\.?)[0-9]*");
						// 整型判断
						Pattern patternFloat = Pattern.compile("[0-9]+");
						// 如果是整数或浮点数 就不要加<![CDATA[]]>了
						if (patternInt.matcher(text).matches() || patternFloat.matcher(text).matches()) {
							cdata = false;
						} else {
							cdata = true;
						}
					}
					super.setValue(text);
				}

				protected void writeText(QuickWriter writer, String text) {
					writer.write(cdata ? "<![CDATA[" + text + "]]>" : text);
				}
			};
		}
	});

	/**
	 * 文本消息对象转换成xml
	 */
	public static String toXml(TextMessage message) {
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	/**
	 * 图片消息对象转换成xml
	 */
	public static String toXml(ImageMessage message) {
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	/**
	 * 语音消息对象转换成xml
	 */
	public static String toXml(VoiceMessage message) {
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	/**
	 * 视频消息对象转换成xml
	 */
	public static String toXml(VideoMessage message) {
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	/**
	 * 音乐消息对象转换成xml
	 */
	public static String toXml(MusicMessage message) {
		xstream.alias("xml", message.getClass());
		return xstream.toXML(message);
	}

	/**
	 * 图文消息对象转换成xml
	 */
	public static String toXml(NewsMessage message) {
		xstream.alias("xml", message.getClass());
		xstream.alias("item", Article.class);
		return xstream.toXML(message);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		String s = new String(tempArr);
		return s;
	}

}