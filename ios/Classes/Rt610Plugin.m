#import "Rt610Plugin.h"
#if __has_include(<rt610_plugin/rt610_plugin-Swift.h>)
#import <rt610_plugin/rt610_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "rt610_plugin-Swift.h"
#endif

@implementation Rt610Plugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftRt610Plugin registerWithRegistrar:registrar];
}
@end
