package pt.trycatch.jamify.jammer.companion;

import java.util.List;

import pt.trycatch.jamify.jammer.ImprovisedChord;
import pt.trycatch.jamify.jammer.JammerUtils;
import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.jammer.modifiers.SlowMusic;
import pt.trycatch.jamify.jammer.modifiers.UseChordNotes;

public class PianoCompanion {
	
	public static String improvise(Song song, int denominator, int bpm, int[] chordDegreesToUse){
		String piano = "T" + bpm + " V2 I[ACOUSTIC_GRAND] ";
		List<ImprovisedChord> improChords = JammerUtils.getBasicImprovisedChords(song, denominator);
		//improChords = SlowMusic.modifyChords(improChords, denominator);
		//improChords = UseChordNotes.modifyChords(improChords, denominator, chordDegreesToUse);
		/*for(ImprovisedChord ic : improChords){
			String note = ic.getChord();
			piano += note + ic.getFigure() + " ";
		}*/
		for(ImprovisedChord ic : improChords){
			String note = String.valueOf(ic.getChord().charAt(0));
			if(ic.getChord().length() > 1 && ic.getChord().charAt(1) == '#')
				note += "#";
			piano += note + "5" + ic.getFigure() + " ";
		}
		return piano.substring(0, piano.length()-1);
	}
}
