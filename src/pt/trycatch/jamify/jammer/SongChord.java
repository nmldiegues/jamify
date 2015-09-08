package pt.trycatch.jamify.jammer;

public class SongChord {
	private String chordName;
	public double init;
	public double end;
	public double beats;
	
	public boolean toRemove;
	public int score;
	
	public static final int SCORE_GOOD = 1;
	public static final int SCORE_MEDIUM = 2;
	public static final int SCORE_BAD = 3;
	
	public SongChord(String chordName, double init){
		this.chordName = chordName;
		this.init = init;
	}
	
	public void setEnd(double end) {
		this.end = end;
	}
	
	public SongChord(String chordName, double init, double end){
		this.chordName = chordName;
		this.init = init;
		this.end = end;
	}
	
	public String getChordName(){
		return chordName;
	}
	
	public double getInit(){
		return init;
	}
	
	public double getEnd(){
		return end;
	}
	
	public double getBeats(){
		return beats;
	}
	
	public void setBeat(double beats){
		this.beats = beats;
	}
	
	@Override
	public String toString(){
		return getChordName() + " " + getBeats();
	}

	public static final double EPSILON = 10.0;
	
	public int compareChord(SongChord that) {
		if (this.chordName.equals(that.chordName)) {
			if ((this.init > (that.init - EPSILON) && this.init < (that.init + EPSILON)) &&
					(that.end < (that.end - EPSILON) && that.end > (that.end - EPSILON))) {
				return SCORE_GOOD;
			} else if ((this.init > (that.init - EPSILON) && this.init < (that.init + EPSILON)) ||
				(that.end < (that.end - EPSILON) && that.end > (that.end - EPSILON))) {
				return SCORE_MEDIUM;
			} else {
				return SCORE_BAD;
			}
		} else {
			return SCORE_BAD;
		}
	}
}
