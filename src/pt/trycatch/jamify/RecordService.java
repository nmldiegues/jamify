package pt.trycatch.jamify;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class RecordService {

	private static String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.gp3";
	
	private MediaRecorder mRecorder;
	
	public RecordService() {
		mRecorder = new MediaRecorder();
	}
	
	public void startRecording() {
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("Recorder", "prepare() failed");
		}

		mRecorder.start();
	}
	
	public String stopRecording() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
			} catch (IllegalStateException ise) {}
			mRecorder = null;
		}
		
		return mFileName;
	}
}
