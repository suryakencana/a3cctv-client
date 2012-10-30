package kr.a3cctv.client.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PictureCallback {

	SurfaceHolder holder;
	Camera camera;
	StorageService storageService;
	private OrientationEventListener oel;

	public Preview(Context context) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		storageService = new StorageServiceImpl();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
	}
	
	public void enableOel(){
		oel = new OrientationEventListener(
				getContext()) {

			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation == ORIENTATION_UNKNOWN)
					return;
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(0, info);
				orientation = (orientation + 45) / 90 * 90;
				int rotation = 0;
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else { // back-facing camera
					rotation = (info.orientation + orientation) % 360;
				}
//				if(camera!=null){
					Camera.Parameters parameters = camera.getParameters();
					parameters.setRotation(rotation);
					camera.setParameters(parameters);
//					camera.setDisplayOrientation(rotation);
//				}
			}
		};

		oel.enable();
	}

	public void disableOel(){
		oel.disable();
	}
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		final Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
		camera.startPreview();
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			enableOel();
//			 camera.setDisplayOrientation(90);
		} catch (IOException e) {
			camera.release();
			camera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			storageService.saveToSdCard(System.currentTimeMillis() + ".jpg",
					data, getContext());
		} catch (Exception e) {
			Log.e(VIEW_LOG_TAG, e.toString());
		}

		camera.startPreview();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) shot();
		return true;
	}

	public void takePicture(Camera camera) {
		camera.takePicture(null, null, this);
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		public void onAutoFocus(boolean success, Camera camera) {
			if (success) takePicture(camera);
		}
	};
	
	
	public void shot() {
		Camera.Parameters params = camera.getParameters();
		
		List<Camera.Size> sizes = params.getSupportedPictureSizes();
		for (Camera.Size size : sizes) {
			if (size.width <= 800 || size.height <= 800) {
				params.setPictureSize(size.width, size.height);
				params.setJpegQuality(70);
				break;
			}
		}
		camera.setParameters(params);
		camera.autoFocus(autoFocusCallback);
	}

}
