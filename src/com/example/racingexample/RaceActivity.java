package com.example.racingexample;


import java.util.ArrayList;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 22:43:20 - 15.07.2010
 */
public class RaceActivity extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	

	
	
	public int cameraWidth = 0;
	public int cameraHeight = 0;
	

	// ===========================================================
	// Fields
	// ===========================================================

	BoundCamera bCamera;
	Scene mScene;
	PhysicsWorld mPhysicsWorld;

	private TMXTiledMap mTMXTiledMap;
	
	
	
	
	// ===========================================================
	// Constructors
	// ===========================================================
	Car car = new Car();
	Obstacle obstacle = new Obstacle();
	ScreenControl screenControl = new ScreenControl();
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		cameraHeight = metrics.heightPixels;
		cameraWidth =  metrics.widthPixels;
		
		new Camera(0, 0, cameraWidth, cameraHeight);
		this.bCamera = new BoundCamera(0, 0, cameraWidth, cameraHeight);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), this.bCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		car.loadResources(this);
		obstacle.loadResources(this);
		screenControl.loadResources(this);
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("Water", "true")) {
						
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");
				
			
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		 
		TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		
		
		
		
		final ArrayList<TMXLayer> tmxLayers = this.mTMXTiledMap.getTMXLayers();
		
		for (int layerID = 0; layerID < tmxLayers.size(); layerID++) {
			mScene.attachChild(tmxLayers.get(layerID));
		}
		
		
		
		
		this.mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false, 8, 1);

		
		
		
		
		car.initCar(this);
		obstacle.initBoxes(this);
		screenControl.initOnScreenControls(this, car);
		
		this.bCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
		this.bCamera.setBoundsEnabled(true);
		this.bCamera.setChaseEntity(car.mCarSprite);
		
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, tmxLayer.getHeight() - 0, tmxLayer.getWidth(), 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, tmxLayer.getWidth(), 0, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 0, tmxLayer.getHeight(), vertexBufferObjectManager);
		final Rectangle right = new Rectangle(tmxLayer.getWidth() - 0, 0, 0, tmxLayer.getHeight(), vertexBufferObjectManager);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 1.0f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		
		createMapObjects(this.mTMXTiledMap);

		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		return this.mScene;
	}
	

	

	private void createMapObjects(TMXTiledMap map) {
		
		// Loop through the object groups
        for(final TMXObjectGroup group: this.mTMXTiledMap.getTMXObjectGroups()) {

                if(group.getTMXObjectGroupProperties().containsTMXProperty("Wall", "true")){
                        createWallLayerObjects(group.getTMXObjects());
                }
        }
}
	

	private void createWallLayerObjects(ArrayList<TMXObject> objects) {
		 
		// This is our "wall" layer. Create the boxes from it
        for(final TMXObject object : objects) {
                
                final Rectangle rect = new Rectangle(object.getX(), object.getY(), object.getWidth(), object.getHeight(), this.getVertexBufferObjectManager());
   
                rect.setVisible(false);
                this.mScene.attachChild(rect);
        }
	}
	

	@Override
	public void onGameCreated() {

	}

	// ===========================================================
	// Methods
	// ===========================================================



	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
