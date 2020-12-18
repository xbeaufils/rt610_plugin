package fr.nemesys.service.rfid;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.handheld.LF134K.LF134KManager;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.sentry.core.Sentry;

public class RFIDService extends Service  {
    private final IBinder binder = new RFIDBinder();

    /* access modifiers changed from: private */
    private static LF134KManager lf134k;
    private String TAG = "RFIDService";
    public static int LOG = 2020;
    /* access modifiers changed from: private */

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RFIDBinder extends Binder {
        RFIDService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RFIDService.this;
        }
    }
    /* access modifiers changed from: private */
    public  class RFIDHandler extends Handler {

        public void sendLog (String key, String message) {
            Bundle bundle = new Bundle();
            bundle.putString("key", key);
            bundle.putString("log", message);
            Message msg = new Message();
            msg.what = 	RFIDService.LOG;
            msg.setData(bundle);
            this.sendMessage(msg);
        }

        public void handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            //RFIDService.this.sendLog("RFIDServiceMessage", msg.toString());
            if (msg.what == LF134KManager.MSG_RFID_134K) {
                Bundle bundle = msg.getData();
                Integer data = bundle.getInt(LF134KManager.KEY_134K_ID);
                if (data != null) {
                    RFIDService.this.sendToInput(bundle);
                }
            }
            if (msg.what == RFIDService.LOG) {
                Bundle bundle = msg.getData();
                String log = bundle.getString("log");
                String key = bundle.getString("key");
                RFIDService.this.sendLog(key, log);
            }
        }
    }
    public RFIDHandler handler = new RFIDHandler();

    private BroadcastReceiver killReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("kill2", false)) {
                Log.d(TAG, "killReceiver" );
                RFIDService.this.stopSelf();
            }
        }
    };

    private BroadcastReceiver readReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        RFIDService.this.sendLog("readReceiver","readReceiver");
        String action = intent.getAction();
        Log.d(TAG + ":readReceiver", "Action :" + action );
        if (action.equals("nemesys.rfid.LF134.read")) {
            if (RFIDService.lf134k != null) {
                try {
                    RFIDService.lf134k.startRead();
                    //RFIDService.lf134k.scan();
                } catch (Exception e) {
                    Sentry.captureException(e);
                    Log.e(TAG + ":readReceiver", "ScanThread error", e);
                    RFIDService.this.sendLog("RFID:OnstartCommand", getStackTrace(e) );
                }
            }
            else {
                RFIDService.this.sendLog("readReceiver","Thread not startd");
                Log.e(TAG + ":readReceiver", "Thread not startd");
            }
        }
         }
    };

    public IBinder onBind(Intent arg0) {
        return binder;
    }

    public void onCreate() {
        Log.d(this.TAG, "create");
        Util.initSoundPool(this);
        IntentFilter killfilter = new IntentFilter();
        killfilter.addAction("android.rfid.KILL_SERVER");
        registerReceiver(this.killReceiver, killfilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction("nemesys.rfid.LF134.read");
        registerReceiver(this.readReceiver, filter);

        super.onCreate();
        Log.d(this.TAG, "create");
        this.sendLog(TAG, "Create");
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.killReceiver);
        unregisterReceiver(this.readReceiver);
        Log.d(this.TAG, "Destroy");
        this.sendLog(TAG, "Destroy");
    }

     public int onStartCommand(Intent intent, int flags, int startId) {
         Log.d(this.TAG, "StartCommand");
         this.sendLog(TAG, "StartCommand");
         if (lf134k == null) {
            try {
                lf134k = new LF134KManager() ;//(this.handler, this);
                lf134k.setHandler(new RFIDHandler());
            } catch (Exception e) {
                Sentry.captureException(e);
                this.sendLog(TAG + ":OnstartCommand", getStackTrace(e) );
                Log.e(TAG + ":OnstartCommand", "ScanThread error", e);
            }
        } else {
            try {
                lf134k.Close();
                lf134k = new LF134KManager() ; //rfidThread = new RFIDThread(this.handler, this);
                lf134k.setHandler(new RFIDHandler());
            } catch (Exception e) {
                Sentry.captureException(e);
                this.sendLog("RFID:OnstartCommand", getStackTrace(e) );
                Log.e(TAG + ":OnstartCommand", "ScanThread error", e);
            }
        }
        Notification notification = new NotificationCompat.Builder(this)
                 .setContentTitle("AndroidMonks Sticker")
                 .setTicker("AndroidMonks Sticker")
                 .setContentText("Example")
                 //.setSmallIcon(R.drawable.ic_shape_1)
                 //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                 .setOngoing(true).build();
        startForeground(1001,notification);
        return START_STICKY; //super.onStartCommand(intent, flags, startId);
    }

    private void sendLog(String key, String message) {
        Intent logIntent = new Intent();
        logIntent.setAction("nemesys.rfid.LF134.log");
        logIntent.putExtra("key", key);
        logIntent.putExtra("log", message);
        sendBroadcast(logIntent);
    }

    public static void Close() {
        Log.d("RFIDService", "Close: ");
        if (lf134k != null) {
            lf134k.Close();
            lf134k = null;
        }
    }

    /* access modifiers changed from: private */
    public void sendToInput(Bundle bundle) {
        try {
            int intData = bundle.getInt(LF134KManager.KEY_134K_ID);
            String data = new Integer(intData).toString();
            int intNation = bundle.getInt(LF134KManager.KEY_134K_COUNTRY);
            String nation = new Integer(intNation).toString();
            //String type = bundle.getString("type");
            int datalent = data.length();
            int nationlent = nation.length();
            for (int i = 0; i < 12 - datalent; i++) {
                data = "0" + data;
            }
            for (int j = 0; j < 3 - nationlent; j++) {
                nation = "0" + nation;
            }
            Intent toBack = new Intent();
            toBack.setAction("nemesys.rfid.LF134.result");
            toBack.putExtra("id", data);
            toBack.putExtra("nation", nation);
            //toBack.putExtra("type", type);
            sendBroadcast(toBack);
            this.lf134k.stopRead();
            //this.lf134k.Close();
            //this.lf134k.interrupt();
            //this.lf134k.runFlag = false;
        }
        catch (Exception e) {
            this.sendLog(TAG + ":sendToInput", getStackTrace(e) );
            Sentry.captureException(e);
        }
    }

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
