/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package cn.pda.serialport;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * SerialPort for open device power
 */
public class MockSerialPort implements ISerialPort{
	public static final int Power_3v3 = 0;
	public static final int Power_5v = 1;
	public static final int Power_Scaner = 2;
	public static final int Power_Psam = 3;
	public static final int Power_Rfid = 4;
	public static final int com0 = 0;
	public static final int com1 = 1;
	public static final int com2 = 2;
	public static final int com3 = 3;
	public static final int com4 = 4;
	public static final int com5 = 5;
	public static final int com6 = 6;
	public static final int com7 = 7;
	public static final int com8 = 8;
	public static final int com9 = 9;
	public static final int com10 = 10;
	public static final int com11 = 11;
	public static final int com12 = 12;
	public static final int com13 = 13;
	public static final int com14 = 14;
	public static final int com15 = 15;
	public static final int com16 = 16;
	public static final int com17 = 17;
	public static final int com18 = 18;
	public static final int com19 = 19;
	public static final int com20 = 20;
	public static final int com21 = 21;
	public static final int com22 = 22;
	public static final int com23 = 23;
	public static final int com24 = 24;
	public static final int com25 = 25;
	public static final int com26 = 26;
	public static final int com27 = 27;
	public static final int com28 = 28;
	public static final int com29 = 29;
	public static final int baudrate1200 = 1200;
	public static final int baudrate2400 = 2400;
	public static final int baudrate4800 = 4800;
	public static final int baudrate9600 = 9600;
	public static final int baudrate14400 = 14400;
	public static final int baudrate19200 = 19200;
	public static final int baudrate38400 = 38400;
	public static final int baudrate56000 = 56000;
	public static final int baudrate57600 = 57600;
	public static final int baudrate115200 = 115200;
	public static final int baudrate128000 = 128000;
	public static final int baudrate256000 = 256000;
	private static final String TAG = "MockSerialPort";


	public static int TNCOM_EVENPARITY = 0;//
	public static int TNCOM_ODDPARITY = 1 ;//

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private boolean trig_on=false;
	byte[] test;
	//
	public MockSerialPort(){}

	public MockSerialPort(int port, int baudrate, int flags) throws SecurityException, IOException {
	}
	// Getters and setters
	public InputStream getInputStream() {
		byte[] initialArray = { 0x02,0x30,0x31,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x43,0x39,0x30,0x30,0x30}; //,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x7B,(byte)0x84,0x03 };
		//byte[] initialArray = { 0,2,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,3,0,0,0,0xF,0xF,0,7};
		//InputStream targetStream = new ByteArrayInputStream(initialArray);
		//return targetStream;
		return new MockInputStream(initialArray);
	}

	public OutputStream getOutputStream() {
		return new MockOutputStream();
	}
	public void power_5Von() {
		zigbeepoweron();
	}
	public void power_5Voff(){
		zigbeepoweroff();
	}
	public void power_3v3on(){
		power3v3on();
	}
	public void power_3v3off(){
		power3v3off();
	}
	public void rfid_poweron(){
		//rfidPoweron();
	}
	public void rfid_poweroff(){
		//rfidPoweroff();
	}
	public void psam_poweron() {
	}
	public void psam_poweroff() {
		//scaner_trigoff();
	}
	public void scaner_poweron() {
		scanerpoweron();
		scaner_trigoff();
	}
	public void scaner_poweroff() {
		scanerpoweroff();
		//scaner_trigoff();
	}
	public void scaner_trigon() {
		scanertrigeron();
		trig_on=true;
	}
	public void scaner_trigoff() {
		scanertrigeroff();
		trig_on=false;
	}
	public boolean scaner_trig_stat(){
		return trig_on;
	}

	private native static FileDescriptor open(int port, int baudrate);
	private native static FileDescriptor open(int port, int baudrate, int portparity);
	public void close(int port) {};
	public native void zigbeepoweron();
	public native void zigbeepoweroff();
	
	public native void scanerpoweron();
	public native void scanerpoweroff();
	public native void psampoweron();
	public native void psampoweroff();
	public native void scanertrigeron();
	public native void scanertrigeroff();
	public native void power3v3on();
	public native void power3v3off();
	
	public native void rfidPoweron();
	public native void rfidPoweroff();
	
	public native void usbOTGpowerOn();
	public native void usbOTGpowerOff();
	
	public native void irdapoweron();
	public native void irdapoweroff();
	
	public native void setGPIOhigh(int gpio);
	public native void setGPIOlow(int gpio);
	
	
//	public native void setPortParity(int mode); //
	
	public native void test(byte[] bytes);

	static {
		System.loadLibrary("devapi");
		System.loadLibrary("irdaSerialPort");
	}

}
