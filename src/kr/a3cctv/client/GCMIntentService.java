package kr.a3cctv.client;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	@Override
	protected void onError(Context context, String resId) {
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		showDialog(context, intent);
	}

	@Override
	protected void onRegistered(Context context, String resId) {
		registerToServer(context, resId);
	}

	@Override
	protected void onUnregistered(Context context, String resId) {
		unRegisterToServer(context, resId);
	}

	//우리 서버로 등록.
	private void registerToServer(Context context, String resId){
//		showToast(context, "서버에서 메시지를 받도록 등록되었습니다");
	}
	private void unRegisterToServer(Context context, String resId){
//		showToast(context, "서버에서 메시지를 받지 않게 되었습니다");
	}
	private void showDialog(Context context, Intent intent){
		Intent i = new Intent(context, AlertActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		
	}
	
	private void showToast(Context context, String msg){
//		Log.d("test", msg);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
