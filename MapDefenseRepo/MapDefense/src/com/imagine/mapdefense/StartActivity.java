package com.imagine.mapdefense;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Intent;
import android.os.Handler;

public class StartActivity extends MapDefenseGameActivity {
	
	private BitmapTextureAtlas texture;
	private TextureRegion splashTextureRegion;
	
	
    @Override
	public Engine onLoadEngine() {
    	return super.onLoadEngine();		
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
       scene.setBackground(new ColorBackground(0,0,0,.0f));
       return scene;
    }

	@Override
	public void onLoadComplete() {
		handler.postDelayed(launchTask, 1000);
	}
	
    private Runnable launchTask = new Runnable() {
        public void run() {
          Intent myIntent = new Intent(StartActivity.this,
               MainMenuActivity.class);
          StartActivity.this.startActivity(myIntent);
        }
     };
}