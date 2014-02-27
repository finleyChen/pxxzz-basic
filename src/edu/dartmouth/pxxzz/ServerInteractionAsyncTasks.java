package edu.dartmouth.pxxzz;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import edu.dartmouth.pxxzz.chatclient.ChatClient;
import edu.dartmouth.pxxzz.chatclient.ChatMessage;
import edu.dartmouth.pxxzz.chatclient.MessageBody;
import edu.dartmouth.pxxzz.helper.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

public class ServerInteractionAsyncTasks {
	private ChatClient mChatClient;
	private Utils utils;
	private Context mContext;
	PxxzzApplication pxxzzApplication;

	public ServerInteractionAsyncTasks(Context context,
			PxxzzApplication application) {
		mContext = context;
		utils = new Utils(mContext);
		pxxzzApplication = application;
		mChatClient = new ChatClient(pxxzzApplication.getUsername(),
				pxxzzApplication.getPassword());
	}

	public void sendResponse(Boolean accept, String friendName,
			String encodedImage,String ttl) {
		Log.e("send Response:friendName", friendName);
		new SendResponseAsyncTask(accept, friendName, encodedImage,ttl).execute();
	}

	public void sendRequest(String username, int ttl) {
		new SendRequestAsyncTask(username, ttl).execute();
	}

	public void sendUpdates() {
		new SendUpdatesAsyncTask().execute();
	}

	public void loadImage(String url) {
		Log.e("loadImage", "loadImage");
		new LoadImageAsyncTask().execute(url);
	}

	public class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String encodedImageUrlString = urls[0];
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
				imageBitmap = utils.decodeBase64(stringBuilder.toString());
				resizedBitmap = Bitmap.createScaledBitmap(imageBitmap,
						imageBitmap.getWidth() / 2,
						imageBitmap.getHeight() / 2, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resizedBitmap;

		}

		protected void onPostExecute(Bitmap resultBitmap) {
			Log.e("Downloadhead", "FINISHED!!!!!!!!!!!!!!");

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			Intent fullScreenImageIntent = new Intent(mContext,
					FullscreenImageActivity.class);
			fullScreenImageIntent.putExtra("BitmapImageByteArray", byteArray);
			fullScreenImageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(fullScreenImageIntent);
			Log.e("pxxzzApplication.ttl",pxxzzApplication.ttl+"");
			if(pxxzzApplication.ttl==1){
				return;
			}
			CountDownTimer tapCountDown = new CountDownTimer(2000,500){
				public void onTick(long millisUntilFinished) {
					
				}
				public void onFinish() {
					Log.e("jian","jian");
					pxxzzApplication.actionInstance.takeAPicture();
					//finialize whether the clicked time is what #.
					
				}
			}.start();
			
		}
	}

	public class SendResponseAsyncTask extends AsyncTask<Context, Void, Void> {
		private Boolean accept;
		private String encodedImage;
		private String friendName;
		private String ttl;
		
		public SendResponseAsyncTask(Boolean accept, String friendName,
				String encodedImage,String ttl) {
			this.accept = accept;
			this.encodedImage = encodedImage;
			this.friendName = friendName;
			this.ttl=ttl;
		}

		@Override
		protected Void doInBackground(Context... context) {

			ChatMessage responseMessage;

			Log.e("doInBackground", friendName);
			if (accept) {
				responseMessage = new ChatMessage(
						pxxzzApplication.getUsername(), friendName,
						encodedImage, null, null,
						pxxzzApplication.getNickname(), new Date(),
						MessageBody.FLAG_ACCEPT + "",ttl);
				mChatClient.sendMessage(responseMessage);
			} else {
				responseMessage = new ChatMessage(
						pxxzzApplication.getUsername(), friendName,
						encodedImage, null, null,
						pxxzzApplication.getNickname(), new Date(),
						MessageBody.FLAG_REJECT + "","0");
				mChatClient.sendMessage(responseMessage);
			}
			return null;
		}

	};

	public class SendUpdatesAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... username) {
			Log.e("before send", "source is " + pxxzzApplication.getUsername());
			ChatMessage requestBroadcast = new ChatMessage(
					pxxzzApplication.getUsername(), null, null,
					pxxzzApplication.getImageProfileString(),
					pxxzzApplication.getCountDown(),
					pxxzzApplication.getNickname(), new Date(),
					MessageBody.FLAG_UPDATE + "","0");
			mChatClient.sendMessage(requestBroadcast);
			return null;
		}

	};

	// TODO: change timer here.
	public class SendRequestAsyncTask extends AsyncTask<Context, Void, Void> {
		public String username;
		public int ttl;

		public SendRequestAsyncTask(String username, int ttl) {
			this.username = username;
			this.ttl = ttl;
		}

		@Override
		protected Void doInBackground(Context... context) {
			Log.e("before send", "source is " + pxxzzApplication.getUsername());
			ChatMessage requestBroadcast = new ChatMessage(
					pxxzzApplication.getUsername(), username, null, null, null,
					pxxzzApplication.getNickname(), new Date(),
					MessageBody.FLAG_REQUEST + "", ttl+"");
			mChatClient.sendMessage(requestBroadcast);
			return null;
		}
	};

}