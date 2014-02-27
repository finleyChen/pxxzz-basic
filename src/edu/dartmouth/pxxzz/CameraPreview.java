package edu.dartmouth.pxxzz;

import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	PictureCallback jpegCallback;
	private String TAG = "CAMERA";
	private Context mContext;

	public CameraPreview(Context context, Camera camera,
			PictureCallback callback) {
		super(context);
		mContext = context;
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		jpegCallback = callback;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null) {
			return;
		}
		try {

			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			mCamera.setPreviewCallback(previewCallback);
		} catch (IOException e) {
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera == null) {
			return;
		}
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
		}

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mCamera == null) {
			return;
		}
		if (mHolder.getSurface() == null) {
			return;
		}

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
		}
	}

	private int counter = 0;
	private final int TAKE_NUM = 20;
	PreviewCallback previewCallback = new PreviewCallback() {

		public void onPreviewFrame(byte[] data, Camera camera) {
			CameraPreview.this.invalidate();
			camera.addCallbackBuffer(data);
			Log.e("counter",counter+"");
			//TODO stops here. 
			if (counter == TAKE_NUM) {
				Log.e("take","picture");
				mCamera.takePicture(null, null, jpegCallback);
				
			}
			counter++;

		}
	};

}