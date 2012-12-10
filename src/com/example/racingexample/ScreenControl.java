package com.example.racingexample;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;

import com.badlogic.gdx.physics.box2d.Body;

public class ScreenControl {

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	
	public void loadResources(RaceActivity raceActivity) {
		mOnScreenControlTexture = new BitmapTextureAtlas(raceActivity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, raceActivity, "onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, raceActivity, "onscreen_control_knob.png", 128, 0);
		mOnScreenControlTexture.load();
	}
	
	public void initialize(final RaceActivity raceActivity, final Car car, GameCamera gameCamera) {
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, 
				gameCamera.height - mOnScreenControlBaseTextureRegion.getHeight(), gameCamera.bCamera, 
				mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion, 0.1f, 
				raceActivity.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				final Body carBody = car.mCarBody;
				
				
				car.velocity = Vector2Pool.obtain(pValueX * 20, pValueY * 20);
				carBody.setLinearVelocity(car.velocity);
				Vector2Pool.recycle(car.velocity);
				

				final float rotationInRad = (float)Math.atan2(-pValueX, pValueY);
				carBody.setTransform(carBody.getWorldCenter(), rotationInRad);

				car.mCarSprite.setRotation(MathUtils.radToDeg(rotationInRad));
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				/* Nothing. */
			}
		});
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
				analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
				analogOnScreenControl.getControlBase().setScale(1.25f);
				analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		raceActivity.mScene.setChildScene(analogOnScreenControl);
	} 
}
