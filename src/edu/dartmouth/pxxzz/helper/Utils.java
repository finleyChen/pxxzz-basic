package edu.dartmouth.pxxzz.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.io.FileUtils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

	private Context _context;
	private ArrayList<String> backFilePaths = new ArrayList<String>();
	private ArrayList<String> frontFilePaths = new ArrayList<String>();
	private ArrayList<String> filePaths = new ArrayList<String>();

	public static String toNumeralString(final Boolean input) {
		if (input == null) {
			return "null";
		} else {
			return input.booleanValue() ? "1" : "0";
		}
	}
	public Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	// constructor
	public Utils(Context context) {
		this._context = context;
		backFilePaths = backFilePaths();
		frontFilePaths = frontFilePaths();
		filePaths.addAll(backFilePaths);
		filePaths.addAll(frontFilePaths);
	}

	public ArrayList<String> videoFilePaths() {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ File.separator + Constants.PHOTO_ALBUM_VIDEO);

		// check for directory
		if (directory.isDirectory()) {
			// getting list of file paths
			File[] listFiles = FileUtils.listFiles(directory, null, true)
					.toArray(
							new File[FileUtils.listFiles(directory, null, true)
									.size()]);

			// Check for count
			if (listFiles.length > 0) {
				// loop through all files
				for (int i = 0; i < listFiles.length; i++) {

					// get file path
					String filePath = listFiles[i].getAbsolutePath();

					// check for supported file extension

					// Add image path to array list
					filePaths.add(filePath);

				}
			} else {
				// image directory is empty

			}

		} else {
			// have the object build the directory structure, if needed.
			directory.mkdirs();
		}

		return filePaths;

	}

	public ArrayList<String> frontFilePaths() {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ File.separator + Constants.PHOTO_ALBUM_FRONT);

		// check for directory
		if (directory.isDirectory()) {
			// getting list of file paths
			File[] listFiles = FileUtils.listFiles(directory, null, true)
					.toArray(
							new File[FileUtils.listFiles(directory, null, true)
									.size()]);

			// Check for count
			if (listFiles.length > 0) {
				// loop through all files
				for (int i = 0; i < listFiles.length; i++) {

					// get file path
					String filePath = listFiles[i].getAbsolutePath();

					// check for supported file extension
					if (IsSupportedFile(filePath)) {
						// Add image path to array list
						filePaths.add(filePath);
					}
				}
			} else {
				// image directory is empty

			}

		} else {
			// have the object build the directory structure, if needed.
			directory.mkdirs();
		}

		return filePaths;
	}

	public ArrayList<String> backFilePaths() {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ File.separator + Constants.PHOTO_ALBUM_BACK);

		// check for directory
		if (directory.isDirectory()) {
			// getting list of file paths
			File[] listFiles = FileUtils.listFiles(directory, null, true)
					.toArray(
							new File[FileUtils.listFiles(directory, null, true)
									.size()]);

			// Check for count
			if (listFiles.length > 0) {
				// loop through all files
				for (int i = 0; i < listFiles.length; i++) {

					// get file path
					String filePath = listFiles[i].getAbsolutePath();

					// check for supported file extension
					if (IsSupportedFile(filePath)) {
						// Add image path to array list
						filePaths.add(filePath);
					}
				}
			} else {
				// image directory is empty
			}

		} else {
			// have the object build the directory structure, if needed.
			directory.mkdirs();
		}

		return filePaths;
	}

	public ArrayList<String> getBackFilePaths() {
		return backFilePaths;
	}

	public ArrayList<String> getFrontFilePaths() {
		return frontFilePaths;
	}

	public ArrayList<String> getFilePaths() {
		return filePaths;
	}

	/*
	 * Check supported file extensions
	 * 
	 * @returns boolean
	 */
	private boolean IsSupportedFile(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (Constants.FILE_EXTN
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	/*
	 * getting screen width
	 */
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}
	private Bitmap getRoundBitmap(Bitmap rectBitmap){
		int w = rectBitmap.getWidth();                                          
		int h = rectBitmap.getHeight();                                         

		int radius = Math.min(h / 2, w / 2);                                
		Bitmap roundBitmap = Bitmap.createBitmap(w + 8, h + 8, Config.ARGB_8888);

		Paint p = new Paint();                                              
		p.setAntiAlias(true);                                               

		Canvas c = new Canvas(roundBitmap);                                      
		c.drawARGB(0, 0, 0, 0);                                             
		p.setStyle(Style.FILL);                                             

		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);                  

		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));                 

		c.drawBitmap(rectBitmap, 4, 4, p);                                      
		p.setXfermode(null);                                                
		p.setStyle(Style.STROKE);                                           
		p.setColor(Color.WHITE);                                            
		p.setStrokeWidth(3);                                                
		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);                  

		return roundBitmap;
	}
}
