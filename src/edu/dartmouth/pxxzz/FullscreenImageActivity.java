package edu.dartmouth.pxxzz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FullscreenImageActivity extends Activity {
	public WindowManager rootWindowManager;
	public WindowManager.LayoutParams childWindowManagerParams;
    float x1,x2;
    float y1, y2;
//	public EditText textbox;
	public void initializeParams() {
		childWindowManagerParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		childWindowManagerParams.gravity = Gravity.TOP | Gravity.LEFT;
		childWindowManagerParams.x = 0;
		childWindowManagerParams.y = 0;
		childWindowManagerParams.horizontalMargin=0;
	}
	@Override
	public void onPause(){
		super.onPause();
		//rootWindowManager.removeView(textbox);

	}
	public boolean onTouchEvent(MotionEvent touchevent) 
    {
                 switch (touchevent.getAction())
                 {
                        // when user first touches the screen we get x and y coordinate
                         case MotionEvent.ACTION_DOWN: 
                         {
                             x1 = touchevent.getX();
                             y1 = touchevent.getY();
                             break;
                        }
                         case MotionEvent.ACTION_UP: 
                         {
                             x2 = touchevent.getX();
                             y2 = touchevent.getY(); 

                             if (x1 < x2) 
                             {
                                 Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                                 finish();
                              }
                            
                             // if right to left sweep event on screen
                             if (x1 > x2)
                             {
                                 Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                                 finish();
                             }
                            
                             // if UP to Down sweep event on screen
                             if (y1 < y2) 
                             {
                                 Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                                 finish();
                             }
                            
                             if (y1 > y2)
                             {
                                 Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                              }
                             break;
                         }
                 }
                 return false;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        rootWindowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
        initializeParams();
        byte[] byteArray = getIntent().getByteArrayExtra("BitmapImageByteArray");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        
        ImageView imageView = (ImageView) findViewById(R.id.cheeseimage);
        imageView.setImageBitmap(bmp);
        
        

    }
    
}