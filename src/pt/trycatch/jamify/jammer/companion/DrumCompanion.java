package pt.trycatch.jamify.jammer.companion;

import java.util.List;

import pt.trycatch.jamify.jammer.ImprovisedChord;
import pt.trycatch.jamify.jammer.JammerUtils;
import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jfungue.Pattern;
import pt.trycatch.jfungue.Rhythm;

public class DrumCompanion {
	public static String improvise(Song song, int denominator, int bpm){
		List<ImprovisedChord> improChords = JammerUtils.getBasicImprovisedChords(song, denominator);
		String drumImproviseHH = "";//"T" + bpm + " V9 ";
		drumImproviseHH += getHighHats(improChords);
		
		String drumImproviseLB = "";//"T" + bpm + " V9 ";
		drumImproviseLB += getLowBongo(improChords);
		
		String drumImproviseCr = "";//"T" + bpm + " V9 ";
		drumImproviseCr += getCrash(improChords);

		//return "T103 V9 L1 [PEDAL_HI_HAT] [PEDAL_HI_HAT] [PEDAL_HI_HAT] L2 [LOW_TOM] [LOW_TOM] [LOW_TOM]";
		String s = "T" + bpm + " V9 L1 " + drumImproviseHH.substring(0, drumImproviseHH.length()-1) + " L2 " + drumImproviseLB.substring(0, drumImproviseLB.length()-1) + " L3 " + drumImproviseCr.substring(0, drumImproviseCr.length()-1);
		return s;
	}
	
	public static String getHighHats(List<ImprovisedChord> improvisedChords){
		String hh = "";
		for(ImprovisedChord ic : improvisedChords){
			hh += "[ACOUSTIC_SNARE]"+ic.getFigure() + " ";
		}
		return hh;
	}
	
	public static String getLowBongo(List<ImprovisedChord> improvisedChords){
		String lb = "";
		int i = 0;
		for(ImprovisedChord ic : improvisedChords){
			if(i == 4)
				lb += "[LOW_TOM]" + ic.getFigure() + " ";
				//lb += "X" + ic.getFigure() + " ";
			else
				lb += "R" + ic.getFigure() + " ";
			i++;
		}
		return lb;
	}
	
	public static String getCrash(List<ImprovisedChord> improvisedChords){
		String lb = "";
		int i = 0;
		for(ImprovisedChord ic : improvisedChords){
			if(i == 8){
				i = 0;
				lb += "[CRASH_CYMBAL_1]" + ic.getFigure() + " ";
			}
			else
				lb += "R" + ic.getFigure() + " ";
			i++;
		}
		return lb;
	}
}
