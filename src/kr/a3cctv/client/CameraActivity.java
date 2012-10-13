package kr.a3cctv.client;

import kr.a3cctv.client.camera.Preview;
import android.app.Activity;
import android.os.Bundle;

public class CameraActivity extends Activity {

	private Preview preview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preview = new Preview(this);
		setContentView(preview);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		preview.disableOel();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	
}
