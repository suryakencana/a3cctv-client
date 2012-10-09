package kr.a3cctv.client.camera;

import java.io.IOException;

import android.content.Context;

public interface StorageService {

	public void saveToSdCard(String filename, byte[] data, Context context) throws IOException;
	
}
