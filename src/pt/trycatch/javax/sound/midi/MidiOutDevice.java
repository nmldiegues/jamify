/*
 * Copyright 1999-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package pt.trycatch.javax.sound.midi;




/**
 * MidiOutDevice class representing functionality of MidiOut devices.
 *
 * @author David Rivas
 * @author Kara Kytle
 * @author Florian Bomers
 */
class MidiOutDevice extends AbstractMidiDevice {

    // CONSTRUCTOR

    MidiOutDevice(AbstractMidiDeviceProvider.Info info) {
                super(info);
    }


    // IMPLEMENTATION OF ABSTRACT MIDI DEVICE METHODS

    @Override
	protected synchronized void implOpen() throws MidiUnavailableException {
        int index = ((AbstractMidiDeviceProvider.Info)getDeviceInfo()).getIndex();
        id = nOpen(index); // can throw MidiUnavailableException
        if (id == 0) {
            throw new MidiUnavailableException("Unable to open native device");
        }
    }


    @Override
	protected synchronized void implClose() {
        // prevent further action
        long oldId = id;
        id = 0;

        super.implClose();

        // close the device
        nClose(oldId);
    }


    @Override
	public long getMicrosecondPosition() {
        long timestamp = -1;
        if (isOpen()) {
            timestamp = nGetTimeStamp(id);
        }
        return timestamp;
    }



    // OVERRIDES OF ABSTRACT MIDI DEVICE METHODS

    /** Returns if this device supports Receivers.
        This implementation always returns true.
        @return true, if the device supports Receivers, false otherwise.
    */
    @Override
	protected boolean hasReceivers() {
        return true;
    }


    @Override
	protected Receiver createReceiver() {
        return new MidiOutReceiver();
    }


    // INNER CLASSES

    class MidiOutReceiver extends AbstractReceiver {

        @Override
		protected void implSend(MidiMessage message, long timeStamp) {
            int length = message.getLength();
            int status = message.getStatus();
            if (length <= 3 && status != 0xF0 && status != 0xF7) {
                int packedMsg;
                if (message instanceof ShortMessage) {
                    if (message instanceof FastShortMessage) {
                        packedMsg = ((FastShortMessage) message).getPackedMsg();
                    } else {
                        ShortMessage msg = (ShortMessage) message;
                        packedMsg = (status & 0xFF)
                            | ((msg.getData1() & 0xFF) << 8)
                            | ((msg.getData2() & 0xFF) << 16);
                    }
                } else {
                    packedMsg = 0;
                    byte[] data = message.getMessage();
                    if (length>0) {
                        packedMsg = data[0] & 0xFF;
                        if (length>1) {
                            /* We handle meta messages here. The message
                               system reset (FF) doesn't get until here,
                               because it's length is only 1. So if we see
                               a status byte of FF, it's sure that we
                               have a Meta message. */
                            if (status == 0xFF) {
                                return;
                            }
                            packedMsg |= (data[1] & 0xFF) << 8;
                            if (length>2) {
                                packedMsg |= (data[2] & 0xFF) << 16;
                            }
                        }
                    }
                }
                nSendShortMessage(id, packedMsg, timeStamp);
            } else {
                if (message instanceof FastSysexMessage) {
                    nSendLongMessage(id, ((FastSysexMessage) message).getReadOnlyMessage(),
                                     length, timeStamp);
                } else {
                    nSendLongMessage(id, message.getMessage(), length, timeStamp);
                }
            }
        }

        /** shortcut for the Sun implementation */
        synchronized void sendPackedMidiMessage(int packedMsg, long timeStamp) {
            if (isOpen() && id != 0) {
                nSendShortMessage(id, packedMsg, timeStamp);
            }
        }


    } // class MidiOutReceiver


    // NATIVE METHODS

    private native long nOpen(int index) throws MidiUnavailableException;
    private native void nClose(long id);

    private native void nSendShortMessage(long id, int packedMsg, long timeStamp);
    private native void nSendLongMessage(long id, byte[] data, int size, long timeStamp);
    private native long nGetTimeStamp(long id);

} // class MidiOutDevice
