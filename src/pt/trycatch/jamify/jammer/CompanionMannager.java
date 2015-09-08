package pt.trycatch.jamify.jammer;
import java.util.ArrayList;
import java.util.List;

import pt.trycatch.jamify.jammer.companion.BassCompanion;
import pt.trycatch.jamify.jammer.companion.DrumCompanion;
import pt.trycatch.jamify.jammer.companion.PianoCompanion;
import pt.trycatch.jfungue.Pattern;
import pt.trycatch.jfungue.Player;

public class CompanionMannager{
	private static List<int[]> chordDegreesToUse = new ArrayList<int[]>();
	private static int chordDegreesToUseIndex = 0;
	private String instrument;
	
	public CompanionMannager(String instrument){
		super();
		this.instrument = instrument;
	}
	
	public CompanionMannager(){
		chordDegreesToUse.add(new int[]{0,1});
		chordDegreesToUse.add(new int[]{1});
		chordDegreesToUse.add(new int[]{0,0});
		chordDegreesToUse.add(new int[]{1,1});
		this.instrument = "Piano";
	}
	
	public String getInstrument(){
		return instrument;
	}
	
	public void setInstrument(String instrument){
		this.instrument = instrument;
	}
	
	public static void main(String[] args)
	{
		/*Song s = SongLoader.loadSong("MusicCompanion/MardyBum.txt", 115);
		System.out.println(BassCompanion.improvise(s, 4, 115));
		Player player = new Player();
		Pattern song = new Pattern();
		Pattern track2 = new Pattern(BassCompanion.improvise(s, 4, 115));
		
		song.add(track2);
		player.play(song);
		try {
			player.saveMidi(song, new File("MardyBumBass.MIDI"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//System.out.println(s);
		/*Player player = new Player();
		Pattern song = new Pattern();
		Pattern track1 = new Pattern("V0 I[Guitar] C D E F");
		Pattern track2 = new Pattern("V1 I[SLAP_BASS_1] R D2 E2q R");
		
		song.add(track1);
		song.add(track2);
		song.add(track1);
		song.add(track2);
		song.add(track1);
		song.add(track2);
		song.add(track1);
		song.add(track2);
		player.play(song);*/
		
		
	}
	
	public String getImprovisation(Song s){
		String impro = null;
		String improPiano = PianoCompanion.improvise(s, s.denominator, s.bpm, chordDegreesToUse.get(chordDegreesToUseIndex));
		String improBass = BassCompanion.improvise(s, s.denominator, s.bpm, chordDegreesToUse.get(chordDegreesToUseIndex));
		String improDrums = DrumCompanion.improvise(s, s.denominator, s.bpm);
		if (getInstrument().equals("Piano")) {
			impro = improPiano;
		} else if (getInstrument().equals("Bass")) {
			impro = improBass;
		}
		else if (getInstrument().equals("Bass+Piano")) {
			impro = improBass + " " + improPiano;
		}
		else if (getInstrument().equals("Bass+Piano+Drums")) {
			impro = improBass + " " + improPiano + " " + improDrums;
		}
		
		chordDegreesToUseIndex = (chordDegreesToUseIndex + 1) % chordDegreesToUse.size();
		/*String improBass = BassCompanion.improvise(s, s.denominator, s.bpm, chordDegreesToUse.get(chordDegreesToUseIndex));
		String improPiano = PianoCompanion.improvise(s, s.denominator, s.bpm, chordDegreesToUse.get(chordDegreesToUseIndex));
		impro = DrumCompanion.improvise(s, s.denominator, s.bpm) + " " + improBass + " " + improPiano;*/
		return impro;
		//return DrumCompanion.improvise(s, s.denominator, s.bpm) + improBass;
	}
	
	public String getImrpovisation(){
		Song s = SongLoader.loadSong("MusicCompanion/MardyBum.txt", 115);
		//String impro =  BassCompanion.improvise(s, 4, 115, chordDegreesToUse.get(chordDegreesToUseIndex));
		String impro = PianoCompanion.improvise(s, 4, 115, chordDegreesToUse.get(chordDegreesToUseIndex));
		chordDegreesToUseIndex = (chordDegreesToUseIndex + 1) % chordDegreesToUse.size();
		return impro;
	}
	
	public static void test(){
		Player player = new Player();
		Pattern verse = new Pattern("V0 I[Piano] C D E F");
		Pattern chorus = new Pattern("V0 I[Piano] C A B G");
		Pattern song = new Pattern();
		song.add(verse);
		song.add(chorus);
		song.add(verse);
		song.add(chorus);
		song.add(verse);
		song.add(chorus);
		player.play(song);
		System.exit(0);
	}
}