# AndroidRouter


---
title: Android路由方案选型
category: Android开发
feature_image: "https://ssevening.github.io/assets/android.png"
image: "https://ssevening.github.io/assets/android.png"
---

今天说一下Android的路由跳转，一个App分为多个模块时，模块与模块之间是怎么跳转的呢？有哪些方式？各自的优缺点又是什么呢？

<!-- more -->

## 一、背景：
在国外，一个App，只负责一个功能，比如：看电影、充话费、购物、旅游等，一个功能，就会做成一个App，但在国内，就是喜欢做一个大而全的全家桶，比如万能的淘宝、万能的微信、万能的支付宝、万能的大众点评，但模块一多，要解决的问题也就来了。问题如下：

1. 模块多，代码多，方法数超过65534，要分成多个DEX，否则无法打出APK安装包。
2. 模块间的相互调用和解耦，从A Activity 跳转到 B Activity，但又不能代码依赖，要怎么解决？
3. 因为模块多，功能也多，测试 花在回归测试上的工作量就会上去，影响发版速度。
4. 因为模块多，出问题也就多，各个模块紧急发布版本的可能性也多。


问题1 很简单的可以通过mutiDex解决，今天我们来聊一聊问题2 的解决，那就是 Android的模块间路由。


## 二、路由的核心原理

> 建立字符串和具体Activity的映射关系，模块跳转的时候，查找字符串映射的Activity路径，并启动Activity.

## 三、路由的核心实现方式
* JAVA代码Class.forName的方式。
* Schema 映射Activity的方式。
* 自建 string 和 Activity 映射关系管理方式。【比如:ARouter】

### 1. Class.forName的方式

示例代码如下：通过类路径直接从虚拟机中找类，再启动Activity.

```
try {
    Class c = Class.forName("www.ssevening.com.MainActivity");
    intent = new Intent(activity, c);
    intent.putExtra("key", "value");
    // 添加类目
    //intent.addCategory("")
    // 设置相应的flag
    // intent.setFlags()
    activity.startActivity(intent);
} catch (ClassNotFoundException e) {
    e.printStackTrace();
}

```

实现简单，花在Class.forName时间约4ms，性能不是问题，那有没有其他问题呢？

第一点：我们先看入参，我们需要传递 activity的类路径，开发中改个包名或Activity类名再常见不过了，就会出现类名找不到的情况，最终影响线上功能。

第二点：容错和降级方案，类找不到的话，就没有任何补救措施，线上功能直接就挂了，影响用户体验。

第三点，电商类App都会有两个端，WEB端和移动端，移动端下面又分出Android 和 iOS, 必然会面临WEB端向App端的流量导入，一种是引流下载App；另一种就是，客户端也可以拦截网站的http网址，从而直接唤起App。

这里说到的第二种由http网址直接向App跳转的情况，就又涉及到了另外一套路由规则，即：URL 向 Native 页面的规则。

再加上反射实现的路由，我们面临两套规则的维护，必然我们不希望用反射的方式来实现。

那有没有办法直接用URL向我们App的Native映射呢？答案必然是肯定的。且看下面分解。

### 2. Schema 映射的方式

首先说一下 Schema 是一种什么东东。大家如果手机上装有多个应用市场，那当一个App邀请给五星好评的时候，通常会弹出多个应用市场供你选择。五星好评实现代码如下：

```
Uri uri = Uri.parse("market://details?id=www.ssevening.com.router");

Intent intent = new Intent(Intent.ACTION_VIEW, uri);

activity.startActivity(intent);

```

向系统发送了一个VIEW 意图，询问系统，谁可以处理 market:// 这样的协议？ 然后系统查出可处理此协议的应用列表，供我们选择。可处理  market协议的代码要怎么写呢？很简单，在 Manifest.xml 修改可以处理market协议Activity代码声明文件即可。 示例代码如下：

```
<activity android:name=".ShopcartActivity">
<intent-filter>
    <action android:name="android.intent.action.VIEW" />

    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />

    <data
        android:pathPattern="details"
        android:scheme="market" />
</intent-filter>
</activity>

```

