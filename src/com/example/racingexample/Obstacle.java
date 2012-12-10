package com.example.racingexample;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Obstacle {
	
	private BitmapTextureAtlas mBoxTexture;
	private ITextureRegion mBoxTextureRegion;
	private int boxSize = 16;
	private int RACETRACK_WIDTH = 64;
	
	public void loadResources(RaceActivity raceActivity) {
		mBoxTexture = new BitmapTextureAtlas(raceActivity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBoxTexture, raceActivity, "box.png", 0, 0);
		mBoxTexture.load();
	}
	
	public void initializeBoxes(RaceActivity raceActivity, GameCamera gameCamera) {
		addBox(gameCamera.width / 2, RACETRACK_WIDTH / 2, raceActivity);
		addBox(gameCamera.width, RACETRACK_WIDTH / 2, raceActivity);
		addBox(gameCamera.width / 2, gameCamera.height - RACETRACK_WIDTH / 2, raceActivity);
		addBox(gameCamera.width / 2, gameCamera.height - RACETRACK_WIDTH / 2, raceActivity);
}
	
	private void addBox(final float pX, final float pY, RaceActivity raceActivity) {
		final Sprite box = new Sprite(pX, pY, boxSize, boxSize, mBoxTextureRegion, raceActivity.getVertexBufferObjectManager());

		final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0.5f, 0.5f);
		final Body boxBody = PhysicsFactory.createBoxBody(raceActivity.mPhysicsWorld, box, BodyType.DynamicBody, boxFixtureDef);
		boxBody.setLinearDamping(10);
		boxBody.setAngularDamping(10);
		raceActivity.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(box, boxBody, true, true));

		raceActivity.mScene.attachChild(box);
	}
}
