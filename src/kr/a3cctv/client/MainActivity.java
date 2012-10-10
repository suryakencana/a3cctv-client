package kr.a3cctv.client;

import kr.a3cctv.client.camera.Preview;
import android.app.Activity;
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
        
        Preview view = new Preview(this);
        
        setContentView(view);
        
        gcmRegister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void gcmRegister(){
    	//에뮬레이터 등에서는 이용 불가. 체크
        GCMRegistrar.checkDevice(this);
        //permission 등.
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
        	GCMRegistrar.register(this, SENDER_ID);
        } else {
          Log.v(TAG, "Already registered");
        }
    }
}
