package kr.a3cctv.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class WebViewActivity extends Activity {

	private static final int REFRESH_INTERVAL = 5 * 60 * 1000;

	private WebView webView;

	private Handler refreshSite = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (webView != null) {
				webView.loadUrl(Util.SERVER_DOMAIN);
				sendEmptyMessageDelayed(0, REFRESH_INTERVAL);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_webview);
		
		setBtn();

		final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);

		webView = (WebView) findViewById(R.id.webview);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings websetting = webView.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		websetting.setBuiltInZoomControls(false);
		websetting.setSaveFormData(false);
		websetting.setSupportZoom(true);

		webView.loadUrl(Util.SERVER_DOMAIN);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}

		});
		// refreshSite.sendEmptyMessageDelayed(0, REFRESH_INTERVAL);
	}

	private void setBtn() {
		ImageButton btn = (ImageButton) findViewById(R.id.btn);
		
		if (Util.isGoogleTV(WebViewActivity.this) || !Util.hasCamera(WebViewActivity.this)) {
			btn.setVisibility(View.GONE);
		} else {
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Util.openCameraAct(WebViewActivity.this);
					finish();
				}
			});
		}
	}
}
