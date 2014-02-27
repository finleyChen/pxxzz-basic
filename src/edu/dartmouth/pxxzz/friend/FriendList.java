package edu.dartmouth.pxxzz.friend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.dartmouth.pxxzz.chatclient.ChatClient;
import edu.dartmouth.pxxzz.helper.Constants;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class FriendList {

	public ArrayList<Friend> mFriendList = new ArrayList<Friend>();
	public ArrayList<Friend> mOnlineFriendList = new ArrayList<Friend>();
	public synchronized void update(ChatClient chatClient, String url) {
		try {
			String jsonResponse = chatClient.sendMessage(url, Constants.MESSAGE_GET_FRIEND_LIST);
			
			fromJSONString(jsonResponse);

			for (Friend friend : mFriendList) {
				Log.e("mfriendlist","mfriendlist");
				//downloadProfileImg(friend);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}
	public int pickOneOnlineFriend(){
		Random randomGenerator = new Random();
		if(mOnlineFriendList.size()==0){
			return -1;
		}
		int randomIndex = randomGenerator.nextInt(mOnlineFriendList.size());
		int pickedIndex=0;
		for(Friend friend:mFriendList){
			if(mOnlineFriendList.get(randomIndex).mUsername.equals(friend.mUsername)){
				return pickedIndex;
			}
			pickedIndex=pickedIndex+1;
		}
		return -1;
	}
	public ArrayList<Friend> getOnlineFriends(){
		return mOnlineFriendList;
	}
	public void fromJSONString(String jsonString) {
		JSONObject value = (JSONObject) JSONValue.parse(jsonString);
		mFriendList.clear();
		mOnlineFriendList.clear();
		
		for (Object keyObj : value.keySet()) {
			JSONObject friendProfile = (JSONObject) value.get(keyObj);
			String username = (String) keyObj;
//			if (friendProfile.containsKey(Constants.EXTRA_GCM_PROFILE_IMG)
//					&& friendProfile.containsKey(Constants.EXTRA_GCM_STATUS)) 
			if(true){
				String imgUrl = (String) friendProfile.get(Constants.EXTRA_GCM_PROFILE_IMG);
				String nickname = (String) friendProfile.get(Constants.EXTRA_GCM_NICKNAME);
				long status = (Long) friendProfile.get(Constants.EXTRA_GCM_STATUS);
				long countDown;
				if(friendProfile.get(Constants.EXTRA_GCM_COUNT)!=null){
					countDown = (Long) friendProfile.get(Constants.EXTRA_GCM_COUNT);
				}
				else{
					Log.e("null","null");
				}
//				int countdown = Integer.valueOf((String)) ;
//				
				Friend friend = new Friend();
				friend.mUsername = username;
				friend.mImgUrl = imgUrl;
				friend.mStatus = (int)status;
				friend.mNickName = nickname;
//				friend.mCountDown = countdown;
//				if(countdown!=null)
//					Log.e("countdown",countdown);	
				downloadProfileImg(friend);
				mFriendList.add(friend);
				if(friend.mStatus==1){
					mOnlineFriendList.add(friend);
					Log.e("online friend", friend.mUsername+","+friend.mNickName);
				}
			}
		}
		Log.e("mOnlineFriendList size", mOnlineFriendList.size()+"");
	}

	public void downloadProfileImg(Friend friend) {
		String encodedImageUrlString = friend.mImgUrl;
		URL encodedImageUrl;
		Bitmap imageBitmap = null;
		Bitmap resizedBitmap = null;
		try {
			encodedImageUrl = new URL(encodedImageUrlString);
			URLConnection connection = encodedImageUrl.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder stringBuilder = new StringBuilder();
			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(inputLine + "\n");
			}
			byte[] decodedByte = Base64.decode(stringBuilder.toString(), Base64.DEFAULT);
			imageBitmap = BitmapFactory.decodeByteArray(decodedByte, 0,
					decodedByte.length);
			resizedBitmap = Bitmap.createScaledBitmap(imageBitmap,
					imageBitmap.getWidth() / 2, imageBitmap.getHeight() / 2,
					false);
			
			friend.mProfileImg = resizedBitmap;
		} catch (Exception ex) {
			Log.e("exception","exception"+ex);
		}
	}
}
