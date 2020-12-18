
import 'dart:async';

import 'package:flutter/services.dart';

class Rt610Plugin {
  static const MethodChannel _channel =
      const MethodChannel('rt610_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> start() async {
    final String version = await _channel.invokeMethod('start');
    return version;
  }

  static Future<String> read() async {
    final String version = await _channel.invokeMethod('startRead');
    return version;
  }

  static Future<String> stop() async {
    final String version = await _channel.invokeMethod('stop');
    return version;
  }

}
