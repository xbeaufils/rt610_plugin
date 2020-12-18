package com.handheld.LF134K;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.pda.serialport.ISerialPort;
import cn.pda.serialport.MockSerialPort;
import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;

/**
 * 134.2K接口 interface
 * @author admin
 *
 */
public class LF134KManager {
    private ISerialPort mSerialport ;
    private InputStream mIn ;
    private OutputStream mOut ;
    public static int Port = 12 ;
    public static int BaudRate = 9600 ;
    public static int Power = SerialPort.Power_Psam;
    public static final int MSG_RFID_134K = 1101 ;
    public static final String KEY_134K_ID = "134k_id" ;
    public static final String KEY_134K_COUNTRY = "134k_country" ;

    private Handler handler = null ;
    private boolean running = true ;
    private boolean startFlag = false ;

    private String tag = "Rfid134k" ;
    public LF134KManager() throws SecurityException, IOException{
        //mSerialport = new SerialPort(Port, BaudRate,0) ;
        mSerialport = new MockSerialPort(Port, BaudRate,0) ;
//		Log.e("port", Port+":"+BaudRate+":"+Power);
        //open power
        switch (Power) {
            case SerialPort.Power_Scaner:
                mSerialport.scaner_poweron();
                break;
            case SerialPort.Power_3v3:
                mSerialport.power_3v3on();
                break;
            case SerialPort.Power_5v:
                mSerialport.power_5Von();
                break;
            case SerialPort.Power_Psam:
                mSerialport.psam_poweron();
                break;
            case SerialPort.Power_Rfid:
                mSerialport.rfid_poweron();
                break;
        }
        mIn = mSerialport.getInputStream() ;
        mOut = mSerialport.getOutputStream() ;
        sleep(500) ;
        //clear useless data
        byte[] temp = new byte[16] ;
        mIn.read(temp);
        readThread = new ReadThread();
        readThread.start();
    }
    ReadThread readThread;
    public void setHandler(Handler handler){
        this.handler = handler ;
    }


    //read id thread
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(running){
                if(startFlag){
//					Log.e("134.2k", "running");
                    LF134KManager.sleep(10) ;
                    findCardCMD() ;//发鿁指仿 Imitation
                    getRecv() ;
                }
            }
        }
    }

    //获取串口返回数据 Obtenir les données de retour du port série
    private byte[] getRecv(){
        byte[] buffer = new byte[512] ;
        byte[] recv = null ;
        int size = 0 ;
        int available = 0 ;
        try{
            available = mIn.available() ;
            if(available > 0){
                sleep(40) ;
                size = mIn.read(buffer) ;
                if(size > 0){
                    recv = new byte[size] ;
                    System.arraycopy(buffer, 0, recv, 0, size) ;
                    if(recv != null){
                        Log.d("getRecv:",  Tools.Bytes2HexString(recv, recv.length)) ;
                        resolveData(recv) ;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace() ;
        }
        return recv ;
    }

    //解析返回数据匿 Analyse et renvoi des données
    private Map<Integer, Integer> resolveData(byte[] recv){
        Log.d("LF134Manager:resolve", Tools.Bytes2HexString(recv, recv.length));
        Map<Integer, Integer> map = null;
        int countryCode ;
        int id ;
        //丿条完整的数据包长度为88 La longueur d'un paquet complet est de 88
        if(recv.length > 7){
            //E7 03 C6 FB 65 08 00 8F
            map = new HashMap<Integer, Integer>();
            //如国家代码 Tels que le code du pays：E7 03--->0x03E7--十进制 Décimal int--->999
            countryCode = (recv[0]&0xff) + (recv[1] &0xff)*256 ;
            //ID：C6 FB 65 08 00--->0x000865FBC6--十进制 Décimal int--->140901318
            id = ((recv[2]&0xff) + (recv[3] &0xff)*256 + (recv[4] &0xff)*256*256
                    + (recv[5] &0xff)*256*256*256 + (recv[6] &0xff)*256*256*256*256) ;
            //返回数据 Renvoyer les données
            sendMsg(countryCode, id);
            map.put(id, countryCode) ;
        }
        return map ;
    }

    private void sendMsg(int countryCode, int id){
        if(handler != null){
            Message msg = new Message() ;
            Bundle bundle = new Bundle() ;
            msg.what = MSG_RFID_134K ;
            bundle.putInt(KEY_134K_ID, id) ;
            bundle.putInt(KEY_134K_COUNTRY, countryCode) ;
            msg.setData(bundle) ;
            handler.sendMessage(msg) ;
        }
    }


//	//计算校验咿 Vérification du calcul
//	private byte checkCRC(byte[] recv){
//		byte crc = 0 ;
//		for(int i = 0 ; i < 7 ; i++){
//			crc = (byte) (crc^recv[i]) ;
//		}
//		return crc ;
//	}

    //发鿁寻卡指仿 // Trouvez l'imitation du doigt de la carte
    private void findCardCMD(){
        byte[] cmd = new byte[]{(byte)0xAA};
        try {
            mOut.write(cmd) ;
            mOut.flush() ;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //弿始读卿  Lire Qing
    public void startRead(){
        startFlag = true ;
        mSerialport.rfid_poweron() ;

    }

    //停止读卡 Arrêter de lire la carte
    public void stopRead(){
        startFlag = false ;
        mSerialport.rfid_poweroff() ;

    }

    //close rfid reader
    public void Close(){
        try{
            running = false ;
            startFlag = false ;
            readThread.interrupt();
            sleep(100) ;
            if(mOut != null){
                mOut.close() ;
            }
            if(mIn != null){
                mIn.close() ;
            }
            if(mSerialport != null){
                switch (Power) {
                    case SerialPort.Power_Scaner:
                        mSerialport.scaner_poweroff();
                        break;
                    case SerialPort.Power_3v3:
                        mSerialport.power_3v3off();
                        break;
                    case SerialPort.Power_5v:
                        mSerialport.power_5Voff();
                        break;
                    case SerialPort.Power_Psam:
                        mSerialport.psam_poweroff();
                        break;
                    case SerialPort.Power_Rfid:
                        mSerialport.rfid_poweroff();
                        break;
                }
                mSerialport.close(Port) ;
                Log.d("LF134Manager", "Close port " + Port);
            }
        }catch(Exception e){

        }
    }

    //delay
    private static void sleep(final int time ){
        try {
            Thread.sleep(time) ;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
