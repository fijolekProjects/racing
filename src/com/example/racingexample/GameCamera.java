package com.example.racingexample;

import org.andengine.engine.camera.BoundCamera;

import android.util.DisplayMetrics;


public class GameCamera {

	public int width;
	public int height;
	public BoundCamera bCamera;

	public void onCreateEngine(RaceActivity raceActivity) {
		getScreenSize(raceActivity);
		bCamera = new BoundCamera(0, 0, width, height);
	}

	private void getScreenSize(RaceActivity raceActivity) {
		DisplayMetrics metrics = new DisplayMetrics();
		raceActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width =  metrics.widthPixels;
	}

	public void defineBoundsAndChaseEntity(RaceActivity raceActivity, Car car) {
		bCamera.setBounds(0, 0, raceActivity.tmxLayer.getHeight(), raceActivity.tmxLayer.getWidth());
		bCamera.setBoundsEnabled(true);
		bCamera.setChaseEntity(car.mCarSprite);
		
	}
}
