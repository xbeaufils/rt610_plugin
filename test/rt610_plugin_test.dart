import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:rt610_plugin/rt610_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('rt610_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Rt610Plugin.platformVersion, '42');
  });
}
