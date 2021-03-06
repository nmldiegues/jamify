package pt.trycatch.javax.sound.midi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * MidiSystem porting for Android USB MIDI.<br />
 * Implemented Receiver and Transmitter only.
 * 
 * @author K.Shoji
 */
public final class MidiSystem {
	static List<DeviceFilter> deviceFilters = null;
	static Set<UsbMidiDevice> midiDevices = null;
	static Map<UsbDevice, UsbDeviceConnection> deviceConnections;
	static OnMidiDeviceAttachedListener deviceAttachedListener = null;
	static OnMidiDeviceDetachedListener deviceDetachedListener = null;
	static MidiDeviceConnectionWatcher deviceConnectionWatcher = null;
	static OnMidiSystemEventListener systemEventListener = null;

	/**
	 * Find {@link Set<UsbMidiDevice>} from {@link UsbDevice}<br />
	 * method for jp.kshoji.javax.sound.midi package.
	 * 
	 * @param usbDevice
	 * @param usbDeviceConnection 
	 * @return {@link Set<UsbMidiDevice>}, always not null
	 */
	static Set<UsbMidiDevice> findAllUsbMidiDevices(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
		Set<UsbMidiDevice> result = new HashSet<UsbMidiDevice>();

		Set<UsbInterface> interfaces = UsbMidiDeviceUtils.findAllMidiInterfaces(usbDevice, deviceFilters);
		for (UsbInterface usbInterface : interfaces) {
			UsbEndpoint inputEndpoint = UsbMidiDeviceUtils.findMidiEndpoint(usbDevice, usbInterface, UsbConstants.USB_DIR_IN, deviceFilters);
			UsbEndpoint outputEndpoint = UsbMidiDeviceUtils.findMidiEndpoint(usbDevice, usbInterface, UsbConstants.USB_DIR_OUT, deviceFilters);

			result.add(new UsbMidiDevice(usbDevice, usbDeviceConnection, usbInterface, inputEndpoint, outputEndpoint));
		}

		return Collections.unmodifiableSet(result);
	}


	/**
	 * Implementation for multiple device connections.
	 * 
	 * @author K.Shoji
	 */
	static final class OnMidiDeviceAttachedListenerImpl implements OnMidiDeviceAttachedListener {
		private final UsbManager usbManager;

		/**
		 * constructor
		 * 
		 * @param usbManager
		 */
		public OnMidiDeviceAttachedListenerImpl(UsbManager usbManager) {
			this.usbManager = usbManager;
		}

		/*
		 * (non-Javadoc)
		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener#onDeviceAttached(android.hardware.usb.UsbDevice)
		 */
		@Override
		public synchronized void onDeviceAttached(UsbDevice attachedDevice) {
			deviceConnectionWatcher.notifyDeviceGranted();

			UsbDeviceConnection deviceConnection = usbManager.openDevice(attachedDevice);			
			if (deviceConnection == null) {
				return;
			}

			synchronized (deviceConnection) {
				deviceConnections.put(attachedDevice, deviceConnection);
			}

			synchronized (midiDevices) {
				midiDevices.addAll(findAllUsbMidiDevices(attachedDevice, deviceConnection));
			}

			Log.d(Constants.TAG, "Device " + attachedDevice.getDeviceName() + " has been attached.");

			if (systemEventListener != null) {
				systemEventListener.onMidiSystemChanged();
			}
		}
	}

	/**
	 * Implementation for multiple device connections.
	 * 
	 * @author K.Shoji
	 */
	static final class OnMidiDeviceDetachedListenerImpl implements OnMidiDeviceDetachedListener {
		/*
		 * (non-Javadoc)
		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener#onDeviceDetached(android.hardware.usb.UsbDevice)
		 */
		@Override
		public void onDeviceDetached(UsbDevice detachedDevice) {
			UsbDeviceConnection usbDeviceConnection;
			synchronized (deviceConnections) {
				usbDeviceConnection = deviceConnections.get(detachedDevice);
			}

			if (usbDeviceConnection == null) {
				return;
			}

			Set<UsbMidiDevice> detachedMidiDevices = findAllUsbMidiDevices(detachedDevice, usbDeviceConnection);
			for (UsbMidiDevice usbMidiDevice : detachedMidiDevices) {
				usbMidiDevice.close();
			}

			synchronized (midiDevices) {
				midiDevices.removeAll(detachedMidiDevices);
			}

			Log.d(Constants.TAG, "Device " + detachedDevice.getDeviceName() + " has been detached.");

			if (systemEventListener != null) {
				systemEventListener.onMidiSystemChanged();
			}
		}
	}

