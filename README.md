# robot
一般通过Robot，基于SpringBoot和mirai-core开发，需要JDK11以上的运行环境

一时兴起开发的自娱自乐整活QQ机器人，因此有许多定制化功能,包括但不限于基金净值估算抓取，生草表情包制作，日语大辞典，色图随机器，可以当作是Mirai的用法入门教学。

快速启动可以下载RELEASE包，然后执行以下命令：
```java
  java -jar robot.jar --spring.config.location=全量配置文件路径
```
配置文件可参考[这里](https://github.com/LainNetWork/robot/blob/master/src/main/resources/application.yml)

如果真的有人会使用本项目的代码，保留来源声明即可。

ps:因为被用于个人项目，主分支包含大量Spring cloud相关配置，建议使用直接使用release包，或自行更改配置，最近没有时间打理分支

走过路过，留一个star让开发者知道他不是个废物吧.jpg
