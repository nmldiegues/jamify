package pt.trycatch.jamify.jammer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class SongLoader {
	
	public static Song loadSong(String filePath, int bpm){
		List<SongChord> chords = new ArrayList<SongChord>();
		try {
			//Find the directory for the SD Card using the API
			//*Don't* hardcode "/sdcard"
			String sdcard = Environment.getExternalStorageDirectory().getPath();

			//Get the text file
			File file = new File(sdcard + "/" + filePath);
			String line;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				String[] chordLine = line.split(" ");
				chords.add(new SongChord(chordLine[0], Double.parseDouble(chordLine[1]), Double.parseDouble(chordLine[2])));
			}
			br.close();
			// FIXME if this is needed
			return new Song(chords, bpm, -1, -1, -1);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
