package com.imagine.mapdefense;

import java.util.concurrent.Callable;

import org.anddev.andengine.audio.music.MusicManager;
import org.anddev.andengine.audio.sound.SoundManager;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.FontManager;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.sensor.accelerometer.AccelerometerSensorOptions;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.sensor.location.ILocationListener;
import org.anddev.andengine.sensor.location.LocationSensorOptions;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationSensorOptions;
import org.anddev.andengine.ui.IGameInterface;
import org.anddev.andengine.util.ActivityUtils;
import org.anddev.andengine.util.AsyncCallable;
import org.anddev.andengine.util.Callback;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.progress.ProgressCallable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;

import com.google.android.maps.MapActivity;

public abstract class MapGameActivity extends MapActivity implements IGameInterface{

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		// ===========================================================
		// Constructors
		// ===========================================================

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		/**
		 * Performs a task in the background, showing a {@link ProgressDialog},
		 * while the {@link Callable} is being processed.
		 * 
		 * @param <T>
		 * @param pTitleResID
		 * @param pMessageResID
		 * @param pErrorMessageResID
		 * @param pCallable
		 * @param pCallback
		 */
		protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback) {
			this.doAsync(pTitleResID, pMessageResID, pCallable, pCallback, null);
		}

		/**
		 * Performs a task in the background, showing a indeterminate {@link ProgressDialog},
		 * while the {@link Callable} is being processed.
		 * 
		 * @param <T>
		 * @param pTitleResID
		 * @param pMessageResID
		 * @param pErrorMessageResID
		 * @param pCallable
		 * @param pCallback
		 * @param pExceptionCallback
		 */
		protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
			ActivityUtils.doAsync(this, pTitleResID, pMessageResID, pCallable, pCallback, pExceptionCallback);
		}

		/**
		 * Performs a task in the background, showing a {@link ProgressDialog} with an ProgressBar,
		 * while the {@link AsyncCallable} is being processed.
		 * 
		 * @param <T>
		 * @param pTitleResID
		 * @param pMessageResID
		 * @param pErrorMessageResID
		 * @param pAsyncCallable
		 * @param pCallback
		 */
		protected <T> void doProgressAsync(final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback) {
			this.doProgressAsync(pTitleResID, pCallable, pCallback, null);
		}

		/**
		 * Performs a task in the background, showing a {@link ProgressDialog} with a ProgressBar,
		 * while the {@link AsyncCallable} is being processed.
		 * 
		 * @param <T>
		 * @param pTitleResID
		 * @param pMessageResID
		 * @param pErrorMessageResID
		 * @param pAsyncCallable
		 * @param pCallback
		 * @param pExceptionCallback
		 */
		protected <T> void doProgressAsync(final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
			ActivityUtils.doProgressAsync(this, pTitleResID, pCallable, pCallback, pExceptionCallback);
		}

		/**
		 * Performs a task in the background, showing an indeterminate {@link ProgressDialog},
		 * while the {@link AsyncCallable} is being processed.
		 * 
		 * @param <T>
		 * @param pTitleResID
		 * @param pMessageResID
		 * @param pErrorMessageResID
		 * @param pAsyncCallable
		 * @param pCallback
		 * @param pExceptionCallback
		 */
		protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final AsyncCallable<T> pAsyncCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
			ActivityUtils.doAsync(this, pTitleResID, pMessageResID, pAsyncCallable, pCallback, pExceptionCallback);
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================

		public static class CancelledException extends Exception {
			private static final long serialVersionUID = -78123211381435596L;
		}
		
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		protected Engine mEngine;
		private WakeLock mWakeLock;
		protected RenderSurfaceView mRenderSurfaceView;
		protected boolean mHasWindowFocused;
		private boolean mPaused;
		private boolean mGameLoaded;

		// ===========================================================
		// Constructors
		// ===========================================================

		@Override
		protected void onCreate(final Bundle pSavedInstanceState) {
			super.onCreate(pSavedInstanceState);
			this.mPaused = true;

			this.mEngine = this.onLoadEngine();

			this.applyEngineOptions(this.mEngine.getEngineOptions());

			this.onSetContentView();
		}

		@Override
		protected void onResume() {
			super.onResume();

			if(this.mPaused && this.mHasWindowFocused) {
				this.doResume();
			}
		}

		@Override
		public void onWindowFocusChanged(final boolean pHasWindowFocus) {
			super.onWindowFocusChanged(pHasWindowFocus);

			if(pHasWindowFocus) {
				if(this.mPaused) {
					this.doResume();
				}
				this.mHasWindowFocused = true;
			} else {
				if(!this.mPaused) {
					this.doPause();
				}
				this.mHasWindowFocused = false;
			}
		}

		@Override
		protected void onPause() {
			super.onPause();

			if(!this.mPaused) {
				this.doPause();
			}
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();

			this.mEngine.interruptUpdateThread();

			this.onUnloadResources();
		}

		@Override
		public void onUnloadResources() {
			if(this.mEngine.getEngineOptions().needsMusic()) {
				this.getMusicManager().releaseAll();
			}
			if(this.mEngine.getEngineOptions().needsSound()) {
				this.getSoundManager().releaseAll();
			}
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public Engine getEngine() {
			return this.mEngine;
		}

		public TextureManager getTextureManager() {
			return this.mEngine.getTextureManager();
		}

		public FontManager getFontManager() {
			return this.mEngine.getFontManager();
		}

		public SoundManager getSoundManager() {
			return this.mEngine.getSoundManager();
		}

		public MusicManager getMusicManager() {
			return this.mEngine.getMusicManager();
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public void onResumeGame() {

		}

		@Override
		public void onPauseGame() {

		}

		// ===========================================================
		// Methods
		// ===========================================================

		private void doResume() {
			if(!this.mGameLoaded) {
				this.onLoadResources();
				final Scene scene = this.onLoadScene();
				this.mEngine.onLoadComplete(scene);
				this.onLoadComplete();
				this.mGameLoaded = true;
			}

			this.mPaused = false;
			this.acquireWakeLock(this.mEngine.getEngineOptions().getWakeLockOptions());
			this.mEngine.onResume();

			this.mRenderSurfaceView.onResume();
			this.mEngine.start();
			this.onResumeGame();
		}

		private void doPause() {
			this.mPaused = true;
			this.releaseWakeLock();

			this.mEngine.onPause();
			this.mEngine.stop();
			this.mRenderSurfaceView.onPause();
			this.onPauseGame();
		}

		public void runOnUpdateThread(final Runnable pRunnable) {
			this.mEngine.runOnUpdateThread(pRunnable);
		}

		protected void onSetContentView() {
			setContentView(R.layout.main);
			this.mRenderSurfaceView = (RenderSurfaceView) findViewById(R.id.render);
//			mRenderSurfaceView.setContext(this);
			// We want an 8888 pixel format because that's required for
	        // a translucent window. And we want a depth buffer.
	        this.mRenderSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
	        this.mRenderSurfaceView.setRenderer(this.mEngine);
	               
	        // Use a surface format with an Alpha channel
	        this.mRenderSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	        
	        //TODO: Figure out what version, etc. we are working with.
//	        // Make sure we are on top otherwise the surface view cuts a hole through
//	        // anything above it in the zorder.
//	        this.mRenderSurfaceView.setZOrderOnTop(true);    


		}

		private void acquireWakeLock(final WakeLockOptions pWakeLockOptions) {
			if(pWakeLockOptions == WakeLockOptions.SCREEN_ON) {
				ActivityUtils.keepScreenOn(this);
			} else {
				final PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
				this.mWakeLock = pm.newWakeLock(pWakeLockOptions.getFlag() | PowerManager.ON_AFTER_RELEASE, "AndEngine");
				try {
					this.mWakeLock.acquire();
				} catch (final SecurityException e) {
					Debug.e("You have to add\n\t<uses-permission android:name=\"android.permission.WAKE_LOCK\"/>\nto your AndroidManifest.xml !", e);
				}
			}
		}

		private void releaseWakeLock() {
			if(this.mWakeLock != null && this.mWakeLock.isHeld()) {
				this.mWakeLock.release();
			}
		}

		private void applyEngineOptions(final EngineOptions pEngineOptions) {
			if(pEngineOptions.isFullscreen()) {
				ActivityUtils.requestFullscreen(this);
			}

			if(pEngineOptions.needsMusic() || pEngineOptions.needsSound()) {
				this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			}

			switch(pEngineOptions.getScreenOrientation()) {
				case LANDSCAPE:
					this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					break;
				case PORTRAIT:
					this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					break;
			}
		}


		protected void enableVibrator() {
			this.mEngine.enableVibrator(this);
		}

		/**
		 * @see {@link Engine#enableLocationSensor(Context, ILocationListener, LocationSensorOptions)}
		 */
		protected void enableLocationSensor(final ILocationListener pLocationListener, final LocationSensorOptions pLocationSensorOptions) {
			this.mEngine.enableLocationSensor(this, pLocationListener, pLocationSensorOptions);
		}

		/**
		 * @see {@link Engine#disableLocationSensor(Context)}
		 */
		protected void disableLocationSensor() {
			this.mEngine.disableLocationSensor(this);
		}

		/**
		 * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener)}
		 */
		protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener) {
			return this.mEngine.enableAccelerometerSensor(this, pAccelerometerListener);
		}

		/**
		 * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener, AccelerometerSensorOptions)}
		 */
		protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener, final AccelerometerSensorOptions pAccelerometerSensorOptions) {
			return this.mEngine.enableAccelerometerSensor(this, pAccelerometerListener, pAccelerometerSensorOptions);
		}

		/**
		 * @see {@link Engine#disableAccelerometerSensor(Context)}
		 */
		protected boolean disableAccelerometerSensor() {
			return this.mEngine.disableAccelerometerSensor(this);
		}

		/**
		 * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener)}
		 */
		protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener) {
			return this.mEngine.enableOrientationSensor(this, pOrientationListener);
		}

		/**
		 * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener, OrientationSensorOptions)}
		 */
		protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener, final OrientationSensorOptions pLocationSensorOptions) {
			return this.mEngine.enableOrientationSensor(this, pOrientationListener, pLocationSensorOptions);
		}

		/**
		 * @see {@link Engine#disableOrientationSensor(Context)}
		 */
		protected boolean disableOrientationSensor() {
			return this.mEngine.disableOrientationSensor(this);
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
}
