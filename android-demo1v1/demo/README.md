## VVROOM-SDK-Android

### SDK环境
```
...
compileSdkVersion 27

    defaultConfig {
        ...
        minSdkVersion 16
        targetSdkVersion 26
        ...
    }
...
```
将vvroom-peerconnection-release-xx.aar 拷贝到libs目录

### 依赖
```
// 引入AAR
implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])

compile 'com.alibaba:fastjson:1.2.17'
```