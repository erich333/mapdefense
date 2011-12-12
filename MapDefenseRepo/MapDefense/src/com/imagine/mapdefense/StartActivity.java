package com.imagine.mapdefense;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class StartActivity extends MapGameActivity {
    private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private static final float SPRITE_WIDTH_DP = 160;
	private static final float SPRITE_HEIGHT_DP = 160;
	private float densityScaling;
	private int spriteWidth;
	private int spriteHeight;
	private Camera camera;
	private BitmapTextureAtlas texture;
	private TextureRegion splashTextureRegion;

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
    public void onLoadResources() {
       this.texture = new BitmapTextureAtlas(512, 512,
          TextureOptions.BILINEAR_PREMULTIPLYALPHA);
       this.splashTextureRegion = BitmapTextureAtlasTextureRegionFactory
          .createFromAsset(this.texture,
          this, "gfx/Splashscreen.png", 0, 0);
       this.mEngine.getTextureManager().loadTexture(this.texture);
    }

    @Override
    public Scene onLoadScene() {
       this.mEngine.registerUpdateHandler(new FPSLogger());
       final Scene scene = new Scene(1);
       /* Center the splash on the camera. */
       final int centerX =
         (CAMERA_WIDTH - this.splashTextureRegion.getWidth()) / 2;
       final int centerY =
            (CAMERA_HEIGHT -
             this.splashTextureRegion.getHeight()) / 2;
       /* Create the sprite and add it to the scene. */
       final Sprite splash = new Sprite(centerX,
             centerY, this.splashTextureRegion);
       scene.getLastChild().attachChild(splash);
       return scene;
    }

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
}