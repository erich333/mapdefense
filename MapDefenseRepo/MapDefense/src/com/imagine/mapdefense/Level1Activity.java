package com.imagine.mapdefense;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.runnable.RunnableHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.modifier.ease.EaseLinear;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class Level1Activity extends MapGameActivity {
	private float CAMERA_WIDTH = 720;
	private float CAMERA_HEIGHT = 512;
	private static final float SPRITE_WIDTH_DP = 160;
	private static final float SPRITE_HEIGHT_DP = 160;
	MapView mapView;
	List<Overlay> mapOverlays;
	//	Drawable drawable;
	//	HelloitemizedOverlay itemizedOverlay;
	GeoPoint geoPoint = null;
	MapController myMC = null;
	protected OverlayItem last;
	private Camera mCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mPlayerTextureRegion;
	private GeoPoint[] geoPoints;
	private Path path;
	public boolean notDrawn = true;
	private float mDensityScaling;
	private Object mSpriteWidth;
	private int mSpriteHeight;
	private Scene scene;
	private Handler handler = new Handler();
	private Engine engine;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
		mapOverlays = mapView.getOverlays();

		mapView.setSatellite(true);

		final String[] pairs = getDirectionData("maps.kml");
		String[] lngLat = pairs[0].split(",");
		geoPoints = new GeoPoint[pairs.length];
		// Starting Point
		GeoPoint startGP = new GeoPoint(
				(int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
						.parseDouble(lngLat[0]) * 1E6));

		myMC = mapView.getController();
		geoPoint = startGP;
		myMC.setCenter(geoPoint);
		myMC.setZoom(15);
		mapView.setClickable(false);
		mapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

		//Navigate
		int minLat = Integer.MAX_VALUE;
		int minLong = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int maxLong = Integer.MIN_VALUE;
		GeoPoint gp1;
		GeoPoint gp2 = startGP;
		geoPoints[0] = startGP;
		for(int i=1;i<pairs.length;i++){
			lngLat = pairs[i].split(",");
			gp1 = gp2;
			//watch out! For GeoPoint, first:latitude, second:longitude

			gp2 = new GeoPoint(
					(int) (Double.parseDouble(lngLat[1]) * 1E6),
					(int) (Double.parseDouble(lngLat[0]) * 1E6));
			geoPoints[i] = gp2;
			mapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));


			minLat  = Math.min( gp2.getLatitudeE6(), minLat );
			minLong = Math.min( gp2.getLongitudeE6(), minLong);
			maxLat  = Math.max( gp2.getLatitudeE6(), maxLat );
			maxLong = Math.max( gp2.getLongitudeE6(), maxLong );


			Log.d("xxx", "pair:" + pairs[i]);
		}

		GeoPoint centerGP = new GeoPoint((maxLat+minLat)/2,(maxLong+minLong)/2);
		myMC.setCenter(centerGP);
		myMC.zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));

		//end point
		mapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));



	}


	private String[] getDirectionData(String fileName) {


		Document doc = null;
		String pathConent = "";
		InputStream input = null;
		try {

			input = getAssets().open("maps/" + fileName, AssetManager.ACCESS_BUFFER);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(input);

		} catch (Exception e) {
			Log.e(this.getLocalClassName(), "Error loading file: " + fileName, e);
		} finally{
			try {
				if(input!=null){
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		NodeList nl = doc.getElementsByTagName("LineString");
		for (int s = 0; s < nl.getLength(); s++) {
			Node rootNode = nl.item(s);
			NodeList configItems = rootNode.getChildNodes();
			for (int x = 0; x < configItems.getLength(); x++) {
				Node lineStringNode = configItems.item(x);
				NodeList path = lineStringNode.getChildNodes();
				pathConent = path.item(0).getNodeValue();
			}
		}
		String[] tempContent = pathConent.split(" ");
		return tempContent;
	}

	//*****************************************************************************

	public class DirectionPathOverlay extends Overlay {

		private GeoPoint gp1;
		private GeoPoint gp2;

		public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) {
			this.gp1 = gp1;
			this.gp2 = gp2;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			if(notDrawn ){
				synchronized(Level1Activity.this){
					notDrawn = false;
					Level1Activity.this.notify();
				}
			}
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection();
			if (shadow == false) {

				Paint paint = new Paint();
				paint.setAntiAlias(true);
				Point point = new Point();
				projection.toPixels(gp1, point);
				paint.setColor(Color.BLUE);
				Point point2 = new Point();
				projection.toPixels(gp2, point2);
				paint.setStrokeWidth(2);
				canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,
						(float) point2.y, paint);
			}
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub

			super.draw(canvas, mapView, shadow);
		}
	}

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
		this.mDensityScaling = metrics.density;

		// multiply 160dpi-Sprite dimensions with density factor
		this.mSpriteWidth = Math.round(SPRITE_WIDTH_DP * mDensityScaling);
		this.mSpriteHeight = Math.round(SPRITE_HEIGHT_DP * mDensityScaling);



		RatioResolutionPolicy ratio;

		this.mCamera = new Camera(0, 0, this.CAMERA_WIDTH, this.CAMERA_HEIGHT);
		ratio = new RatioResolutionPolicy(this.CAMERA_WIDTH, this.CAMERA_HEIGHT);
		RunnableHandler runHandle = new RunnableHandler();
		engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				ratio, this.mCamera).setNeedsSound(true));
		engine.registerUpdateHandler(runHandle);
		return engine;
	}
	@Override
	public void onLoadResources() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "gfx/androidmarker.png", 0, 0);

		this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		scene = new Scene();
		scene.setBackground(new ColorBackground(0,0,0,.0f));
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		while(notDrawn){
			synchronized(Level1Activity.this){
				try {
					Level1Activity.this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		path = new Path(geoPoints.length);
		Projection projection = mapView.getProjection();
		for(GeoPoint gp:geoPoints){
			Point pt = new Point();
			projection.toPixels(gp, pt);

			path.to(pt.x - (this.mPlayerTextureRegion.getWidth()/2), pt.y - (this.mPlayerTextureRegion.getHeight()/2));
		}
		reanimate();
	}
	int players = 0;
	private void reanimate() {
		this.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				/* Create the Player and add it to the scene. */
				Sprite player = new Sprite(-1, -1, Level1Activity.this.mPlayerTextureRegion){
					@Override
					protected void onManagedUpdate(final float pSecondsElapsed) {
						super.onManagedUpdate(pSecondsElapsed);
					}
				};

				scene.attachChild(player);
				Sprite enemy = player;
				enemy .clearEntityModifiers();
				IPathModifierListener pathListener = new IPathModifierListener() {
					@Override
					public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
						Debug.d("onPathStarted");
					}

					@Override
					public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
						Debug.d("onPathWaypointStarted:  " + pWaypointIndex);
						if(pWaypointIndex==5 && players < 5){
							players++;
							reanimate();
						}
					}

					@Override
					public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
						Debug.d("onPathWaypointFinished: " + pWaypointIndex);
					}

					@Override
					public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
						Debug.d("onPathFinished");
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								Level1Activity.this.scene.detachChild(pEntity);
							}
						});
						
//						reanimate();
					}
				};
				enemy.registerEntityModifier(new PathModifier(10, path,pathListener, EaseLinear.getInstance()));
			}
		});
	}

}
