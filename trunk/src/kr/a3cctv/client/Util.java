package kr.a3cctv.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Util {
	
	public static final String SERVER_DOMAIN = "http://a3-cctv.appspot.com";
	public static final String SHARED_DATA = "a3shared";
	public static final String KEY_TOKEN = "token";
	
	public static final int TIME_ALERT_MOVE = 3000;
	
	private static final String TAKE_PICTURE = "takePicture";
	
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
		String message = (String)intent.getExtras().get("message");
		Log.d("message:", message);
		
		Intent i = new Intent (act, GCMActionActivity.class);
		if (message.equals(TAKE_PICTURE)) {
			i.putExtra("type", true);
		} 
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		act.startActivity(i);
	}
	
	public static boolean checkManifestPermission(Context context, String permissionName){
		if (context.checkCallingOrSelfPermission(permissionName) == PackageManager.PERMISSION_GRANTED){
			return true;
		} else {
			return false;
		}
	}
	
	public static void registerDevice(final String regId, final Context context) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Util.SERVER_DOMAIN+"/register");
				
				String auth = Util.getToken(context);
				httppost.setHeader("Cookie", auth);
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("regId", regId));
				nameValuePairs.add(new BasicNameValuePair("modelName", Build.MODEL));
				UrlEncodedFormEntity entityRequest;
				try {
					entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException();
				}
				httppost.setEntity(entityRequest);
				
				HttpParams parameters = httppost.getParams();
				parameters.setParameter("regId", regId);
				parameters.setParameter("modelName", Build.MODEL);
				
				httppost.setParams(parameters);
				
				HttpResponse response;
				try {
					response = httpclient.execute(httppost);
					Log.d(this.getClass().getSimpleName(), response
							.getStatusLine().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		if (regId != "") task.execute();
	}
}
