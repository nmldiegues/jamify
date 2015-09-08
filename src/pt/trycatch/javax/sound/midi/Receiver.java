package pt.trycatch.javax.sound.midi;

public interface Receiver {
	void send(MidiMessage message, long timeStamp);

	void close();
}
