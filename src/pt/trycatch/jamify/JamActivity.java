package pt.trycatch.jamify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import pt.trycatch.jamify.jammer.CompanionMannager;
import pt.trycatch.jamify.jammer.JammerUtils;
import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jfungue.Pattern;
import pt.trycatch.jfungue.Player;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class JamActivity extends Activity {

	public volatile boolean isJamming = false;
	
	private PlayService mPlay;
	private RecordService mRecord;
	private CompanionMannager cm;
	private String array_spinner[];
	public static String mMidiFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jam.mid";
	private Song imprSong;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jam);
		array_spinner = new String[4];
		array_spinner[0] = "Bass";
		array_spinner[1] = "Piano";
		array_spinner[2] = "Bass+Piano";
		array_spinner[3] = "Bass+Piano+Drums";
		Spinner s = (Spinner) findViewById(R.id.instruments);
		
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
		CheckBox isJamming = (CheckBox) findViewById(R.id.isJamming);
		isJamming.setOnCheckedChangeListener(new StartJammingListener());
		
		Button likeButton = (Button) findViewById(R.id.likeBtn);
		likeButton.setOnClickListener(new LikeButtonListener());
		
		Button tryAnotherButton = (Button) findViewById(R.id.dontLikeBtn);
		tryAnotherButton.setOnClickListener(new TryAnotherButtonListener());
		
		cm = new CompanionMannager();
	}

	private void restartAllActivity() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ProgressBar recogProgress = (ProgressBar) findViewById(R.id.recognitionProgress);
				recogProgress.setVisibility(View.INVISIBLE);
			}
		});
		
		if (mRecord != null) {
			mRecord.stopRecording();
		}
		if (mPlay != null) {
			mPlay.stopPlaying();
		}
		// stop recording and/or playing
		mRecord = new RecordService();
		mPlay = new PlayService();
		
	}
	
	private void startJamming() {
		restartAllActivity();
		mRecord.startRecording();
		// set timer to stop recording
		
		//Timer timer = new Timer();
		//AnalyseJamTask stopTask = new AnalyseJamTask(timer);
        //timer.schedule(stopTask, 10000, Long.MAX_VALUE);
        
        runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ProgressBar recogProgress = (ProgressBar) findViewById(R.id.recognitionProgress);
				recogProgress.setVisibility(View.VISIBLE);
			}
        });
	}
	
	public class AnalyseJamTask extends TimerTask {

		private final Timer timer;
		
		public AnalyseJamTask(Timer timer) {
			this.timer = timer;
		}
		
		@Override
		public void run() {
			timer.cancel();
			
			String fileName = mRecord.stopRecording();
			final Song songInfo = BackendRequestThread.getAllInfoOnFile(fileName);
			imprSong = songInfo;
			
	        runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ProgressBar recogProgress = (ProgressBar) findViewById(R.id.recognitionProgress);
					recogProgress.setVisibility(View.INVISIBLE);
					
					TextView bpmText = (TextView) findViewById(R.id.bpmText);
					bpmText.setText("Tempo: " + songInfo.getBMP() + " beats / min");
					
					TextView metricText = (TextView) findViewById(R.id.metricText);
					metricText.setText("Metric: " + songInfo.getNumerator() + "/" + songInfo.getDenominator());
					
					TextView scaleTxt = (TextView) findViewById(R.id.scaleTxt);
					scaleTxt.setText("Scale: " + songInfo.getChords().get(0).getChordName());
					
					TextView domChords = (TextView) findViewById(R.id.dominantText);
					domChords.setText("Dominant Chords: " + JammerUtils.getDominantChords(songInfo));
					
					ProgressBar tempoScale = (ProgressBar) findViewById(R.id.tempoScale);
					tempoScale.setProgress(songInfo.getBMP()*100/140);
					
				}
	        });
	        
	        startJam();
	       
		}
		
	}
	
	private void startJam() {
		String fileName = mRecord.stopRecording();
		final Song songInfo = BackendRequestThread.getAllInfoOnFile(fileName);
		imprSong = songInfo;
		ProgressBar recogProgress = (ProgressBar) findViewById(R.id.recognitionProgress);
		recogProgress.setVisibility(View.INVISIBLE);
		
		TextView bpmText = (TextView) findViewById(R.id.bpmText);
		bpmText.setText("Tempo: " + songInfo.getBMP() + " beats / min");
		
		TextView metricText = (TextView) findViewById(R.id.metricText);
		metricText.setText("Metric: " + songInfo.getNumerator() + "/" + songInfo.getDenominator());
		
		TextView scaleTxt = (TextView) findViewById(R.id.scaleTxt);
		scaleTxt.setText("Scale: " + songInfo.getChords().get(0).getChordName());
		
		TextView domChords = (TextView) findViewById(R.id.dominantText);
		domChords.setText("Dominant Chords: " + JammerUtils.getDominantChords(songInfo));
		
		ProgressBar tempoScale = (ProgressBar) findViewById(R.id.tempoScale);
		tempoScale.setProgress(songInfo.getBMP()*100/140);
			
		 String instrument = ((Spinner) findViewById(R.id.instruments)).getSelectedItem().toString();
			cm.setInstrument(instrument);
			
	        String impr = cm.getImprovisation(imprSong);
	        Player player = new Player();
	        Pattern pattern = new Pattern(impr);
	        pattern.add(pattern);
	        pattern.add(pattern);
	        File f = new File(mMidiFile);
	        FileInputStream fis = null;
	        try {
	        	player.saveMidi(pattern, f);
	        	fis = new FileInputStream(f);
	        	mPlay.startPlaying();
	        } catch (Exception ioe) {
	        	Log.e("Error playing", ioe.getMessage());
	        } finally {
	        	if (fis != null) { try {
					fis.close();
				} catch (IOException e) {
				} }
	        }
		
	}
	
	public class StartJammingListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				if (isJamming) {
					restartAllActivity();
				}
				isJamming = true;
				startJamming();
				
			} else {
				startJam();
				if (isJamming) {
					//restartAllActivity();
				}
				isJamming = false;
			}
		}

		
	}
	
	public class LikeButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// empty logic
		}
	}
	
	public class TryAnotherButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			restartAllActivity();
			startJam();
			// parametrize the Jammer to use another style
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.jam, menu);
		return true;
	}

}
