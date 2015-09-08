package pt.trycatch.javax.sound.midi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * Detects USB MIDI Device Connected
 * stop() method must be called when the application will be destroyed.
 * 
 * @author K.Shoji
 */
public final class MidiDeviceConnectionWatcher {
	private final MidiDeviceConnectionWatchThread thread;
	final HashMap<String, UsbDevice> grantedDeviceMap;
	final Queue<UsbDevice> deviceGrantQueue;
	volatile boolean isGranting;

	/**
	 * constructor
	 * 
	 * @param context
	 * @param usbManager
	 * @param deviceAttachedListener
	 * @param deviceDetachedListener
	 */
	public MidiDeviceConnectionWatcher(Context context, UsbManager usbManager, OnMidiDeviceAttachedListener deviceAttachedListener, OnMidiDeviceDetachedListener deviceDetachedListener) {
		deviceGrantQueue = new LinkedList<UsbDevice>();
		isGranting = false;
		grantedDeviceMap = new HashMap<String, UsbDevice>();
		thread = new MidiDeviceConnectionWatchThread(context, usbManager, deviceAttachedListener, deviceDetachedListener);
		thread.start();
	}
	
	public void checkConnectedDevicesImmediately() {
		thread.checkConnectedDevices();
	}
	
	/**
	 * stops the watching thread <br />
	 * <br />
	 * Note: Takes one second until the thread stops.
	 * The device attached / detached events will be noticed until the thread will completely stops.
	 */
	public void stop() {
		thread.stopFlag = true;
		
		// blocks while the thread will stop
		while (thread.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
	/**
	 * Broadcast receiver for MIDI device connection granted
	 * 
	 * @author K.Shoji
	 */
	private final class UsbMidiGrantedReceiver extends BroadcastReceiver {
		private static final String USB_PERMISSION_GRANTED_ACTION = "jp.kshoji.driver.midi.USB_PERMISSION_GRANTED_ACTION";
		
		private final String deviceName;
		private final UsbDevice device;
		private final OnMidiDeviceAttachedListener deviceAttachedListener;
		
		/**
		 * @param device
		 * @param deviceAttachedListener
		 */
		public UsbMidiGrantedReceiver(String deviceName, UsbDevice device, OnMidiDeviceAttachedListener deviceAttachedListener) {
			this.deviceName = deviceName;
			this.device = device;
			this.deviceAttachedListener = deviceAttachedListener;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (USB_PERMISSION_GRANTED_ACTION.equals(action)) {
				boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
				if (granted) {
					if (deviceAttachedListener != null && device != null) {
						grantedDeviceMap.put(deviceName, device);
						deviceAttachedListener.onDeviceAttached(device);
					}
				} else {
					// reset the 'isGranting' to false
					notifyDeviceGranted();
				}
			}
		}
	}
	
	/**
	 * USB Device polling thread
	 * 
	 * @author K.Shoji
	 */
	private final class MidiDeviceConnectionWatchThread extends Thread {
		private Context context;
		private UsbManager usbManager;
		private OnMidiDeviceAttachedListener deviceAttachedListener;
		private OnMidiDeviceDetachedListener deviceDetachedListener;
		private Set<String> connectedDeviceNameSet;
		private Set<String> removedDeviceNames;
		boolean stopFlag;
		private List<DeviceFilter> deviceFilters;

		/**
		 * constructor
		 * 
		 * @param context
		 * @param usbManager
		 * @param deviceAttachedListener
		 * @param deviceDetachedListener
		 */
		MidiDeviceConnectionWatchThread(Context context, UsbManager usbManager, OnMidiDeviceAttachedListener deviceAttachedListener, OnMidiDeviceDetachedListener deviceDetachedListener) {
			this.context = context;
			this.usbManager = usbManager;
			this.deviceAttachedListener = deviceAttachedListener;
			this.deviceDetachedListener = deviceDetachedListener;
			connectedDeviceNameSet = new HashSet<String>();
			removedDeviceNames = new HashSet<String>();
			stopFlag = false;
			deviceFilters = DeviceFilter.getDeviceFilters(context);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			
			while (stopFlag == false) {
				checkConnectedDevices();
				
				synchronized (deviceGrantQueue) {
					if (!deviceGrantQueue.isEmpty() && !isGranting) {
						isGranting = true;
						UsbDevice device = deviceGrantQueue.remove();
						
						PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(UsbMidiGrantedReceiver.USB_PERMISSION_GRANTED_ACTION), 0);
						context.registerReceiver(new UsbMidiGrantedReceiver(device.getDeviceName(), device, deviceAttachedListener), new IntentFilter(UsbMidiGrantedReceiver.USB_PERMISSION_GRANTED_ACTION));
						usbManager.requestPermission(device, permissionIntent);
					}
				}
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					Log.d(Constants.TAG, "Thread interrupted", e);
				}
			}
		}

		/**
		 * checks Attached/Detached devices
		 */
		synchronized void checkConnectedDevices() {
			HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
			
			// check attached device
			for (String deviceName : deviceMap.keySet()) {
				// check if already removed
				if (removedDeviceNames.contains(deviceName)) {
					continue;
				}
				
				if (!connectedDeviceNameSet.contains(deviceName)) {
					connectedDeviceNameSet.add(deviceName);
					UsbDevice device = deviceMap.get(deviceName);
					
					Set<UsbInterface> midiInterfaces = UsbMidiDeviceUtils.findAllMidiInterfaces(device, deviceFilters);
					if (midiInterfaces.size() > 0) {
						synchronized (deviceGrantQueue) {
							deviceGrantQueue.add(device);
						}
					}
				}
			}
			
			// check detached device
			for (String deviceName : connectedDeviceNameSet) {
				if (!deviceMap.containsKey(deviceName)) {
					removedDeviceNames.add(deviceName);
					UsbDevice device = grantedDeviceMap.remove(deviceName);

					Log.d(Constants.TAG, "deviceName:" + deviceName + ", device:" + device + " detached.");
					if (device != null) {
						deviceDetachedListener.onDeviceDetached(device);
					}
				}
			}
			
			connectedDeviceNameSet.removeAll(removedDeviceNames);
		}
	}

	/**
	 * notifies the 'current granting device' has successfully granted.
	 */
	public void notifyDeviceGranted() {
		isGranting = false;
	}
}
