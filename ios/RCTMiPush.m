#import "RCTMiPush.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "RCTConvert.h"
#import "RCTUtils.h"

NSString *const MiPush_didFailToRegisterForRemoteNotificationsWithError = @"MiPush_didFailToRegisterForRemoteNotificationsWithError";
NSString *const MiPush_didRegisterForRemoteNotificationsWithDeviceToken = @"MiPush_didRegisterForRemoteNotificationsWithDeviceToken";
NSString *const MiPush_didReceiveRemoteNotification = @"MiPush_didReceiveRemoteNotification";
NSString *const MiPush_didReceiveLocalNotification = @"MiPush_didReceiveLocalNotification";

@implementation RCTMiPush

+ (void)didFailToRegisterForRemoteNotificationsWithError:(NSError *)err
{
    [[NSNotificationCenter defaultCenter] postNotificationName:MiPush_didFailToRegisterForRemoteNotificationsWithError
                                                        object:self
                                                      userInfo:[err localizedDescription]];
}
+ (void)didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
    if ([UIApplication instancesRespondToSelector:@selector(registerForRemoteNotifications)]) {
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }
}
// Required for the register event.
+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [MiPushSDK bindDeviceToken:deviceToken];

    NSMutableString *hexString = [NSMutableString string];
    NSUInteger deviceTokenLength = deviceToken.length;
    const unsigned char *bytes = deviceToken.bytes;
    for (NSUInteger i = 0; i < deviceTokenLength; i++) {
        [hexString appendFormat:@"%02x", bytes[i]];
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:MiPush_didRegisterForRemoteNotificationsWithDeviceToken
                                                        object:self
                                                      userInfo:hexString];
}
// Required for the notification event.
+ (void)didReceiveRemoteNotification:(NSDictionary *)notification
{
    // 针对长连接做了消息排重合并，只在下面处理即可
    [MiPushSDK handleReceiveRemoteNotification: notification];

    [[NSNotificationCenter defaultCenter] postNotificationName:MiPush_didReceiveRemoteNotification
                                                        object:self
                                                      userInfo:notification];
}
// Required for the localNotification event.
+ (void)didReceiveLocalNotification:(UILocalNotification *)notification
{
    NSMutableDictionary *details = [NSMutableDictionary new];
    if (notification.alertBody) {
        details[@"alertBody"] = notification.alertBody;
    }
    if (notification.userInfo) {
        details[@"userInfo"] = RCTJSONClean(notification.userInfo);
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:MiPush_didReceiveLocalNotification
                                                        object:self
                                                      userInfo:details];
}

RCT_EXPORT_MODULE()

@synthesize bridge = _bridge;
@synthesize methodQueue = _methodQueue;

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setBridge:(RCTBridge *)bridge
{
    _bridge = bridge;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleFailToRegisterForRemoteNotificationsWithError:)
                                                 name:MiPush_didFailToRegisterForRemoteNotificationsWithError
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRegisterForRemoteNotificationsWithDeviceToken:)
                                                 name:MiPush_didRegisterForRemoteNotificationsWithDeviceToken
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleReceiveRemoteNotification:)
                                                 name:MiPush_didReceiveRemoteNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleReceiveLocalNotification:)
                                                 name:MiPush_didReceiveLocalNotification
                                               object:nil];
}

- (void)handleFailToRegisterForRemoteNotificationsWithError:(NSNotification *)notification
{
    [self sendMiPushEvent:@{
        @"type": @"REGISTER_REMOTE_NOTIFICATION_FAILED",
        @"error": notification.userInfo
    }];
}

- (void)handleRegisterForRemoteNotificationsWithDeviceToken:(NSNotification *)notification
{
    [self sendMiPushEvent:@{
        @"type": @"REGISTERED_REMOTE_NOTIFICATION",
        @"hexDeviceToken": notification.userInfo
    }];
}

- (void)handleReceiveRemoteNotification:(NSNotification *)notification
{
    [self sendMiPushEvent:@{
        @"type": @"REMOTE_NOTIFICATION",
        @"notification": notification.userInfo
    }];
}

