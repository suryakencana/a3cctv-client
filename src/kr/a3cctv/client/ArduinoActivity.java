package kr.a3cctv.client;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class ArduinoActivity extends Activity implements Runnable {

	private static final String TAG = "A3CCTV_CLIENT";
	private static final String ACTION_USB_PERMISSION = "A3CCTV.USB_PERMISSION";
	private static final int MESSAGE_ECHO = 1;

	private boolean mPermissionRequestPending = false;
	private UsbManager mUsbManager = null;
	private UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	private PendingIntent mPermissionIntent;

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					// UsbAccessory accessory = UsbManager.getAccessory(intent);
					UsbAccessory accessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				// UsbAccessory accessory = UsbManager.getAccessory(intent);
				UsbAccessory accessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_arduino);

		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}

		// CheckBox ledButton = (CheckBox) findViewById(R.id.ledCheckBox);
		// ledButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (mOutputStream == null) {
		// return;
		// }
		// // Send command to accessory
		// byte[] buffer = new byte[2];
		// buffer[0] = COMMAND_LED;
		// buffer[1] = isChecked ? (byte) 1 : (byte) 0;
		//
		// try {
		// mOutputStream.write(buffer);
		// } catch (IOException e) {
		// Log.e(TAG, "write failed", e);
		// }
		//
		// }
		// });
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mAccessory != null) {
			Toast.makeText(
					this,
					"Can't destroy before unplug accessory. For navigation, use HOME key.",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread listenThread = new Thread(null, this, "accessoryListener");
			listenThread.start();
			Log.d(TAG, "accessory opened");
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
			finish();
		}
	}

	private int count = 0;
	
	/**
	 * Listen accessory's voice
	 */
	@Override
	public void run() {

		int ret = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			i = 0;
			while (i < ret) {
				int len = ret - i;
				
				switch (buffer[i]) {
//				case 0x1:
//					if (len >= 2) {
//						Message m = Message.obtain(mHandler, MESSAGE_ECHO);
//						m.obj = Integer.valueOf(buffer[i + 1]);
//						mHandler.sendMessage(m);
//					}
//					i += 2;
//					break;
//					
				default:
					Message m = Message.obtain(mHandler, MESSAGE_ECHO);
//					m.obj = Integer.valueOf(buffer[i]);
					m.obj = "ret "+ret +"buffer "+buffer[0]+"count "+count;
					count++;
					mHandler.sendMessage(m);
					break;
				}
			}
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_ECHO:
				TextView buttonState = (TextView) findViewById(R.id.tv1);
				buttonState.setText((String)msg.obj);
//				if ((Integer) msg.obj > 0) {
//					buttonState.setText("Echo : ON");
//				} else {
//					buttonState.setText("Echo : OFF");
//				}
				break;
			}
		}
	};
	
	private void showToast(String str) {
		Toast.makeText(ArduinoActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
