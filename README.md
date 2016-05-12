小米 Push 的 React Native 封装。

* 非官方发布

# 安装
```
npm install --save react-native-mipush
```

## Android

MainActivity.java 添加以下代码:

```
// ...
import com.xiaomi.push.reactnative.MiPushPackage;
// ...
public class MainActivity extends ReactActivity {
    private MiPushPackage mMiPushPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMiPushPackage = new MiPushPackage(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mMiPushPackage.onIntent(intent);
    }

    // ...
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.asList(
            new MainReactPackage(),
            mMiPushPackage,         // <------ Add into package list
    // ...
```

## iOS

暂不支持。

# 限制

由于 React Native 的 JS 代码只有 Activity 在前台时才可执行，所以在 Activity 在前台时能够处理所有消息，不在前台时只能处理点击打开界面的通知栏消息。

# License

MIT.
