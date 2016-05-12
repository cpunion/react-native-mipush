小米 Push 的 React Native 封装。

* 非官方发布

# 安装
```
npm install --save react-native-mipush
rnpm link react-native-mipush
```

# 限制

由于 React Native 的 JS 代码只有 Activity 在前台时才可执行，所以在 JS 中应该只处理以下消息：

1. 有点击打开界面的通知栏消息
2. APP在后台时可以忽略的其他消息

如果此消息不能忽略，请使用通知消息。

# License

MIT.
