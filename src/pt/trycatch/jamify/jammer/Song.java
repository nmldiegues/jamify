package pt.trycatch.jamify.jammer;

import java.util.List;

public class Song {
	private List<SongChord> chords;
	public final int bpm;
	public final int tpm;
	public final int numerator;
	public final int denominator;
	
	public Song(List<SongChord> chords, int bmp, int tpm, int numerator, int denominator){
		this.chords = chords;
		setBeats(chords, bmp);
		this.bpm = bmp;
		this.tpm = tpm;
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	private void setBeats(List<SongChord> chords, int bmp) {
		for(SongChord chord : chords){
			double duration = chord.getEnd() - chord.getInit();
			double beats = duration*bmp/60000.0;
			chord.setBeat((double) Math.round(beats * 100) / 100);
		}
	}
	
	public int getNumerator(){
		return numerator;
	}
	
	public int getDenominator(){
		return denominator;
	}
	
	public int getBMP(){
		return this.bpm;
	}

	public List<SongChord> getChords(){
		return chords;
	}
	
	@Override
	public String toString(){
		String str = "";
		for(SongChord sc : getChords()){
			str  += sc.toString() + "\n";
		}
		return str;
	}
}