所以，让应用支持market可以像上面那样写代码。那把android:scheme="market" 中的 market 换成 http 或 https, 那我们的App也就可以处理 http请求了。代码如下：

```
<activity android:name=".ShopcartActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:host="www.ssevening.com"
            android:pathPattern="/shopcart.html"
            android:scheme="https" />
    </intent-filter>
</activity>

```

下面要解决的问题就是：系统怎么查找出哪个类可以处理哪些URL呢？答案尽在下面代码：

```
// 创建一个ActionView的意图
Intent intent = new Intent(Intent.ACTION_VIEW);
// 设置好要访问的Uri
Uri uri = Uri.parset("http://www.ssevening.com/shopcart.html");
intent.setData(uri);
// 查询出可以处理该Uri的解决信息
final List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
// 这里我们直接取第一个来处理，也可以有一些加强逻辑，比如找到和自己当前App包名一样的Activity,或者找不到给个默认处理方式等
ResolveInfo info = list.get(0);
// 到这里终于取到了可以处理的类名，设置到intent中
intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
// 余下的就是去启动Activity实现了
context.startActivity(intent);

```

上面代码实现了scheme的路由，这样，WEB的URL可以映射到 Native App，同时，自己App内部，通过代码包装，可以这样调用：
> SchemaNav.from(ProductActivity.this).withExtras(bundle).fire("https://www.ssevening.com/shopcart.html");

通过上面代码，从产品页面，跳转到了购物车页面，同时还传递了参数，革命有没有成功呢？来来来，一起挑战一下！

* 入参URL改变的问题：URL的改变比包名要稳定，即使WEB端url有改变，影响也只是不能唤起我们的NativeApp。影响小。
* 两套规则映射管理的问题：现在只有URL的映射，一套规则，整个世界安静了。
* 如果查找不到要处理的类，怎么办？本身就是一个网址啊，直接启动一个WebView，打开网页就好了啊。WEB端成了App强大的后盾。资源合理利用了一把，低碳环保！

大家会想，好像是这么回事，那你说的第三种自建映射规则有什么不好的呢？

这里以ARouter为例，ARouter的核心实现就是：
> 通过运行时注解，维护了一个映射map，管理页面名称 和 页面路径的关系。

同样解决不了两套映射关系维护的问题
并且还引入一个新问题：万一后期有第三方Module要添加到我们的应用中时，第三方也要按我们的玩法，去依赖ARouter，这个时候，人家就要操了，太他妈的恶心了！对第三方也不友好。
而Schema的方式，本身就是Android的写法，说不定别人本来也就实现了呢？岂不是悠哉乐哉？


综上所述：把骡子和马都牵出来遛遛！

| 功能特性      |     反射实现 |   Schema实现   | 自己维护映射关系列|
| :-------- | --------:| :------: | :-----:|
| 入参稳定    |   不稳定 |  较稳定  | 十分稳定|
| 一套规则    |   两套规则 |  一套规则  |两套规则|
| 支持降级    |   不支持|  支持降级  |不支持|
| 第三方易接入    |  易接入 |  易接入  | 不易接入|

通过对比，Schema实现除了入参不稳定外，其他方面都还是不错的。当然如果你的App就真的只是个App，没有相对应的WEB页面，那ARouter也是个不错的选择，毕竟还可以支持跨模块服务的调用。

说了这么多，talk is easy, show you the fucking github code!

[GitHub地址](https://github.com/ssevening/AndroidRouter/)


欢迎关注作者微信公众号，及时获得作者更新：

![微信公众号](https://ssevening.github.io/assets/weichat_qrcode.jpg)

另外还建立了小密圈：圈主 和 嘉宾 都就职于 阿里巴巴 的顶尖开发者，开发的app被Google 编辑推荐，对性能，架构，图片，MD设计都有研究和深入，欢迎大家加入，提升自己，一起进步，互相帮助交流！

![微信公众号](https://ssevening.github.io/assets/mi_qrcode.png)










