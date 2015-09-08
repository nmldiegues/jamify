package pt.trycatch.jamify.jammer;

public class ImprovisedChord {
	private String figure;
	private String chord;
	private double figureVal;
	
	public ImprovisedChord(String figure, String chord, double figureVal) {
		this.figure = figure;
		this.chord = chord;
		this.figureVal = figureVal;
	}
	
	public String getFigure() {
		return figure;
	}
	public void setFigure(String figure) {
		this.figure = figure;
	}
	public String getChord() {
		return chord;
	}
	public void setChord(String chord) {
		this.chord = chord;
	}
	
	public double getFigureVal(){
		return figureVal;
	}
}
