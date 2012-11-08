package kr.a3cctv.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends Activity {
	private static final String SENDER_ID = "1000906162243";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		
		findViews();
	}

	private void findViews(){
		WebView webView = (WebView) findViewById(R.id.login_webview);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//TODO getToken. 으잉.. ㅋㅋ get Param 더 이쁘게 못얻나 ㅋ
				
				if(url.contains("account.google.com")) {
					String auth = Util.getToken(getApplicationContext());
					if (auth != null) {
						Log.d("test", "auth: " + auth);
						super.shouldOverrideUrlLoading(view, "https://appengine.google.com/_ah/conflogin?continue=http://a3-cctv.appspot.com/&auth=" + auth);
					} else {
						return super.shouldOverrideUrlLoading(view, url);
					}
				} else {
					if(url.contains("auth=")){
						String auth = url.substring(url.lastIndexOf("auth=")+5); //마지막 인자가 토큰
						Util.setToken(getApplicationContext(), auth);
						gcmRegister(); //로그인 되면 ~ 리시버들로 등록. 회원가입은 ? ㅎ
						
						setResult(RESULT_OK);
						finish();
					}
				}
				return false;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (!url.contains("accounts.google.com") && url.contains("a3-cctv.appspot.com")) {
					setResult(RESULT_OK);
					finish();
				}
			}
		});
		
		WebSettings websetting = webView.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		websetting.setBuiltInZoomControls(false);
		websetting.setSaveFormData(true);
		websetting.setSavePassword(false);
		websetting.setSupportZoom(false);
		
		webView.loadUrl("http://a3-cctv.appspot.com");
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
			Log.v(MainActivity.TAG, "Already registered");
			Log.d("Ryukw82",GCMRegistrar.getRegistrationId(this));
		}
	}
	
}