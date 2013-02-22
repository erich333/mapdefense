package com.imagine.mapdefense;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.modifier.ScaleAtModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainMenuActivity extends MapDefenseGameActivity implements
IOnMenuItemClickListener {

	protected static final int MENU_ABOUT = 0;
	protected static final int MENU_QUIT = MENU_ABOUT + 1;
	protected static final int MENU_PLAY = 100;
	protected static final int MENU_SCORES = MENU_PLAY + 1;
	protected static final int MENU_OPTIONS = MENU_SCORES + 1;
	protected static final int MENU_HELP = MENU_OPTIONS + 1;

	// ===========================================================
	// Fields
	// ===========================================================
	protected Scene mainScene;

	private BitmapTextureAtlas mMenuBackTexture;
	private TextureRegion mMenuBackTextureRegion;

	protected MenuScene staticMenuScene, mPopUpMenuScene;

	private BitmapTextureAtlas mPopUpTexture;
	private Texture mFontTexture;
	private Font mFont;
	protected TextureRegion mPopUpAboutTextureRegion;
	protected TextureRegion mPopUpQuitTextureRegion;
	protected TextureRegion mMenuPlayTextureRegion;
	protected TextureRegion mMenuScoresTextureRegion;
	protected TextureRegion mMenuOptionsTextureRegion;
	protected TextureRegion mMenuHelpTextureRegion;
	private boolean popupDisplayed;

	@Override
	public Engine onLoadEngine() {
		return super.onLoadEngine();
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		mainScene.registerEntityModifier(new ScaleAtModifier(0.5f,
				0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
		staticMenuScene.registerEntityModifier(
				new ScaleAtModifier(0.5f, 0.0f, 1.0f,
						CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
	}
	
	@Override
	public void onLoadResources() {
		/* Load Font/Textures. */
		this.mFontTexture = new BitmapTextureAtlas(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.mFontTexture,
				this, "Droid.ttf", 32, true, Color.RED);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		this.mMenuBackTexture = new BitmapTextureAtlas(1024, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuBackTextureRegion =
				BitmapTextureAtlasTextureRegionFactory.createFromAsset( this.mMenuBackTexture,
						this, "gfx/MainMenu/MainMenuBk.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);

		this.mPopUpTexture = new BitmapTextureAtlas(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mPopUpAboutTextureRegion =
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPopUpTexture,
						this, "gfx/MainMenu/About_button.png", 0, 0);
		this.mPopUpQuitTextureRegion =
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPopUpTexture,
						this, "gfx/MainMenu/Quit_button.png", 0, 50);
		this.mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
		popupDisplayed = false;
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.createStaticMenuScene();
		this.createPopUpMenuScene();

		/* Center the background on the camera. */
		final int centerX = ( CAMERA_WIDTH -
				this.mMenuBackTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT -
				this.mMenuBackTextureRegion.getHeight()) /
				2;

		this.mainScene = new Scene(1);
		/* Add the background and static menu */
		final Sprite menuBack = new Sprite(centerX,
				centerY, this.mMenuBackTextureRegion);
		mainScene.getLastChild().attachChild(menuBack);
		mainScene.setChildScene(staticMenuScene);
		mainScene.setBackground(new ColorBackground(0,0,0,.0f));
		staticMenuScene.setBackground(new ColorBackground(0,0,0,.0f));
		return this.mainScene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(final int pKeyCode,
			final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU &&
				pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(popupDisplayed) {
				/* Remove the menu and reset it. */
				this.mPopUpMenuScene.back();
				mainScene.setChildScene(staticMenuScene);
				popupDisplayed = false;
			} else {
				/* Attach the menu. */
				this.mainScene.setChildScene(
						this.mPopUpMenuScene, false, true, true);
				popupDisplayed = true;
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
		case MENU_ABOUT:
			Toast.makeText(MainMenuActivity.this,
					"About selected",
					Toast.LENGTH_SHORT).show();
			return true;
		case MENU_QUIT:
			/* End Activity. */
			this.finish();
			return true;
		case MENU_PLAY:
            mainScene.registerEntityModifier(
               new ScaleModifier(1.0f, 1.0f,
               0.0f));
            handler.postDelayed(
               launchLevel1Task,1000);
            return true;
		case MENU_SCORES:
			Toast.makeText(MainMenuActivity.this,
					"Scores selected",
					Toast.LENGTH_SHORT).show();
			return true;
		case MENU_OPTIONS:
			Toast.makeText(MainMenuActivity.this,
					"Options selected",
					Toast.LENGTH_SHORT).show();
			return true;
		case MENU_HELP:
			Toast.makeText(MainMenuActivity.this,
					"Help selected", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void createStaticMenuScene() {
		this.staticMenuScene = new MenuScene(this.camera);
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(
				new TextMenuItem(MENU_PLAY, mFont, "Play Game"),
				1.5f,1.0f);
		playMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.staticMenuScene.addMenuItem(playMenuItem);

		final IMenuItem scoresMenuItem =
				new ColorMenuItemDecorator(
						new TextMenuItem(MENU_SCORES, mFont, "Scores"),
						0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		scoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.staticMenuScene.addMenuItem(scoresMenuItem);

		final IMenuItem optionsMenuItem =
				new ColorMenuItemDecorator(
						new TextMenuItem(MENU_OPTIONS, mFont, "Options"),
						0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.staticMenuScene.addMenuItem(optionsMenuItem);

		final IMenuItem helpMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(MENU_HELP, mFont, "Help"),
				0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.staticMenuScene.addMenuItem(helpMenuItem);
		this.staticMenuScene.buildAnimations();

		this.staticMenuScene.setBackgroundEnabled(false);

		this.staticMenuScene.setOnMenuItemClickListener(this);
	}

	protected void createPopUpMenuScene() {
		this.mPopUpMenuScene = new MenuScene(this.camera);

		final SpriteMenuItem aboutMenuItem =
				new SpriteMenuItem(MENU_ABOUT,
						this.mPopUpAboutTextureRegion);
		aboutMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(aboutMenuItem);
		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(
				MENU_QUIT, this.mPopUpQuitTextureRegion);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mPopUpMenuScene.addMenuItem(quitMenuItem);
		this.mPopUpMenuScene.setMenuAnimator(
				new SlideMenuAnimator());

		this.mPopUpMenuScene.buildAnimations();

		this.mPopUpMenuScene.setBackgroundEnabled(false);

		this.mPopUpMenuScene.setOnMenuItemClickListener(this);
	}

	private Runnable launchLevel1Task = new Runnable() {
        public void run() {
          Intent myIntent = new Intent(MainMenuActivity.this,
         Level1Activity.class);
          MainMenuActivity.this.startActivity(myIntent);
        }
     };

}