- (void)handleReceiveLocalNotification:(NSNotification *)notification
{
    [self sendMiPushEvent:@{
        @"type": @"LOCAL_NOTIFICATION",
        @"notification": notification.userInfo
    }];
}

- (void)miPushRequestSuccWithSelector:(NSString *)selector data:(NSDictionary *)data
{
    // 请求成功
    [self sendMiPushEvent:@{
        @"type": @"REQUST_NOTIFICATION_SUCCESS",
        @"notification": data
    }];
}

- (void)miPushRequestErrWithSelector:(NSString *)selector error:(int)error data:(NSDictionary *)data
{
    if (!data) {
        data = @{};
    }

    // 请求失败
    [self sendMiPushEvent:@{
        @"type": @"REQUST_NOTIFICATION_FAILED",
        @"selector": selector,
        @"error": @(error),
        @"notification": data
    }];
}

- (void)miPushReceiveNotification:( NSDictionary *)data
{
    // 长连接收到的消息。消息格式跟APNs格式一样
    [self sendMiPushEvent:@{
        @"type": @"REMOTE_NOTIFICATION",
        @"notification": data
    }];
}

- (void)sendMiPushEvent:(id)body
{
    dispatch_async(_methodQueue, ^{
        [_bridge.eventDispatcher sendDeviceEventWithName:@"xiaomipush" body:body];
    });
}

RCT_EXPORT_METHOD(registerMiPush)
{
    [MiPushSDK registerMiPush:self];
}

RCT_EXPORT_METHOD(registerMiPushWithType:(int)type)
{
    [MiPushSDK registerMiPush:self type:(UIRemoteNotificationType)type];
}

RCT_EXPORT_METHOD(registerMiPushAndConnect:(BOOL)isConnect type:(int)type)
{
    [MiPushSDK registerMiPush:self type:(UIRemoteNotificationType)type connect:isConnect];
}

RCT_EXPORT_METHOD(unregisterMiPush)
{
    [MiPushSDK unregisterMiPush];
}

RCT_EXPORT_METHOD(bindDeviceToken:(NSString *)hexDeviceToken)
{
    NSMutableData * deviceToken = [[NSMutableData alloc] init];
    char bytes[3] = {'\0', '\0', '\0'};
    for (int i=0; i<[hexDeviceToken length] / 2; i++) {
        bytes[0] = [hexDeviceToken characterAtIndex:i*2];
        bytes[1] = [hexDeviceToken characterAtIndex:i*2+1];
        unsigned char c = strtol(bytes, NULL, 16);
        [deviceToken appendBytes:&c length:1];
    }
    
    [MiPushSDK bindDeviceToken:deviceToken];
}

RCT_EXPORT_METHOD(setAlias:(NSString *)alias)
{
    [MiPushSDK setAlias:alias];
}

RCT_EXPORT_METHOD(unsetAlias:(NSString *)alias)
{
    [MiPushSDK unsetAlias:alias];
}

RCT_EXPORT_METHOD(setAccount:(NSString *)account)
{
    [MiPushSDK setAccount:account];
}

RCT_EXPORT_METHOD(unsetAccount:(NSString *)account)
{
    [MiPushSDK unsetAccount:account];
}

RCT_EXPORT_METHOD(subscribe:(NSString *)topic)
{
    [MiPushSDK subscribe:topic];
}

RCT_EXPORT_METHOD(unsubscribe:(NSString *)topic)
{
    [MiPushSDK unsubscribe:topic];
}

RCT_EXPORT_METHOD(openAppNotify:(NSString *)messageId)
{
    [MiPushSDK openAppNotify:messageId];
}

RCT_EXPORT_METHOD(getAllAliasAsync)
{
    [MiPushSDK getAllAliasAsync];
}

RCT_EXPORT_METHOD(getAllAccountAsync)
{
    [MiPushSDK getAllAccountAsync];
}

RCT_EXPORT_METHOD(getAllTopicAsync)
{
    [MiPushSDK getAllTopicAsync];
}

RCT_REMAP_METHOD(getSDKVersion,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSString * sdkVersion = [MiPushSDK getSDKVersion];

    resolve(sdkVersion);
}

@end
