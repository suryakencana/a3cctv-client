package kr.a3cctv.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	public static final String TAG = "hackfair";
	public static final String SENDER_ID = "1000906162243";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gcmRegister();
		
		if (Util.isGoogleTV(this) && !Util.hasCamera(this)) {
			//Only viewer
			Intent i = new Intent(this, WebViewActivity.class);
			startActivity(i);
		} else {
			//
			Intent i = new Intent(this, CameraActivity.class);
			startActivity(i);
		}
		
		finish();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void gcmRegister() {
		// 에뮬레이터 등에서는 이용 불가. 체크
		GCMRegistrar.checkDevice(this);
		// permission 등.
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
			Log.d("Ryukw82",GCMRegistrar.getRegistrationId(this));
		}
	}
}
