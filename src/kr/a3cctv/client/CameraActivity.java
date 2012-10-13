package kr.a3cctv.client;

import kr.a3cctv.client.camera.Preview;
import android.app.Activity;
import android.os.Bundle;

public class CameraActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Preview preview = new Preview(this);
		setContentView(preview);
	}

	
	
	
}
