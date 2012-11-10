package kr.a3cctv.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class AlertActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gcm);

		Button btn = (Button) findViewById(R.id.btn);

		if (Util.isGoogleTV(AlertActivity.this)) {

			btn.setVisibility(View.GONE);

		} else {

			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					moveWebview();
				}
			});

		}

		timer.sendEmptyMessageDelayed(0, Util.TIME_ALERT_MOVE);

	}

	private Handler timer = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			moveWebview();

		}
	};

	private void moveWebview() {
		Util.openWebAct(AlertActivity.this);
		finish();
	}

	@Override
	protected void onDestroy() {
		if (timer.hasMessages(0)) {
			timer.removeMessages(0);
		}
		super.onDestroy();
	}

}
