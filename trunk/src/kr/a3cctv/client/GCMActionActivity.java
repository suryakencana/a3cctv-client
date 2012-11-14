package kr.a3cctv.client;

import java.io.IOException;
import java.util.List;

import kr.a3cctv.client.camera.StorageService;
import kr.a3cctv.client.camera.StorageServiceImpl;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * GCM 수신받은 경고 화면을 띄우고, 사진도 찍음
 * 
 * @author bbangzum
 * 
 */
public class GCMActionActivity extends Activity implements
		SurfaceHolder.Callback {

	private SurfaceView sv = null;
	private SurfaceHolder sHolder = null;
	private Camera mCamera = null;
	private Parameters parameters = null;
	private StorageService storageService = null;

	private PowerManager.WakeLock mWakeLock = null;

	boolean type = false; // true 사진찍기, false 사진업로드 됐단 알람

	// TEST
//	ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gcm);

		type = getIntent().getBooleanExtra("type", false);

//		iv = (ImageView) findViewById(R.id.test);

		TextView title = (TextView) findViewById(R.id.title);
		TextView desc = (TextView) findViewById(R.id.desc);

		wakeUpDevice(GCMActionActivity.this);

		if (type) {
			title.setText(R.string.alert_title_shot);
			desc.setVisibility(View.GONE);
			displayShot();
		} else {
			title.setText(R.string.alert_title_noti);
			displayAlert();
		}

		timer.sendEmptyMessageDelayed(0, Util.TIME_ALERT_MOVE);
	}
	
	

	private void displayShot() {
		storageService = new StorageServiceImpl();
		sv = (SurfaceView) findViewById(R.id.zeroSize);
		sHolder = sv.getHolder();
		sHolder.addCallback(GCMActionActivity.this);
		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void displayAlert() {

		Button btn = (Button) findViewById(R.id.btn);
		if (!Util.isGoogleTV(GCMActionActivity.this)) {

			btn.setVisibility(View.VISIBLE);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					moveWebview();
				}
			});
		}
	}

	private void wakeUpDevice(Context context) {
		Log.v("A3CCTV", "GCM: Wake Up Device");
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("PPLUS_GCM");
		kl.disableKeyguard();
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.FULL_WAKE_LOCK, "A3CCTV_GCM");
		mWakeLock.acquire();

	}

	private Handler timer = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (type) {
				finish();
			} else {
				moveWebview();
			}

		}
	};

	private void moveWebview() {
		Util.openWebAct(GCMActionActivity.this);
		finish();
	}

	@Override
	protected void onDestroy() {
		
		sv = null;
		sHolder = null; 
		
		if (timer.hasMessages(0)) {
			timer.removeMessages(0);
		}

		if (mWakeLock != null) {
			mWakeLock.release();
		}

		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		parameters = mCamera.getParameters();

		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
		for (Camera.Size size : sizes) {
			if (size.width <= 800 || size.height <= 800) {
				parameters.setPictureSize(size.width, size.height);
				parameters.setJpegQuality(70);
				break;
			}
		}

		mCamera.setParameters(parameters);
		mCamera.startPreview();

		Camera.PictureCallback mCall = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

//				Bitmap bmp = BitmapFactory
//						.decodeByteArray(data, 0, data.length);
//				iv.setImageBitmap(bmp);

				try {
					storageService.saveToSdCard(System.currentTimeMillis()
							+ ".jpg", data, GCMActionActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		mCamera.takePicture(null, null, mCall);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

}
