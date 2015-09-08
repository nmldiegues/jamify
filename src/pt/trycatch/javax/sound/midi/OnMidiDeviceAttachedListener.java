package pt.trycatch.javax.sound.midi;

import android.hardware.usb.UsbDevice;

/**
 * Listener for MIDI attached events
 * 
 * @author K.Shoji
 */
public interface OnMidiDeviceAttachedListener {
	/**
	 * device has been attached
	 * 
	 * @param usbDevice
	 */
	void onDeviceAttached(UsbDevice usbDevice);
}
