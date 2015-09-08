package pt.trycatch.jamify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.jammer.SongChord;
import android.util.Log;

public class BackendRequestThread extends Thread {

	private static String SERVER_NAME = "146.193.41.48";
	private static int SERVER_PORT = 10005;

	private String mFileName;
	private String result;

	public BackendRequestThread(String fileName) {
		this.mFileName = fileName;
	}

	public static Song getAllInfoOnFile(String fileName) {
		BackendRequestThread t = new BackendRequestThread(fileName);
		t.start();
		try {
			t.join();
		} catch (Exception e) {
			Log.e("Error on asynch thread", e.getMessage());
		}
		String result = t.result;
		return parseSong(result);
	}
	
	public static Song parseSong(String song) {
		int bpm = 0;
		int tpm = 0;
		int num = 0;
		int den = 0;
		List<SongChord> chords = new ArrayList<SongChord>();
		
		String[] parts = song.split(";");
		bpm = 70;//Integer.parseInt(parts[0]);
		tpm = Integer.parseInt(parts[1]);
		num = Integer.parseInt(parts[2]);
		den = Integer.parseInt(parts[3]);
		
		String logDebug = "";
		for (int i = 4; i < parts.length; i++) {
			String[] chordParts = parts[i].split(":");
			String chordName = chordParts[1];
			chordName = chordName.replace(" " , "");
			chordName = chordName.replace("Minor" , "min");
			chordName = chordName.replace("Major" , "maj");
			chordName = chordName.replace("Augmented" , "aug");
			chordName = chordName.replace("Diminished" , "dim");
			logDebug += chordName + " | ";
			SongChord c = new SongChord(chordName, Double.parseDouble(chordParts[0]) * 1000);
			chords.add(c);
		}
		Log.d("Chords detected", logDebug);
		
		SongChord previousChord = null;
		double startTime = 0.0;
		Iterator<SongChord> iter = chords.iterator();
		while (iter.hasNext()) {
			SongChord currentChord = iter.next();
			if (previousChord == null) {
				previousChord = currentChord;
				startTime = currentChord.getInit();
				currentChord.init = currentChord.init - startTime;
				continue;
			}
			currentChord.init = currentChord.init - startTime;
			previousChord.setEnd(currentChord.getInit());
			
			if (previousChord.getChordName().equals(currentChord.getChordName())) {
				previousChord.toRemove = true;
				currentChord.init = previousChord.init;
			}
			
			if ((previousChord.end - previousChord.init) < 1.5 * 1000) {
				previousChord.toRemove = true;
			}
			
			if (previousChord.end > 30.0 * 1000) {
				previousChord.toRemove = true;
			}
			
			previousChord = currentChord;
		}
		if (previousChord != null) {
			previousChord.setEnd(previousChord.getInit() + 2*1000);
			
			iter = chords.iterator();
			while (iter.hasNext()) {
				SongChord currentChord = iter.next();
				if (currentChord.toRemove) {
					iter.remove();
				}
			}
		}
		
		Song result = new Song(chords, bpm, tpm, num, den);
		return result;
	}

	@Override
	public void run() {
		Socket socket = null;
		File myFile = new File (mFileName);
		FileInputStream fis;
		OutputStream os = null;
		BufferedReader in = null;
		try {
			socket = new Socket(SERVER_NAME, SERVER_PORT);
			fis = new FileInputStream(myFile);
			os = socket.getOutputStream();
			byte [] buffer  = new byte [8092];
			int bytesRead = 0;
			while ((bytesRead = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, bytesRead);
				Log.d("Sockets", "sent bytes: " + bytesRead);
				os.flush();
			}
			os.close();
			socket.close();

			socket = new Socket(SERVER_NAME, SERVER_PORT+1);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = in.readLine();
			Log.d("Answer from socket", line);

			in.close();
			Log.d("Client", "Client sent message");
			socket.close();
			
			this.result = line;
		} catch (Exception e) {
			try {
				if (in != null) { in.close(); }
				if (os != null) { os.close(); }
				if (socket != null) { socket.close(); }
			} catch (Exception e2) {}
			Log.e("Error on sockets", e.getMessage());
		}

	}


}
