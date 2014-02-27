package edu.dartmouth.pxxzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import net.simonvt.numberpicker.NumberPicker;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.dartmouth.pxxzz.chatclient.ChatClient;
import edu.dartmouth.pxxzz.friend.Friend;
import edu.dartmouth.pxxzz.helper.Constants;

public class FriendListActivity extends Activity {
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	private ArrayList<ChatHead> chatHeadList;
	private PxxzzApplication pxxzzApplication;
	private String mProfileImgUrl;
	private String mNickname;
	public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
	public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
	public static final int REQUEST_CODE_CROP_PHOTO = 2;

	private static final String IMAGE_UNSPECIFIED = "image/*";
	private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
	private NotificationManager mNotificationManager;
	private Context mContext;
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private TextView mProfileNameTextView;
	private TextView mCheeseTimeTextView;
	NumberPicker mNumberPicker;
	private boolean isTakenFromCamera;
	private Notification mNotification;
	private CountDownTimer countDown;
	private CountDownTimer tapCountDown;
	private int clickedPosition=-1;
	private int clickedTimes=1;
	private boolean buttonDisabled = false;
	private int[] notificationIconArray = { R.drawable.one, R.drawable.two,
			R.drawable.three, R.drawable.four, R.drawable.five };

	private class UpdateMyProfileTask extends AsyncTask<Void, Void, Bitmap> {
		protected Bitmap doInBackground(Void... none) {
			ChatClient chatClient = new ChatClient(
					pxxzzApplication.getUsername(),
					pxxzzApplication.getPassword());
			String jsonResponse = chatClient.sendMessage(
					Constants.CHAT_SERVER_GET_FRIEND_URL,
					Constants.MESSAGE_GET_SELF_PROFILE);
			if (jsonResponse == null || jsonResponse.equals("null")) {
				Log.e("jsonResponse", "return null");
				return null;
			}
			Log.e("jsonResponse", jsonResponse);
			fromJSONString(jsonResponse);
			if(mProfileImgUrl==null || mNickname == null){
				return null;
			}
			URL encodedImageUrl;
			Bitmap resizedBitmap = null;
			try {
				encodedImageUrl = new URL(mProfileImgUrl);
				URLConnection connection = encodedImageUrl.openConnection();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder stringBuilder = new StringBuilder();
				while ((inputLine = bufferedReader.readLine()) != null) {
					stringBuilder.append(inputLine + "\n");
				}
				byte[] decodedByte = Base64.decode(stringBuilder.toString(),
						Base64.DEFAULT);
				Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByte,
						0, decodedByte.length);
				resizedBitmap = Bitmap.createScaledBitmap(imageBitmap,
						imageBitmap.getWidth() / 2,
						imageBitmap.getHeight() / 2, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resizedBitmap;
		}

		protected void onPostExecute(Bitmap profileImg) {
			if (profileImg == null) {
				mProfileNameTextView.setText("");
				mImageView.setImageResource(R.drawable.default_avatar);
			} else {
				mImageView.setImageBitmap(profileImg);
				mProfileNameTextView.setText(mNickname);
				saveProfileName();
				saveProfileImage();
			}
		}
	}

