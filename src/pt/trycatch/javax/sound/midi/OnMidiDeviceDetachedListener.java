package pt.trycatch.javax.sound.midi;

import android.hardware.usb.UsbDevice;

/**
 * Listener for MIDI detached events
 * 
 * @author K.Shoji
 */
public interface OnMidiDeviceDetachedListener {
	/**
	 * device has been detached
	 * 
	 * @param usbDevice
	 */
	void onDeviceDetached(UsbDevice usbDevice);
}
