package com.mesba.siivouspaivarc1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(mainIntent);
				}
			}
		};
		timer.start();
	}
	
}