	private class UpdateFriendListTask extends
			AsyncTask<Void, Void, ArrayList<ChatHead>> {
		protected ArrayList<ChatHead> doInBackground(Void... none) {
			Log.e("FriendListActivity", "update friend list");
			ChatClient chatClient = new ChatClient(
					pxxzzApplication.getUsername(),
					pxxzzApplication.getPassword());

			pxxzzApplication.mFriendList.update(chatClient,
					Constants.CHAT_SERVER_GET_FRIEND_URL);

			ArrayList<ChatHead> tmpChatHeadList = new ArrayList<ChatHead>();
			for (Friend friend : pxxzzApplication.mFriendList.mFriendList) {
				ImageView imgView = new ImageView(FriendListActivity.this);
				imgView.setImageBitmap(friend.mProfileImg);
				ChatHead head = new ChatHead(friend.mUsername, imgView, 0,
						pxxzzApplication.getWindowManager(),
						FriendListActivity.this, pxxzzApplication);
				head.status = friend.mStatus;
				head.nickname = friend.mNickName;
				tmpChatHeadList.add(head);
			}

			return tmpChatHeadList;
		}

		protected void onPostExecute(ArrayList<ChatHead> tmpChatHeadList) {
			customGridAdapter.clear();
			customGridAdapter.addAll(tmpChatHeadList);
			customGridAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(datasetChangedReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("register receiver","register receiver");
		registerReceiver(datasetChangedReceiver, new IntentFilter(
				Constants.INTENT_FILTER_DATASET_CHANGED));
		new UpdateFriendListTask().execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add:
			// TODO:
			return true;
		case R.id.random:
			int randomFriend = pxxzzApplication.mFriendList
					.pickOneOnlineFriend();
			Log.e("randomFriend", randomFriend + "");
			if (randomFriend == -1) {
				CharSequence text = "no one is available:(";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(mContext, text, duration);
				toast.show();
				return true;
			}
			customGridAdapter.sendRequest(randomFriend, pxxzzApplication,2);
			Log.e("sendTo", randomFriend + "");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private BroadcastReceiver datasetChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("onReceive", "onReceive");
			if (intent.getAction().equals(
					Constants.INTENT_FILTER_DATASET_CHANGED)) {
				Log.e("onReceive", "onReceive pass intent filter");
				new UpdateFriendListTask().execute();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		mNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
		mNumberPicker.setMaxValue(10);
		mNumberPicker.setMinValue(0);
		mNumberPicker.setFocusable(true);
		mNumberPicker.setFocusableInTouchMode(true);
		mNumberPicker.setVisibility(View.GONE);

		pxxzzApplication = (PxxzzApplication) getApplication();
		chatHeadList = pxxzzApplication.getChatHeadList();
		gridView = (GridView) findViewById(R.id.gridView);
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid,
				chatHeadList);
		gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				if(clickedPosition ==-1)
				{
					clickedPosition=position;
					clickedTimes=clickedTimes+1;
					tapCountDown = new CountDownTimer(500,500){
						public void onTick(long millisUntilFinished) {
							
						}
						public void onFinish() {
							//finialize whether the clicked time is what #.
							Log.e("clickedTimes",clickedTimes+"");
							customGridAdapter.sendRequest(position, pxxzzApplication, clickedTimes);
							clickedPosition = -1;
							clickedTimes=1;
						}
					}.start();
				}
				else{
					if(position==clickedPosition){
						clickedTimes=clickedTimes+1;
					}
				}
//				if (buttonDisabled)
//					return;
//				if (customGridAdapter.getItem(position).status==0)
//				{
//					return;
//				}
//					
//				
//				buttonDisabled = true;
//				countDown = new CountDownTimer(6000, 1000) {
//
//					public void onTick(long millisUntilFinished) {
//						if (mNotificationManager != null) {
//							mNotificationManager.cancel(0);
//						}
//
//						int secondRemaining = (int) (millisUntilFinished / 1000);
//
//						mNotification = new Notification.Builder(
//								getApplicationContext())
//								.setContentTitle(
//										"Continuous Facial Photo Collecting")
//								.setContentText("Pxxzz 1.3")
//								.setSmallIcon(
//										notificationIconArray[secondRemaining - 1])
//								.build();
//						mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//						mNotificationManager.notify(0, mNotification);
//					}
//
//					public void onFinish() {
//						mNotificationManager.cancel(0);
//						buttonDisabled = false;
//					}
//				}.start();
//
//				Toast.makeText(FriendListActivity.this, "Waiting",
//						Toast.LENGTH_SHORT).show();
				

			}

		});
		mImageView = (ImageView) findViewById(R.id.imageProfile);
		mProfileNameTextView = (TextView) findViewById(R.id.nickname);
		mCheeseTimeTextView = (TextView) findViewById(R.id.cheesetimetext);
		if (savedInstanceState != null) {
			mImageCaptureUri = savedInstanceState
					.getParcelable(URI_INSTANCE_STATE_KEY);
		}

		
		// Load the previously saved profile if there is one
		loadProfile();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the image capture uri before the activity goes into background
		outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
	}

	// ****************** button click callbacks ***************************//

	public void onNameEdited(String string) {
		mProfileNameTextView.setText(string);
		saveProfileName();
		new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
		.sendUpdates();
	}

	public void onCheeseTimeClicked(View v) {

		if (mNumberPicker.getVisibility() == View.VISIBLE) {
			mNumberPicker.setVisibility(View.GONE);
			mCheeseTimeTextView.setVisibility(View.VISIBLE);
			mCheeseTimeTextView.setText(String.valueOf(mNumberPicker.getValue()));
//			saveCheeseTime();
//			new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
//			.sendUpdates();
		} else {
			mNumberPicker.setVisibility(View.VISIBLE);
			mCheeseTimeTextView.setVisibility(View.GONE);
		}

	}

	public void onChangePhotoClicked(View v) {
		// changing the profile image, show the dialog asking the user
		// to choose between taking a picture and picking from gallery
		// Go to MyRunsDialogFragment for details.
		displayDialog(PxxzzDialogFragment.DIALOG_ID_PHOTO_PICKER);
	}

	public void onPhotoEditClicked(View v) {
		displayDialog(PxxzzDialogFragment.DIALOG_ID_EDIT_NAME);
	}

	// Handle date after activity returns.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case REQUEST_CODE_TAKE_FROM_CAMERA:
			// Send image taken from camera for cropping
			cropImage();
			break;

		case REQUEST_CODE_SELECT_FROM_GALLERY:
			// Send selected image from gallery for cropping
			mImageCaptureUri = data.getData();
			cropImage();
			break;

		case REQUEST_CODE_CROP_PHOTO:
			// Update image view after image crop
			Bundle extras = data.getExtras();
			// Set the profile image in UI
			if (extras != null) {
				mImageView
						.setImageBitmap((Bitmap) extras.getParcelable("data"));
			}

