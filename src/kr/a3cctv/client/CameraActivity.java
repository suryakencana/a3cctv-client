package kr.a3cctv.client;

import kr.a3cctv.client.camera.Preview;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity {

	private Preview preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layoutContainer = new RelativeLayout(CameraActivity.this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		preview = new Preview(this);

		ImageButton btn = new ImageButton(CameraActivity.this);
		btn.setImageResource(R.drawable.monitor);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.openWebAct(CameraActivity.this);
				finish();
			}
		});

		layoutContainer.addView(preview, params);

		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		layoutContainer.addView(btn, params);

		setContentView(layoutContainer);

	}

	@Override
	protected void onPause() {
		super.onPause();
		preview.disableOel();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
