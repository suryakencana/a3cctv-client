package kr.a3cctv.client;

import kr.a3cctv.client.camera.Preview;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class Util {
	
	public static final String SERVER_DOMAIN = "http://a3-cctv.appspot.com";
	public static final String SHARED_DATA = "a3shared";
	public static final String KEY_TOKEN = "token";
	
	public static final int TIME_ALERT_MOVE = 3000;
	
	public static boolean isGoogleTV(Context context) {
    	return context.getPackageManager().hasSystemFeature("com.google.android.tv");
    }
	
	public static boolean hasCamera(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
	public static void showToast(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static String getToken(Context context){
		return loadPref(context).getString(KEY_TOKEN, null);
	}
	
	public static void setToken(Context context, String token){
		Editor pref = loadPref(context).edit(); 
		pref.putString(KEY_TOKEN, token);
		pref.commit();
	}
	public static SharedPreferences loadPref(Context context){
		return context.getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE);
	}
	
	public static void openCameraAct (Activity act) {
		Intent i = new Intent (act, CameraActivity.class);
		act.startActivity(i);
	}
	
	public static void openWebAct (Activity act) {
		Intent i = new Intent (act, WebViewActivity.class);
		act.startActivity(i);
	}
	
	public static void openAlertAct (Context act, Intent intent) {
		Intent i = new Intent (act, GCMActionActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		act.startActivity(i);
	}
	
}
