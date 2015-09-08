package pt.trycatch.jamify.jammer.modifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.trycatch.jamify.jammer.ImprovisedChord;
import pt.trycatch.jamify.jammer.JammerUtils;

public class UseChordNotes {
	public static List<ImprovisedChord> modifyChords(List<ImprovisedChord> improChords, int denominator, int[] chordDegreesToUse){
		List<ImprovisedChord> modifiedChords = new ArrayList<ImprovisedChord>();
		int i = 0;
		int j = 0;
		String currentChord;
		for(int z = 0; z < improChords.size()-1; z++){
			currentChord = improChords.get(z).getChord();
			if(z+1 == improChords.size()-1){
				if(currentChord.equals(improChords.get(z+1))){
					modifiedChords.addAll(useChordDegrees(improChords.subList(i, z+1), denominator, chordDegreesToUse));
				}
				else{
					modifiedChords.addAll(useChordDegrees(improChords.subList(i, j+1), denominator, chordDegreesToUse));
					modifiedChords.add(improChords.get(z+1));
				}
			}
			else if(!currentChord.equals(improChords.get(z+1).getChord())){
				j++;
				modifiedChords.addAll(useChordDegrees(improChords.subList(i, j), denominator, chordDegreesToUse));
				i = j;
			}
			else
				j++;
		}
		return modifiedChords;
	}

	private static List<ImprovisedChord> useChordDegrees(List<ImprovisedChord> chords, int denominator, int[] chordDegreesToUse) {
		ImprovisedChord chord = chords.get(0);
		List<String> otherChordNotes = getChordNotes(chord);
		if (otherChordNotes.size() == 0) {
			return chords;
		}
		int j = 0;
		for(int i = 1; i < chords.size(); i++){
			chords.get(i).setChord(otherChordNotes.get(chordDegreesToUse[j]));
			j = (j + 1) % (chordDegreesToUse.length); 
		}
		return chords;
	}

	private static List<String> getChordNotes(ImprovisedChord improvisedChord) {
		String chord = improvisedChord.getChord();
		String root = String.valueOf(chord.charAt(0));
		if(chord.length() > 1 && chord.charAt(1) == '#')
			root += "#";
		if(chord.contains("maj"))
			return JammerUtils.getMajorChordNotes(root);
		if(chord.contains("min"))
			return JammerUtils.getMinorChordNotes(root);
		return Collections.EMPTY_LIST;
	}
}
