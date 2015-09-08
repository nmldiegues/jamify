package pt.trycatch.jamify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static final int SPLASH_TIME = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		

		new IntentLauncher().start();
	}

	private class IntentLauncher extends Thread {
		@Override
		public void run() {
			try {
				// Sleeping
				Thread.sleep(SPLASH_TIME*1000);
			} catch (Exception e) {
			}

			Intent intent = new Intent(MainActivity.this, MenuActivity.class);
			MainActivity.this.startActivity(intent);
			MainActivity.this.finish();
		}
	}

}
