package edu.dartmouth.pxxzz.chatclient;

import java.util.Date;

import org.json.JSONObject;

import android.util.Log;

public class ChatMessage {
	public ChatMessage(String source, String target, String cheese_image, String profile_image, String countDown, String nickname,  Date timestamp,String flag , String ttl ){
		mUserName = source;
		mFriendName = target; 
		mDate = timestamp;
		mProfileImage = profile_image;
		mNickName = nickname;
		mCountDown = countDown;
		mCheeseImage = cheese_image;
		mFlag = flag;
		mTTL = ttl;
	}

	public String mUserName;
	public String mFriendName;
	public Date mDate;
	public String mFlag;
	public String mProfileImage;
	public String mNickName;
	public String mCheeseImage;
	public String mCountDown;
	public String mTTL;
}
