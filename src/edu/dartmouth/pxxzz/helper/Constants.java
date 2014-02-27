package edu.dartmouth.pxxzz.helper;

import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String CHAT_SERVER_URL = "http://129.170.212.217:8080/BioChatServer";
	public static final String CHAT_SERVER_GET_FRIEND_URL = CHAT_SERVER_URL + "/getFriendsLists.do";
	public static final String POST_MSG_ACTION_NAME = "PostMessage.do";
	public static final String GET_MSG_ACTION_NAME = "GetMessages.do";
	public static final String SENDER_ID = "109935965737";
	public static final String PARAM_NAME_USERNAME = "username";
	public static final String PARAM_NAME_PASSWORD = "password";
	public static final String PARAM_NAME_MESSAGE = "message";
	public static final String EXTRA_GCM_SOURCE = "source";
	public static final String EXTRA_GCM_TARGET = "target";
	public static final String EXTRA_GCM_FLAG = "flag";
	public static final String EXTRA_GCM_TTL = "ttl";
	public static final String EXTRA_GCM_DATE = "timestamp";
	public static final String EXTRA_GCM_PROFILE_IMG = "profile_img";
	public static final String EXTRA_GCM_NICKNAME = "nickname";
	public static final String EXTRA_GCM_CHEESE_IMG = "cheese_img";
	public static final String EXTRA_GCM_STATUS = "status";
	public static final String EXTRA_FRIEND_NAME = "friend_name";
	public static final String EXTRA_IMAGE_PATH = "image_path";
	public static final String EXTRA_ENCODED_IMAGE_PATH = "encodedImagePath";
	public static final String INTENT_FILTER_RECEIVE_IMAGE = "RECEIVE_DISPLAY_IMAGE";
	public static final String INTENT_FILTER_CHEESE_TIME = "CHEESE_TIME";
	public static final String INTENT_FILTER_BUSY_NOTIFICATION = "BUSY_NOTIFICATION";
	public static final String INTENT_FILTER_PHOTO_TAKEN= "PHOTO_TAKEN";
	public static final String INTENT_FILTER_DATASET_CHANGED="DATASET_CHANGED";
	public static final String MESSAGE_GET_SELF_PROFILE= "1";
	public static final String MESSAGE_GET_FRIEND_LIST="0";
	public static final String EXTRA_GCM_COUNT = "countdown";
	
	// Number of columns of Grid View
	public static final int NUM_OF_COLUMNS = 3;

	// Gridview image padding
	public static final int GRID_PADDING = 8; // in dp
	

	// SD card image directory
	public static final String PHOTO_ALBUM = "biorhythm/";

	
	public static final String PHOTO_ALBUM_VIDEO = "biorhythm/video";
	
	public static final String PHOTO_ALBUM_BACK = "biorhythm/thumbnail/back";
	
	public static final String PHOTO_ALBUM_FRONT = "biorhythm/thumbnail/front";
	// supported file formats
	public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
			"png");
	
	public static final String GCM_SEPARATOR = ",";
	
	public static final long HISTORY_REFRESH_INTERVAL = 5 * 1000;
	
    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";
    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";
}
