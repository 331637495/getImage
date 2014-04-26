package com.demo.networkpic;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button button;
	private EditText editText;
	private static ImageView imageView;
	private MyHandler myHandler ;
	private static final int CHANGE_UI = 1;
	private static final int ERROR = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) this.findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);
		imageView = (ImageView) this.findViewById(R.id.imageView1);
		myHandler = new MyHandler(this);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String path = editText.getText().toString().trim();
				if (TextUtils.isEmpty(path)) {
					Toast.makeText(MainActivity.this, "图片路径不能为空",
							Toast.LENGTH_LONG).show();
				}else {
					new Thread(){
						public void run() {
							try {
								URL url = new URL(path);
								HttpURLConnection conn= (HttpURLConnection) url.openConnection();
								conn.setRequestMethod("GET");
								conn.setConnectTimeout(5000);
								int code = conn.getResponseCode();
								if (code == 200) {
									InputStream is = conn.getInputStream();
									Bitmap bitmap = BitmapFactory.decodeStream(is);
//									imageView.setImageBitmap(bitmap);
									Message msg = Message.obtain();
									msg.what = CHANGE_UI;
									msg.obj = bitmap;
									myHandler.sendMessage(msg);
								}else {
									Message msg = Message.obtain();
									msg.what = ERROR;
									myHandler.sendMessage(msg);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Message msg = Message.obtain();
								msg.what = ERROR;
								myHandler.sendMessage(msg);
							}
						};
					}.start();
				}

			}
		});
	}
	
	private static class MyHandler extends Handler{
//		public MyHandler() {
//		}
//
//		public MyHandler(Looper L) {
//		super(L);
//		}
		
		 WeakReference<MainActivity> mActivity;  
		 
	        MyHandler(MainActivity mActivity){  
	            this.mActivity = new WeakReference<MainActivity>(mActivity);  
	        }
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == CHANGE_UI) {
				Bitmap bitmap = (Bitmap) msg.obj;
				imageView.setImageBitmap(bitmap);
			}else if (msg.what == ERROR) {
//				Toast.makeText(context.getApplicationContext(), "获取图片失败",
//						Toast.LENGTH_LONG).show();
				Toast.makeText(mActivity.get(), "获取图片失败",
						Toast.LENGTH_LONG).show();
			}
		}
		
	}

}
