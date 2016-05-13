#import <UIKit/UIKit.h>
#import "RCTBridgeModule.h"
#import "MiPushSDK.h"

@interface RCTMiPush : NSObject <RCTBridgeModule, MiPushSDKDelegate>

+ (void)didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings;
+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
+ (void)didFailToRegisterForRemoteNotificationsWithError:(NSError *)err;
+ (void)didReceiveRemoteNotification:(NSDictionary *)notification;
+ (void)didReceiveLocalNotification:(UILocalNotification *)notification;

@end
