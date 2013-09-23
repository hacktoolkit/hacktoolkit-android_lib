package com.hacktoolkit.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hacktoolkit.android.utils.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public abstract class HTKSplashScreenActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutId = getLayoutId();
		setContentView(layoutId);
		runSplashThread();
	}

	public void runSplashThread() {
		final int splashDuration = getSplashDurationMillis();
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < splashDuration) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					finish();
					Intent intent = getNextActivity();
					startActivity(intent);
				}
			}
		};
		splashThread.start();
	}
	protected abstract int getLayoutId();
	protected abstract int getSplashDurationMillis();
	protected abstract Intent getNextActivity();
}