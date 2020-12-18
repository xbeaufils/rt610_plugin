
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:rt610_plugin/rt610_plugin.dart';

import 'dart:developer' as debug;
void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('rt610_plugin');
  final _dataCl = TextEditingController();
  final _logCl = TextEditingController();

  @override
  void initState() {
    super.initState();
    _connectToService();
  }

  Future<void> _connectToService() async {
    try {
      final result = await Rt610Plugin.start();
      return result;
    } on PlatformException catch (e) {
      debug.log("", name:"_connectToService", error: e);
      print(e.toString());
    }
  }


  Future<String> _getDataFromService() async {
    try {
      final result = await Rt610Plugin.read();
      return result;
    } on PlatformException catch (e) {
      debug.log("", name:"_getDataFromService", error: e);
      print(e.toString());
    }
    return 'No Data From Service';
  }


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin RFID RT610'),
        ),
        body:
          Column(
            children: [
             TextField(
               maxLines: null,
               controller: _dataCl,
               enabled: false,
               decoration: InputDecoration(
                   labelText: "Data Collectée",),
             ),
              ButtonBar(
                children: [
                  RaisedButton(
                    child: Text("Read"),
                      onPressed: _getDataFromService),
                  RaisedButton(
                      child: Text("Stop"),
                      onPressed: _stopService),
                  RaisedButton(
                    child: Text("Clear"),
                      onPressed: _clearPress)
              ],),
              TextField(
                maxLines: null,
                controller: _logCl,
                decoration: InputDecoration(
                  labelText: "Logs",),
              ),

            ],)
      ),
    );
  }

  void _clearPress() {
    this._dataCl.text="";
    this._logCl.text="";
    setState(() {

    });
  }

  Future<String> _stopService() async {
    try {
      final result = await Rt610Plugin.stop();
      return result;
    } on PlatformException catch (e) {
      debug.log("Main view", name:"_stopService", error: e);
      print(e.toString());
    }
    return 'No Data From Service';
  }
}
