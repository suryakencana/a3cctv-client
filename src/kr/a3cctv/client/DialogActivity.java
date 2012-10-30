package kr.a3cctv.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class DialogActivity extends Activity {

	private static final int REFRESH_INTERVAL = 3000;

	private boolean isAlreadyMove = false;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gcm);
		
		Button btn = (Button) findViewById(R.id.btn);

		if (Util.isGoogleTV(DialogActivity.this)) {
			btn.setVisibility(View.GONE);
		} else {

			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					moveWebview();
				}
			});

		}

		timer.sendEmptyMessageDelayed(0, REFRESH_INTERVAL);

	}

	private Handler timer = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			moveWebview();

		}
	};

	private void moveWebview() {
		if (!isAlreadyMove) {
			Intent i = new Intent(DialogActivity.this, WebViewActivity.class);
			startActivity(i);
			isAlreadyMove = true;
			finish();
		}
	}

}
