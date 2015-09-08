package pt.trycatch.jamify.jammer.companion;

import java.util.List;

import pt.trycatch.jamify.jammer.ImprovisedChord;
import pt.trycatch.jamify.jammer.JammerUtils;
import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.jammer.modifiers.SlowMusic;
import pt.trycatch.jamify.jammer.modifiers.UseChordNotes;

public class BassCompanion {
	
	public static String improvise(Song song, int denominator, int bpm, int[] chordDegreesToUse){
		String bassImprovise = "T" + bpm + " V1 I[SLAP_BASS_1] ";
		List<ImprovisedChord> improChords = JammerUtils.getBasicImprovisedChords(song, denominator);
		improChords = SlowMusic.modifyChords(improChords, denominator);
		improChords = UseChordNotes.modifyChords(improChords, denominator, chordDegreesToUse);
		for(ImprovisedChord ic : improChords){
			String note = String.valueOf(ic.getChord().charAt(0));
			if(ic.getChord().length() > 1 && ic.getChord().charAt(1) == '#')
				note += "#";
			bassImprovise += note + "2" + ic.getFigure() + " ";
		}
		return bassImprovise.substring(0, bassImprovise.length()-1);
	}
}
