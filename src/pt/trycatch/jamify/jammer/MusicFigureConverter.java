package pt.trycatch.jamify.jammer;


public class MusicFigureConverter {
	public static String getMusicFigure(Double val, int denominator){
			if(val == MusicFiguresEnum.Semibreve.figure()*denominator){
				return "w";
			}
			else if(val == MusicFiguresEnum.Minima.figure()*denominator){
				return "h";
			}
			else if(val == MusicFiguresEnum.SeMinima.figure()*denominator){
				return "q";
			}
			else if(val == MusicFiguresEnum.Colcheia.figure()*denominator){
				return "i";
			}
			else if(val == MusicFiguresEnum.SemiColcheia.figure()*denominator){
				return "s";
			}
			else if(val == MusicFiguresEnum.Fusa.figure()*denominator){
				return "t";
			}
			else if(val == MusicFiguresEnum.SemiFusa.figure()*denominator){
				return "x";
			}
		return null;
	}
}
