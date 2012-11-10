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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
						super.shouldOverrideUrlLoading(view, "https://appengine.google.com/_ah/conflogin?continue="+Util.SERVER_DOMAIN+"/&auth=" + auth);
					} else {
						return super.shouldOverrideUrlLoading(view, url);
					}
				} else {
					if(url.contains("auth=")){
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
				if (!url.contains("accounts.google.com") && url.contains(Util.SERVER_DOMAIN)) {
					CookieSyncManager.getInstance().sync();
					CookieManager cookieManager = CookieManager.getInstance();
					String cookie = cookieManager.getCookie(Util.SERVER_DOMAIN);
					if (cookie != null) Util.setToken(getApplicationContext(), cookie);
					if (Util.getToken(getApplicationContext()) != null) {
						setResult(RESULT_OK);
						finish();
					}
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
		
		webView.loadUrl(Util.SERVER_DOMAIN);
	}
	
	private void gcmRegister() {
		// 에뮬레이터 등에서는 이용 불가. 체크
		GCMRegistrar.checkDevice(this);
		// permission 등.
		GCMRegistrar.checkManifest(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
			Log.d("regId","regId: "+GCMRegistrar.getRegistrationId(this));
		} else {
			Log.v(MainActivity.TAG, "Already registered");
			Log.d("regId","regId: "+GCMRegistrar.getRegistrationId(this));
		}
		registerDevice(regId, this);
	}
	
	public void registerDevice(final String regId, final Context context) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Util.SERVER_DOMAIN+"/register");
				
				String auth = Util.getToken(context);
				httppost.setHeader("Cookie", auth);
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("regId", regId));
				UrlEncodedFormEntity entityRequest;
				try {
					entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException();
				}
				httppost.setEntity(entityRequest);
				
				HttpParams parameters = httppost.getParams();
				parameters.setParameter("regId", regId);
				
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