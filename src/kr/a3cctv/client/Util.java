package kr.a3cctv.client;

import android.content.Context;
import android.content.pm.PackageManager;

public class Util {
	
	public static boolean isGoogleTV(Context context) {
    	return context.getPackageManager().hasSystemFeature("com.google.android.tv");
    }
	
	public static boolean hasCamera(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
