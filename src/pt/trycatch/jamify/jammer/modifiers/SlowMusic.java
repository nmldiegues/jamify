package pt.trycatch.jamify.jammer.modifiers;

import java.util.ArrayList;
import java.util.List;

import pt.trycatch.jamify.jammer.ImprovisedChord;
import pt.trycatch.jamify.jammer.MusicFigureConverter;

public class SlowMusic {

	public static List<ImprovisedChord> modifyChords(List<ImprovisedChord> improChords, int denominator){
		List<ImprovisedChord> modifiedChords = new ArrayList<ImprovisedChord>();
		int i = 0;
		int j = 0;
		String currentChord;
		for(int z = 0; z < improChords.size()-1; z++){
			currentChord = improChords.get(z).getChord();
			if(z+1 == improChords.size()-1){
				if(currentChord.equals(improChords.get(z+1))){
					modifiedChords.addAll(slowChords(improChords.subList(i, z+1), denominator));
				}
				else{
					modifiedChords.addAll(slowChords(improChords.subList(i, j+1), denominator));
					modifiedChords.add(improChords.get(z+1));
				}
			}
			else if(!currentChord.equals(improChords.get(z+1).getChord())){
				j++;
				modifiedChords.addAll(slowChords(improChords.subList(i, j), denominator));
				i = j;
			}
			else
				j++;
		}
		return modifiedChords;
	}

	//tries two join just two notes. If called twice has the effect of joining 4 notes
	private static List<ImprovisedChord> slowChords(List<ImprovisedChord> improChords, int denominator){
		List<ImprovisedChord> slowedChords = new ArrayList<ImprovisedChord>();
		String chord = improChords.get(0).getChord();
		boolean joinedChord = false;
		for(int i = 0; i < improChords.size()-1; i++){
			double c1Val = improChords.get(i).getFigureVal();
			double c2Val = improChords.get(i+1).getFigureVal();
			//checking we dont go over a semibeve note.
			if(c1Val + c2Val < 4){
				String figure = MusicFigureConverter.getMusicFigure(c1Val + c2Val, denominator);
				if(figure == null){
					slowedChords.add(improChords.get(i));
					joinedChord = false;
					continue;
				}
				ImprovisedChord newImprChord = new ImprovisedChord(figure, chord, (c1Val + c2Val));
				slowedChords.add(newImprChord);
				i++;
				joinedChord = true;
			}
			else{
				slowedChords.add(improChords.get(i));
				joinedChord = false;
			}

		}
		if(!joinedChord || improChords.size()%2 != 0){
			slowedChords.add(improChords.get(improChords.size()-1));
		}
		return slowedChords;
	}
}