	/**
	 * Listener for MidiSystem event listener
	 * 
	 * @author K.Shoji
	 */
	public interface OnMidiSystemEventListener {
		/**
		 * MidiSystem has been changed.
		 * (new device is connected, or disconnected.)
		 */
		void onMidiSystemChanged();
	}

	/**
	 * Set the listener of Device connection/disconnection
	 * @param listener
	 */
	public static void setOnMidiSystemEventListener(OnMidiSystemEventListener listener) {
		systemEventListener = listener;
	}

	/**
	 * Initializes MidiSystem
	 * 
	 * @param context
	 * @throws NullPointerException
	 */
	public static void initialize(Context context) throws NullPointerException {
		if (context == null) {
			throw new NullPointerException("context is null");
		}

		UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		if (usbManager == null) {
			throw new NullPointerException("UsbManager is null");
		}

		deviceFilters = DeviceFilter.getDeviceFilters(context);
		midiDevices = new HashSet<UsbMidiDevice>();
		deviceConnections = new HashMap<UsbDevice, UsbDeviceConnection>();
		deviceAttachedListener = new OnMidiDeviceAttachedListenerImpl(usbManager);
		deviceDetachedListener = new OnMidiDeviceDetachedListenerImpl();
		deviceConnectionWatcher = new MidiDeviceConnectionWatcher(context, usbManager, deviceAttachedListener, deviceDetachedListener);
	}

	/**
	 * Terminates MidiSystem
	 */
	public static void terminate() {
		if (midiDevices != null) {
			synchronized (midiDevices) {
				for (UsbMidiDevice midiDevice : midiDevices) {
					midiDevice.close();
				}
				midiDevices.clear();
			}
		}
		midiDevices = null;

		if (deviceConnections != null) {
			synchronized (deviceConnections) {
				deviceConnections.clear();
			}
		}
		deviceConnections = null;

		if (deviceConnectionWatcher != null) {
			deviceConnectionWatcher.stop();
		}
		deviceConnectionWatcher = null;
	}

	private MidiSystem() {
	}

	/**
	 * get all connected {@link MidiDevice.Info} as array
	 * 
	 * @return device informations
	 */
	public static MidiDevice.Info[] getMidiDeviceInfo() {
		List<MidiDevice.Info> result = new ArrayList<MidiDevice.Info>();
		if (midiDevices != null) {
			for (MidiDevice midiDevice : midiDevices) {
				result.add(midiDevice.getDeviceInfo());
			}
		}
		return result.toArray(new MidiDevice.Info[0]);
	}

	/**
	 * get {@link MidiDevice} by device information
	 * 
	 * @param info
	 * @return {@link MidiDevice}
	 * @throws MidiUnavailableException
	 */
	public static MidiDevice getMidiDevice(MidiDevice.Info info) throws MidiUnavailableException {
		if (midiDevices != null) {
			for (MidiDevice midiDevice : midiDevices) {
				if (info.equals(midiDevice.getDeviceInfo())) {
					return midiDevice;
				}
			}
		}

		throw new IllegalArgumentException("Requested device not installed: " + info);
	}

	/**
	 * get the first detected Receiver
	 * 
	 * @return {@link Receiver}
	 * @throws MidiUnavailableException
	 */
	public static Receiver getReceiver() throws MidiUnavailableException {
		if (midiDevices != null) {
			for (MidiDevice midiDevice : midiDevices) {
				Receiver receiver = midiDevice.getReceiver();
				if (receiver != null) {
					return receiver;
				}
			}
		}
		return null;
	}

