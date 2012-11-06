package kr.a3cctv.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	public static final String TAG = "hackfair";
	
	private static final int REQ_LOGIN = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String token = Util.getToken(getApplicationContext());
		if(token==null){
			Util.showToast(getApplicationContext(), "유저 data 없습니다 회원가입/로그인을 위한 전환."); 
			startActivityForResult(new Intent(this, LoginActivity.class), REQ_LOGIN );
		}
		else{
			Util.showToast(getApplicationContext(), "유저 data 발견. 다음으로 넘어갑니다."); 
			startNextActivity();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
		case REQ_LOGIN :
			break;
		}
	}

	private void startNextActivity(){
		
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

}
