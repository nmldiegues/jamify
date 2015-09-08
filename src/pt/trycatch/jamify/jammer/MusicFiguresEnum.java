package pt.trycatch.jamify.jammer;

public enum MusicFiguresEnum {
	Semibreve(1.0),
	Minima(0.5),
	SeMinima(0.25),
	Colcheia(1.0/8.0),
	SemiColcheia(1.0/16.0),
	Fusa(1.0/32.0),
	SemiFusa(1.0/64.0);

	private double figure;
	
	MusicFiguresEnum(double type){
		this.figure = type;
	}

	public double figure(){return figure;}
}
