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
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//TODO getToken. 으잉.. ㅋㅋ get Param 더 이쁘게 못얻나 ㅋ
				
				if(url.contains("https://accounts.google.com/ServiceLogin?service=ah&passive=true&continue=https://appengine.google.com/_ah/conflogin%3Fcontinue%3Dhttp://a3-cctv.appspot.com/&ltmpl=gm&shdf=ChoLEgZhaG5hbWUaDkFwcGVuZ2luZSBDQ1RWDBICYWgiFHWpS6DcbatCqOUbYKk30Bb5vagGKAEyFBzu0qd8MMheFVXA1HDJSSum7nD6")){
					String token =  url.substring(url.lastIndexOf("=")); //마지막 인자가 토큰
					Log.d("test", "url : " + url +  ", token " + token);
					if(token!=null){
						//Util.setToken(getApplicationContext(), token);
						gcmRegister(); //로그인 되면 ~ 리시버들로 등록. 회원가입은 ? ㅎ
					}
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
			
		});
		
		WebSettings websetting = webView.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		websetting.setBuiltInZoomControls(false);
		websetting.setSaveFormData(true);
		websetting.setSavePassword(false);
		websetting.setSupportZoom(false);
		
		webView.loadUrl("https://docs.google.com/presentation/d/1-L5Tmckg3c5CIB8_cyBUmkeSoos1C9Ue1nyOBlROiZo/edit");
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