			// Delete temporary image taken by camera after crop.
			if (isTakenFromCamera) {
				File f = new File(mImageCaptureUri.getPath());
				if (f.exists())
					f.delete();
			}
			saveProfileImage();
			new ServerInteractionAsyncTasks(mContext, pxxzzApplication)
			.sendUpdates();
			break;
		}
	}

	// ******* Photo picker dialog related functions ************//

	public void displayDialog(int id) {
		DialogFragment fragment = PxxzzDialogFragment.newInstance(id);
		fragment.show(getFragmentManager(),
				getString(R.string.dialog_fragment_tag_photo_picker));
	}

	public void onPhotoPickerItemSelected(int item) {
		Intent intent;

		switch (item) {

		case PxxzzDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
			// Take photo from camera???
			// Construct an intent with action
			// MediaStore.ACTION_IMAGE_CAPTURE
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Construct temporary image path and name to save the taken
			// photo
			mImageCaptureUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "tmp_"
					+ String.valueOf(System.currentTimeMillis()) + ".png"));
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			try {
				// Start a camera capturing activity
				// REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
				// defined to identify the activity in onActivityResult()
				// when it returns
				startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			isTakenFromCamera = true;
			break;

		case PxxzzDialogFragment.ID_PHOTO_PICKER_FROM_GALLERY:
			// Select from gallery
			intent = new Intent();
			intent.setType(IMAGE_UNSPECIFIED);
			intent.setAction(Intent.ACTION_GET_CONTENT);
			// Start a gallery choosing activity
			// REQUEST_CODE_SELECT_FROM_GALLERY is an integer tag you
			// defined to identify the activity in onActivityResult()
			// when it returns
			startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_GALLERY);
			break;

		default:
			return;
		}

	}

	private void loadProfile() {
		loadProfileImage();
		loadProfileName();
		loadCheeseDuration();
		if (!loadProfileImage() && !loadProfileName()) {
			Log.e("updateMyPrfile","updateMyProfile");
			new UpdateMyProfileTask().execute();
		}
	}

	public void fromJSONString(String jsonString) {
		JSONObject value = (JSONObject) JSONValue.parse(jsonString);
		if(value==null || value.get(Constants.EXTRA_GCM_PROFILE_IMG)==null){
			return;
		}
		mProfileImgUrl = value.get(Constants.EXTRA_GCM_PROFILE_IMG).toString();
		mNickname = value.get(Constants.EXTRA_GCM_NICKNAME).toString();
	}

	private boolean loadProfileImage() {
		// Load profile photo from internal storage
		try {
			FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
			Bitmap bmap = BitmapFactory.decodeStream(fis);
			mImageView.setImageBitmap(bmap);
			fis.close();
			return true;
		} catch (IOException e) {
			// Default profile photo if no photo saved before.
			mImageView.setImageResource(R.drawable.default_avatar);
			return false;
		}
	}

	private boolean loadProfileName() {
		String key, str_val;
		// Load and update all profile views
		key = getString(R.string.preference_name);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

		// Load user name
		key = getString(R.string.preference_key_profile_name);
		str_val = prefs.getString(key, "");
		mProfileNameTextView.setText(str_val);
		if (str_val.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	private boolean loadCheeseDuration()
	{
		String key, str_val;
		// Load and update all profile views
		key = getString(R.string.preference_cheesetime);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);

		// Load user name
		key = getString(R.string.preference_key_profile_cheese);
		str_val = prefs.getString(key, "");
		mCheeseTimeTextView.setVisibility(View.VISIBLE);
		mCheeseTimeTextView.setText(str_val);
		if (str_val.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	private void saveCheeseTime() {
		String key, str_val;
		key = getString(R.string.preference_cheesetime);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// Write screen contents into corresponding editor fields.
		key = getString(R.string.preference_key_profile_cheese);
		str_val = mCheeseTimeTextView.getText().toString();
		Log.e("str_val",str_val);
		editor.putString(key, str_val);
		editor.apply();
	}

	private void saveProfileName() {
		String key, str_val;
		key = getString(R.string.preference_name);
		SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// Write screen contents into corresponding editor fields.
		key = getString(R.string.preference_key_profile_name);
		str_val = mProfileNameTextView.getText().toString();
		editor.putString(key, str_val);
		editor.apply();
	}

	private void saveProfileImage() {

		// Save profile image into internal storage.
		mImageView.buildDrawingCache();
		Bitmap bmap = mImageView.getDrawingCache();
		try {
			FileOutputStream fos = openFileOutput(
					getString(R.string.profile_photo_file_name), MODE_PRIVATE);
			bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// Crop and resize the image for profile
	private void cropImage() {
		// Use existing crop activity.
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

		// Specify image size
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);

		// Specify aspect ratio, 1:1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		// REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
		// identify the activity in onActivityResult() when it returns
		startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);
	}
}
