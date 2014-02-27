/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.dartmouth.pxxzz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import edu.dartmouth.pxxzz.chatclient.MessageBody;
import edu.dartmouth.pxxzz.chatclient.ServerUtilities;
import edu.dartmouth.pxxzz.helper.Constants;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
    PxxzzApplication pxxzzApplication;
    public GCMIntentService() {
        super(Constants.SENDER_ID);
        pxxzzApplication = (PxxzzApplication)getApplication();
    }
    public String getUsername() {
		return getSharedPreferences("BIO_SIGNIN", MODE_PRIVATE).getString(
				"USERNAME", "");
	}
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.e(TAG, "Device registered: regId = " + registrationId);
        ServerUtilities.register(context, registrationId, getUsername());
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.e(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.e(TAG, "Received message");
		// Get message from the extras, split the message to extract id and operation. 
		// If the operation is "delete", delete the requested exercise entry.
		// Your code.
		Bundle extras=intent.getExtras();
		if (null!=extras){
			String source = (String)extras.get(Constants.EXTRA_GCM_SOURCE);
			int flag = Integer.parseInt((String)extras.get(Constants.EXTRA_GCM_FLAG));
			String target = (String)extras.get(Constants.EXTRA_GCM_TARGET);
			String nickname = (String)extras.get(Constants.EXTRA_GCM_NICKNAME);
			String cheese_img = (String)extras.get(Constants.EXTRA_GCM_CHEESE_IMG);
			String profile_img = (String)extras.get(Constants.EXTRA_GCM_PROFILE_IMG);
			String countdown = (String)extras.get(Constants.EXTRA_GCM_COUNT);
			String timestamp = (String)extras.get(Constants.EXTRA_GCM_DATE);
			String ttl = (String)extras.get(Constants.EXTRA_GCM_TTL);
			
			Log.e("string", "flag"+flag+",source"+source+",target"+target+"nickname,"+nickname+"cheesetime,"+cheese_img+"profile_img,"+profile_img+"timestamp,"+timestamp+","+ttl);
			
			SharedPreferences prefs = getSharedPreferences("check_duplicates", MODE_PRIVATE);

			// Load user name
			String savedTimeStamp = prefs.getString(source, "");
			if (savedTimeStamp.equals("")) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(source, timestamp);
				editor.apply();
			} else {
				if(savedTimeStamp.equals(timestamp)){
					Log.e("there","there is duplicate.");
					return;
				}
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(source, timestamp);
				editor.apply();
			}
			

			
			if (source.equals(getUsername())){
				return;
			}

			if(flag == MessageBody.FLAG_UNLOCK)
			{
				Log.e("GCM","unlock");
				Intent createChatHeadIntent = new Intent(Constants.INTENT_FILTER_DATASET_CHANGED);
				createChatHeadIntent.putExtra(Constants.EXTRA_FRIEND_NAME, target);
				createChatHeadIntent.putExtra(Constants.EXTRA_IMAGE_PATH, profile_img);
				createChatHeadIntent.putExtra("TYPE", MessageBody.FLAG_UNLOCK);
				sendBroadcast(createChatHeadIntent);
				return;
			}
			
			if(flag == MessageBody.FLAG_LOCK){
				Log.e("GCM","lock");
				//Intent dismissChatHeadIntent = new Intent(Config.INTENT_FILTER_DISMISS_CHAT_HEAD);
				Intent dismissChatHeadIntent = new Intent(Constants.INTENT_FILTER_DATASET_CHANGED);
				dismissChatHeadIntent.putExtra(Constants.EXTRA_FRIEND_NAME, target);
				dismissChatHeadIntent.putExtra("TYPE", MessageBody.FLAG_UNLOCK);
				sendBroadcast(dismissChatHeadIntent);
				return;
			}
			if(flag == MessageBody.FLAG_ACCEPT){
				Intent displayImageIntent = new Intent(Constants.INTENT_FILTER_RECEIVE_IMAGE);
				Log.e("gcm", "receive image");
				displayImageIntent.putExtra(Constants.EXTRA_IMAGE_PATH, cheese_img);
				displayImageIntent.putExtra(Constants.EXTRA_GCM_TTL, ttl);
				displayImageIntent.putExtra(Constants.EXTRA_GCM_SOURCE, source);
				Log.e("source in gcm",source+"");
				sendBroadcast(displayImageIntent);
				return;
			}
			if(flag == MessageBody.FLAG_REQUEST){
				Log.e("GCM","request");
				Intent cheeseTimeIntent = new Intent(Constants.INTENT_FILTER_CHEESE_TIME);
				cheeseTimeIntent.putExtra(Constants.EXTRA_FRIEND_NAME, source);
				cheeseTimeIntent.putExtra(Constants.EXTRA_GCM_TTL, ttl);
				Log.e("GCM","broadcast request sent");
				sendBroadcast(cheeseTimeIntent);
				
				return;
			}
			if(flag == MessageBody.FLAG_REJECT){
				Log.e("GCM","reject,"+source);
				Intent rejectIntent = new Intent(Constants.INTENT_FILTER_BUSY_NOTIFICATION);
				rejectIntent.putExtra(Constants.EXTRA_FRIEND_NAME, source);
				sendBroadcast(rejectIntent);
				return;
			}
			
		}
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.e(TAG, "Received deleted messages notification");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.e(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    

}
