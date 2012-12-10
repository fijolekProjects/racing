package com.example.racingexample;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Car {

	Body mCarBody;
	Sprite mCarSprite;
	Vector2 velocity;
	
	private BitmapTextureAtlas mCarTexture;
	ITextureRegion mCarTextureRegion;
	
	
	public void loadResources(RaceActivity raceActivity) {
		mCarTexture = new BitmapTextureAtlas(raceActivity.getTextureManager(), 36, 72, TextureOptions.BILINEAR);
		mCarTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCarTexture, raceActivity, "green-car-top.png", 0, 0);
		mCarTexture.load();
	}
	
	public void initialize(final RaceActivity raceActivity, final GameCamera gameCamera) {
		mCarSprite = new Sprite(80, 80, 36, 72, mCarTextureRegion, raceActivity.getVertexBufferObjectManager());
		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1.0f);
		mCarBody = PhysicsFactory.createBoxBody(raceActivity.mPhysicsWorld, mCarSprite, BodyType.DynamicBody, carFixtureDef);
		mCarBody.setUserData("player");
		
		raceActivity.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mCarSprite, mCarBody, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				gameCamera.bCamera.updateChaseEntity();
			}
		});
		raceActivity.mScene.attachChild(mCarSprite);
	}
}
