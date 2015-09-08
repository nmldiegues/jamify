package pt.trycatch.jamify;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import pt.trycatch.jamify.jammer.CompanionMannager;
import pt.trycatch.jfungue.Pattern;
import pt.trycatch.jfungue.Player;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestActivity extends Activity {

	private CompanionMannager cm = new CompanionMannager();
	private static final String LOG_TAG = "AudioRecordTest";
	private static String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.gp3";

	private RecordButton mRecordButton = null;
	private MediaRecorder mRecorder = null;

	private PlayButton   mPlayButton = null;
	private MediaPlayer   mPlayer = null;

	private SendButton mSendButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imrpovisationTest();
		setContentView(R.layout.activity_test);

		LinearLayout ll = new LinearLayout(this);
		mRecordButton = new RecordButton(this);
		ll.addView(mRecordButton,
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						0));
		mPlayButton = new PlayButton(this);
		ll.addView(mPlayButton,
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						0));
		setContentView(ll);
		mSendButton = new SendButton(this);
		ll.addView(mSendButton,
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	private synchronized void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private synchronized void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	class RecordButton extends Button {
		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRecord(mStartRecording);
				if (mStartRecording) {
					setText("Stop recording");
				} else {
					setText("Start recording");
				}
				mStartRecording = !mStartRecording;
			}
		};

		public RecordButton(Context ctx) {
			super(ctx);
			setText("Start recording");
			setOnClickListener(clicker);
		}
	}

	class SendButton extends Button {

		OnClickListener clicker = new OnClickListener() {
			@Override
			public void onClick(View v) {
				BackendRequestThread.getAllInfoOnFile(mFileName);
			}
		};

		public SendButton(Context ctx) {
			super(ctx);
			setText("Send to Server");
			setOnClickListener(clicker);
		}
	}

	class PlayButton extends Button {
		boolean mStartPlaying = true;

		OnClickListener clicker = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPlay(mStartPlaying);
				if (mStartPlaying) {
					setText("Stop playing");
				} else {
					setText("Start playing");
				}
				mStartPlaying = !mStartPlaying;
			}
		};

		public PlayButton(Context ctx) {
			super(ctx);
			setText("Start playing");
			setOnClickListener(clicker);
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	public void imrpovisationTest(){
		imrpovise();
		imrpovise();
	}
	
	public void imrpovise(){
		MediaPlayer mp = new MediaPlayer();
		Player player = new Player();
		String improv = cm.getImrpovisation();
		System.out.println(improv);
		Pattern song = new Pattern(improv);
		String filePath = this.getFilesDir().getPath().toString() + "/MyImrpo.MIDI";
		File f = new File(filePath);
		FileInputStream fis = null;
		try {
			player.saveMidi(song, f);
			fis = new FileInputStream(f);
			FileDescriptor fd = fis.getFD();
			mp.setDataSource(fd);
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Log.e("Exception on FIle MIDI", e.getMessage());
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
	}

}
