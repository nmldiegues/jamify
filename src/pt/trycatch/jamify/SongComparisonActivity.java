package pt.trycatch.jamify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.jammer.SongChord;
import pt.trycatch.jamify.learner.TouchHorizontalScrollView;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class SongComparisonActivity extends Activity {

	public static Song originalSong;
	public static Song userSong;
	public static int score;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Iterator<SongChord> origIt = originalSong.getChords().iterator();
		Iterator<SongChord> recIt = userSong.getChords().iterator();
		int seqRight = 0;
		int leftOrig = originalSong.getChords().size();
		int leftRec = userSong.getChords().size();
		SongChord orig = origIt.next();
		SongChord rec = recIt.next();
		int compare = 0;
		boolean toggle = true;
		while (true) {
			compare = rec.compareChord(orig);
			if (compare == SongChord.SCORE_GOOD) {
				rec.score = SongChord.SCORE_GOOD;
				leftOrig--;
				leftRec--;
				seqRight++;
				if (leftRec == 0 || leftOrig == 0) {
					orig = origIt.next();
					rec = recIt.next(); 
					break;
				}
			} else if (leftRec >= leftOrig) {
				leftRec--;
				rec.score = compare;
				if (toggle) {
					toggle = false;
				}
				if (!toggle || compare == SongChord.SCORE_MEDIUM) {
					toggle = true;
					rec.score = SongChord.SCORE_MEDIUM;
					seqRight++;
				}
				if (leftRec == 0) {
					rec = recIt.next();
					break;
				}
			} else {
				leftOrig--;
				if (leftOrig == 0) {
					orig = origIt.next();
				}
			}
		}
		rec.score = compare;
		while (recIt.hasNext()) {
			rec = recIt.next();
			rec.score = SongChord.SCORE_BAD;
		}
		
		Map<SongChord, Integer> counts = new HashMap<SongChord, Integer>();
		for (SongChord sc : originalSong.getChords()) {
			Integer val = counts.get(sc);
			if (val == null) {
				val = 0;
			}
			val++;
			counts.put(sc, val);
		}
		int gotRight = 0;
		for (SongChord sc : userSong.getChords()) {
			Integer val = counts.get(sc);
			if (val != null && val > 0) {
				val--;
				counts.put(sc, val);
				gotRight++;
			}
		}
		
		score = (gotRight / originalSong.getChords().size() * 100);
		Log.d("Score", "" + score);
		
		TouchHorizontalScrollView autoScrollView = new TouchHorizontalScrollView(this);
		setContentView(autoScrollView);
		//setContentView(R.layout.activity_song_comparison);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_comparison, menu);
		return true;
	}

}
