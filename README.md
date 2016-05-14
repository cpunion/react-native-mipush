小米 Push 的 React Native 封装。

* 非官方发布。

# 安装
```
npm install --save react-native-mipush
rnpm link react-native-mipush
```

## Android

* 在 rnpm link 的基础上对 MainActivity.java 做以下修改:

```
// ...
public class MainActivity extends ReactActivity {
    private MiPushPackage mMiPushPackage;            <---- 添加

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMiPushPackage = new MiPushPackage(this);    <---- 添加
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {      <---- 添加这个方法
        super.onNewIntent(intent);
        setIntent(intent);
        mMiPushPackage.onIntent(intent);
    }

    // ...
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.asList(
            new MainReactPackage(),
            mMiPushPackage,         // <------ 修改自动生成的 new MiPushPackage
    // ...
```

* AndroidManafest.xml 中修改 application 的 android:name 为 `com.xiaomi.push.reactnative.MiPushApplication`，并添加你的 MIPUSH_APP_ID 和 MIPUSH_APP_KEY：

**注意：由于小米的 AppID 和 AppKey 都是纯数字，需要在前面加上 `\ ` 来转义！**

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="cn.applean.apprunner"
    android:versionCode="1"
    android:versionName="1.0.0">
    <application
      android:name="com.xiaomi.push.reactnative.MiPushApplication"  <----- 这里

      .... 添加以下两行，替换称自己的 APP_ID 和 APP_KEY

      <meta-data android:value="\ 12345678901234567890" android:name="MIPUSH_APP_ID" />
      <meta-data android:value="\ 12345678" android:name="MIPUSH_APP_KEY" />
```

## iOS

Info.plist 用文本编辑器打开，添加以下代码：

```
<dict>
    <key>MiSDKAppID</key>
    <string>1000888</string>   <---- 替换为自己的
    <key>MiSDKAppKey</key>
    <string>500088888888</string>   <---- 替换为自己的
    <key>MiSDKRun</key>
    <string>Online</string>    <---- Online 表示正式环境，Debug 表示 Sandbox 环境
</dict>
```

AppDelegate.m 添加以下代码

```
#import "RCTMiPush.h"
// ...
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // ...
    NSDictionary *initialProperties = [RCTMiPush initAndGetInitialPropertiesFromLaunchOptions:launchOptions];
    // ...
    RCTRootView *rootView =
        [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                    moduleName:@"RNTest"
                             initialProperties:initialProperties  // <-- 修改这里
                              launchOptions:launchOptions];
    // ...
}

// 添加以下这些代码
#pragma mark UIApplicationDelegate
- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err
{
  // 注册APNS失败
  [RCTMiPush didFailToRegisterForRemoteNotificationsWithError:err];
}
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
  [RCTMiPush didRegisterUserNotificationSettings:notificationSettings];
}
// Required for the register event.
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [RCTMiPush didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}
// Required for the notification event.
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification
{
  [RCTMiPush didReceiveRemoteNotification:notification];
}
// Required for the localNotification event.
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
  [RCTMiPush didReceiveLocalNotification:notification];
}
```

# JS 中使用

**由于目前还没有对 iOS 和 Android 版本做接口统一，所以两个平台有所区别，以后版本有可能修改为统一接口。**

## 1.引入模块

```
import MiPush from 'react-native-mipush'
```

## 2.获取启动参数，用于响应点击 Push 消息跳转到应用内

方法1：
```
    MiPush.getInitialMessage()
    .then((message) => {
      console.log('===== initial push:', message)
    })
```

方法2: 通过根 Component 的 props 获取

## 3.启动后点击的新消息

```
    DeviceEventEmitter.addListener("xiaomipush", this._handlePush)
```

两个平台参数没有统一，目前需要自己分别处理。

## 4.启动 Push 代码

Android: 已经在 application 中启动

iOS:
```
    MiPush.registerMiPushAndConnect(true, 0)
```

# 接口文档

待写。

# 限制

由于 React Native 的 JS 代码只有 UI 在前台时才可执行，所以在 Activity 在前台时能够处理所有消息，不在前台时只能处理点击打开界面的通知栏消息。

# License

MIT.
