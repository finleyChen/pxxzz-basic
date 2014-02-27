package edu.dartmouth.pxxzz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import edu.dartmouth.pxxzz.chatclient.ChatClient;
import edu.dartmouth.pxxzz.helper.Constants;
import edu.dartmouth.pxxzz.helper.Utils;

public class TakePicture extends Application
{
	/* Pxxzz Collecting and Uploading */
	private ChatClient mChatClient;
	private Camera mCamera;
	private Utils utils;
	private CameraPreview mPreview;
	private int columnWidth;
	JpegCallback jpegCallback = new JpegCallback();
	private Context mContext;
	
	public TakePicture(Context context){
		mContext = context;
		Resources r = mContext.getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				Constants.GRID_PADDING, r.getDisplayMetrics());
		utils = new Utils(mContext);
		columnWidth = (int) ((utils.getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
	}
	private void writeToFile(String data, String name) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(name, Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	
	private class JpegCallback implements Camera.PictureCallback {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.e("picturetaken","picturetaken");
			stopPreviewAndFreeCamera();
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();

			matrix.postRotate(-90);

			Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0,
					bmp.getWidth(), bmp.getHeight(), matrix, true);
			try {
				// first: generate original files
				long name = System.currentTimeMillis();

				// String saveThumbDir;
				// saveThumbDir = "/sdcard/biorhythm/thumbnail/front/";
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				rotateBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] byteArrayImage = baos.toByteArray();
				String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
				String encodedImagePath = name+".dat";
				writeToFile(encodedImage,encodedImagePath);
				
				
				Intent intent = new Intent(Constants.INTENT_FILTER_PHOTO_TAKEN);
				intent.putExtra(Constants.EXTRA_ENCODED_IMAGE_PATH, encodedImagePath);
				mContext.sendBroadcast(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}
			stopPreviewAndFreeCamera();
		}
	}
	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {
			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inPurgeable = true;
			o.inInputShareable = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inPurgeable = true;
			o2.inInputShareable = true;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void stopPreviewAndFreeCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			if (mPreview != null) {
				mPreview.getHolder().removeCallback(mPreview);
			}
			/*
			 * Important: Call release() to release the camera for use by other
			 * applications. Applications should release the camera immediately
			 * in onPause() (and re-open() it in onResume()).
			 */
			mCamera.release();
			mCamera = null;
		}
	}
	public void takeAPicture()
	{
		initCamera();
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSPARENT);

		params.height = 1;
		params.width = 1;
		Log.e("takeAPicture","takeAPicture1");
		Log.e("takeAPicture","takeAPicture2");
		mPreview = new CameraPreview(mContext, mCamera, jpegCallback);
		Log.e("takeAPicture","takeAPicture3");
		wm.addView(mPreview, params);
		Log.e("takeAPicture","takeAPicture4");
	}
	

	private int findCameraId() {

		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {

				cameraId = i;
				break;
			}
		}

		return cameraId;
	}

	private void initCamera() {
		try {
			int cameraId = findCameraId();
			Log.e("cameraId", cameraId + "");
			mCamera = Camera.open(cameraId);
			if (mCamera == null) {
				return;
			}

		} catch (Exception e) {
			stopPreviewAndFreeCamera();
			return;
		}

		try {
			Camera.Parameters param = mCamera.getParameters();
			param.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			int width = getBestPreviewSize(param).width;
			int height = getBestPreviewSize(param).height;
			param.setPictureSize(width, height);
			mCamera.setParameters(param);
		} catch (Exception e) {
			stopPreviewAndFreeCamera();
		}
	}

	private Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
		int max = 0;
		int index = 0;
		List<Size> supportedSizes = parameters.getSupportedPreviewSizes();
		for (int i = 0; i < supportedSizes.size(); i++) {
			Size s = supportedSizes.get(i);
			int size = s.height * s.width;
			if (size > max) {
				index = i;
				max = size;
			}
		}

		return supportedSizes.get(index);
	}
}