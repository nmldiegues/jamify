package pt.trycatch.jamify.jammer;

import android.R.color;

public class ChordNotes {
	private static String[] chromaticScale = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	public static String sumNote(String note, double tones){
		int notIndex = noteIndex(note);
		int distance = 0;
		while(tones != 0.0){
			distance++;
			tones -= 0.5;
		}
		return chromaticScale[(notIndex+distance)%(chromaticScale.length)];		
	}
	
	private static int noteIndex(String note){
		for(int i = 0; i < chromaticScale.length; i++)
			if(note.equals(chromaticScale[i]))
				return i;
		return -1;
	}
}
