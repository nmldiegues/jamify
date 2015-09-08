package pt.trycatch.jamify.jammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JammerUtils {
	public static double[] beatsVals = new double[]{1.0, 0.5, 0.25, 1.0/8.0, 1.0/16.0, 1.0/32.0, 1.0/64.0};

	public static List<ImprovisedChord> getBasicImprovisedChords(Song song, int denominator) {
		List<ImprovisedChord> impro = new ArrayList<ImprovisedChord>();
		for(SongChord sc : song.getChords()){
			double beatsToFill = sc.getBeats();
			beatsToFill = (double) Math.round(beatsToFill * 10) / 10;
			String hack = String.valueOf(beatsToFill);
			//0 beat songs will be skipped!
			if(!hack.endsWith(".5"))
				beatsToFill = Math.round(beatsToFill);
			int i = 0;
			double currentBeatVal = beatsVals[i++];
			while(beatsToFill != 0.0){
				if(beatsToFill - currentBeatVal >= 0.0){
					beatsToFill -= currentBeatVal;
					impro.add(new ImprovisedChord(MusicFigureConverter.getMusicFigure(currentBeatVal, denominator), sc.getChordName(), currentBeatVal));
				}
				else
					currentBeatVal = beatsVals[i++];
			}
		}
		return impro;
	}

	public static List<String> getMajorChordNotes(String rootNote){
		List<String> otherNotes = new ArrayList<String>();
		otherNotes.add(ChordNotes.sumNote(rootNote, 1.5));
		otherNotes.add(ChordNotes.sumNote(rootNote, 3.5));
		return otherNotes;
	}

	public static List<String> getMinorChordNotes(String rootNote){
		List<String> otherNotes = new ArrayList<String>();
		otherNotes.add(ChordNotes.sumNote(rootNote, 2.0));
		otherNotes.add(ChordNotes.sumNote(rootNote, 3.5));
		return otherNotes;
	}
	
	public static String getDominantChords(Song s){
		Map<String, Double> chordCountMap = new HashMap<String, Double>();
		for(SongChord sc : s.getChords()){
			Double chordCount = chordCountMap.get(sc.getChordName());
			if(chordCount == null){
				chordCount = 0.0;
			}
			chordCount += sc.getBeats();
			chordCountMap.put(sc.getChordName(), chordCount);
		}
		chordCountMap = SortableMap.sort(chordCountMap, SortableMap.ORDER.DESCENDING);
		int i = 0;
		String str = "";
		for(String chord: chordCountMap.keySet()){
			if(i == 3)
				break;
			i++;
			str += chord + " ";
			
		}
		return str.substring(0, str.length()-1);		
	}
}
