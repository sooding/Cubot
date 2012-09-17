package com.project;

import android.app.Activity;
import android.os.Bundle;

public class Run extends Activity {

	/** Our own OpenGL View overridden */
	private Render render;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Initiate our Lesson with this Activity Context handed over
		render = new Render(this);
		//Set the lesson as View to the Activity
		setContentView(render);
	}

	/**
	 * Remember to resume 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		render.onResume();
	}

	/**
	 * Also pause 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		render.onPause();
	}

}