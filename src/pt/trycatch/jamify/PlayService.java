package pt.trycatch.jamify;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class PlayService {
	private static String mFileName = JamActivity.mMidiFile;
	
	private MediaPlayer mPlayer;
	
	public PlayService() {
		mPlayer = new MediaPlayer();
	}
	
	public void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("PlayService", "prepare() failed");
		}
	}

	public void stopPlaying() {
		try {
			mPlayer.release();
		} catch (Exception e) {}
		mPlayer = null;
	}
}
