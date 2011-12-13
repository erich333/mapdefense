package com.imagine.mapdefense;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public abstract class MapDefenseGameActivity extends BaseGameActivity {

	//540x960=Photon resolution.
    protected int CAMERA_WIDTH = 540;
	protected int CAMERA_HEIGHT = 960;
	protected static final float SPRITE_WIDTH_DP = 160;
	protected static final float SPRITE_HEIGHT_DP = 160;
	protected float densityScaling;
	protected int spriteWidth;
	protected int spriteHeight;
	protected Camera camera;

	@Override
	public Engine onLoadEngine() {
		WindowManager w = getWindowManager(); 
		Display d = w.getDefaultDisplay(); 
		//d.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		this.CAMERA_WIDTH = d.getWidth();
		this.CAMERA_HEIGHT = d.getHeight();

		// Factor to scale DIP with to get screen pixel (factor 1.0 equals 160dpi)
		this.densityScaling = metrics.density;

		// multiply 160dpi-Sprite dimensions with density factor
		this.spriteWidth = Math.round(SPRITE_WIDTH_DP * densityScaling);
		this.spriteHeight = Math.round(SPRITE_HEIGHT_DP * densityScaling);

		this.camera = new Camera(0, 0, this.CAMERA_WIDTH, this.CAMERA_HEIGHT);
		
		RatioResolutionPolicy ratio = new RatioResolutionPolicy(this.CAMERA_WIDTH, this.CAMERA_HEIGHT);

		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				ratio, this.camera).setNeedsSound(true));
	}

	@Override
	public abstract void onLoadResources();

	@Override
	public abstract Scene onLoadScene();

	@Override
	public abstract void onLoadComplete();
}
