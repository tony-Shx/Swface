package com.henu.swface.VO;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/4/9.
 */

public class UserLogin extends BmobUser {
	private String QQ;
	private String WeChat;
	private String Weibo;

	public UserLogin() {
	}

	public String getQQ() {
		return QQ;
	}

	public void setQQ(String QQ) {
		this.QQ = QQ;
	}

	public String getWeChat() {
		return WeChat;
	}

	public void setWeChat(String weChat) {
		WeChat = weChat;
	}

	public String getWeibo() {
		return Weibo;
	}

	public void setWeibo(String weibo) {
		Weibo = weibo;
	}
}