	/**
	 * get the first detected Transmitter
	 * 
	 * @return {@link Transmitter}
	 * @throws MidiUnavailableException
	 */
	public static Transmitter getTransmitter() throws MidiUnavailableException {
		if (midiDevices != null) {
			for (MidiDevice midiDevice : midiDevices) {
				Transmitter transmitter = midiDevice.getTransmitter();
				if (transmitter != null) {
					return transmitter;
				}
			}
		}
		return null;
	}

	public static int[] getMidiFileTypes(Sequence sequence) {

		Set allTypes = new HashSet();

		// gather from all the providers

		MidiFileWriter writer = new StandardMidiFileWriter();
		int[] types = writer.getMidiFileTypes(sequence);
		for (int j = 0; j < types.length; j++ ) {
			allTypes.add(new Integer(types[j]));
		}

		int resultTypes[] = new int[allTypes.size()];
		int index = 0;
		Iterator iterator = allTypes.iterator();
		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			resultTypes[index++] = integer.intValue();
		}
		return resultTypes;
	}

	public static int write(Sequence in, int type, File out) throws IOException {

		int bytesWritten = -2;

		MidiFileWriter writer = new StandardMidiFileWriter();
		if( writer.isFileTypeSupported( type, in ) ) {

			bytesWritten = writer.write(in, type, out);
		}

		if (bytesWritten == -2) {
			throw new IllegalArgumentException("MIDI file type is not supported");
		}
		return bytesWritten;
	}
	
	public static Sequence getSequence(InputStream stream)
			throws InvalidMidiDataException, IOException {

		Sequence sequence = null;

			MidiFileReader reader = new StandardMidiFileReader();
			try {
				sequence = reader.getSequence( stream ); // throws IOException
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

		if( sequence==null ) {
			throw new InvalidMidiDataException("could not get sequence from input stream");
		} else {
			return sequence;
		}
	}

	public static Sequencer getSequencer(boolean connected)
			throws MidiUnavailableException {
		Sequencer seq = new RealTimeSequencer();

		if (connected) {
			// IMPORTANT: this code needs to be synch'ed with
			//            all AutoConnectSequencer instances,
			//            (e.g. RealTimeSequencer) because the
			//            same algorithm for synth retrieval
			//            needs to be used!

			Receiver rec = null;
			MidiUnavailableException mue = null;

			// first try to connect to the default synthesizer
			// CHANGED DANGER
//			try {
//				Synthesizer synth = getSynthesizer();
//				if (synth instanceof ReferenceCountingDevice) {
//					rec = ((ReferenceCountingDevice) synth).getReceiverReferenceCounting();
//					// only use MixerSynth if it could successfully load a soundbank
//					if (synth.getClass().toString().contains("com.sun.media.sound.MixerSynth")
//							&& (synth.getDefaultSoundbank() == null)) {
//						// don't use this receiver if no soundbank available
//						rec = null;
//						synth.close();
//					}
//				} else {
//					synth.open();
//					try {
//						rec = synth.getReceiver();
//					} finally {
//						// make sure that the synth is properly closed
//						if (rec == null) {
//							synth.close();
//						}
//					}
//				}
//			} catch (MidiUnavailableException e) {
//				// something went wrong with synth
//				if (e instanceof MidiUnavailableException) {
//					mue = e;
//				}
//			}
			if (rec == null) {
				// then try to connect to the default Receiver
				try {
					rec = MidiSystem.getReceiver();
				} catch (Exception e) {
					// something went wrong. Nothing to do then!
					if (e instanceof MidiUnavailableException) {
						mue = (MidiUnavailableException) e;
					}
				}
			}
			if (rec != null) {
				seq.getTransmitter().setReceiver(rec);
				if (seq instanceof AutoConnectSequencer) {
					((AutoConnectSequencer) seq).setAutoConnect(rec);
				}
			} else {
				if (mue != null) {
					throw mue;
				}
				throw new MidiUnavailableException("no receiver available");
			}
		}
		return seq;
	}

}
