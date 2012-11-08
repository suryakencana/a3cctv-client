package kr.a3cctv.client.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import kr.a3cctv.client.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class StorageServiceImpl implements StorageService {

	public void saveToSdCard(String filename, byte[] data, Context context)
			throws IOException {
		FileOutputStream fos = null;
		try {
			File targetDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "android-cctv");
			if (!targetDirectory.exists()) {
				targetDirectory.mkdirs();
			}
			fos = new FileOutputStream(targetDirectory + File.separator + filename);
			fos.write(data);
			fos.flush();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fos != null) fos.close();
		}

		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi.isConnected()) {
			saveToAppEngine(filename, Util.getToken(context), data);
		} else {
			// TODO
			// Queue 에 쌓기... WIFI 연결되면 업로드
		}
	}

	public void saveToAppEngine(final String filename, final String auth, final byte[] data)
			throws IOException {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						Util.SERVER_DOMAIN+"/uploadImage?auth="+auth);
				MultipartEntity entity = new MultipartEntity();

				try {
					entity.addPart("location", new StringBody(""));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				entity.addPart("imageFile", new ByteArrayBody(data,
						"image/jpeg", filename));
				httppost.setEntity(entity);
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
		
		task.execute();

	}

}
