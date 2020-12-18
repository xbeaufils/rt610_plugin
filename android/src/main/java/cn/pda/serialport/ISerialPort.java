package cn.pda.serialport;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISerialPort {
    public InputStream getInputStream();

    public OutputStream getOutputStream();
    public void power_5Von();
    public void power_5Voff();
    public void power_3v3on();
    public void power_3v3off();
    public void rfid_poweron();
    public void rfid_poweroff();
    public void psam_poweron();
    public void psam_poweroff() ;
    public void scaner_poweron();
    public void scaner_poweroff() ;
    public void scaner_trigon() ;
    public void rfidPoweron();
    public void rfidPoweroff();
    public void close(int port);

}
