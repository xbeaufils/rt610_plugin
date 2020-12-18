package fr.nemesys.rt610_plugin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

import fr.nemesys.service.rfid.RFIDService;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** Rt610Plugin */
public class Rt610Plugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "rt610_plugin");
    channel.setMethodCallHandler(this);
    this.context.registerReceiver(boucleReceiver, new IntentFilter(
            "nemesys.rfid.LF134.result"));
  }

  private BroadcastReceiver boucleReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context arg0, Intent intent) {
      String action = intent.getAction();
      Log.d("boucleReceiver", "action " + action);
      if (action.equals("nemesys.rfid.LF134.result")) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
          String id = extras.getString("id");
          String nation = extras.getString("nation");
          //String type = extras.getString("type");
          Log.d("boucleReceiver", "id " + id);
        }
      }
    }
  };

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    Log.d("RT610Plugin", "onMethodCall: " + call.method);
    if (call.method.equals("startRead")) {
      Intent toRead = new Intent();
      toRead.setAction("nemesys.rfid.LF134.read");
      this.context.sendBroadcast(toRead);
      result.success("startRead");
    } else  if  (call.method.equals("start") ){
      try {
        Intent intentRfid = new Intent();
        intentRfid.setComponent(new ComponentName("fr.nemesys.service.rfid", "fr.nemesys.service.rfid.RFIDService"));
        //MainActivity.this.startService(intentRfid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          Log.d("RT610Plugin", "Build version " + Build.VERSION.SDK_INT + " " + Build.VERSION_CODES.O);
          context.startForegroundService(intentRfid);
        } else {
          context.startService(intentRfid);
        } /*
        Intent toRead = new Intent();
        toRead.setAction("nemesys.rfid.LF134.read");
        result.success("start");

        MainActivity.this.sendBroadcast(toRead);
        new Timer().schedule(new TimerTask() {
          public void run() {
            Message msg = new Message();
            msg.what = MSG_CANSEL_DIALOG;
            MainActivity.this.mHandler.sendMessage(msg);
          }
        }, 2000);
         */
      }
      catch (Exception e) {
        Log.e("Plugin", "onMethodCall: ",e );
      }

      //result.notImplemented();
    } else if (call.method.equals("stopRead")) {

    } else if (call.method.equals("stop")) {
      Intent intentRfid = new Intent();
      intentRfid.setComponent(new ComponentName("fr.nemesys.service.rfid", "fr.nemesys.service.rfid.RFIDService"));
      context.stopService(intentRfid);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
