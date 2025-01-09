# Maven打包
mvn -X clean install package -DskipTests

###### Maven打包注意
检查cmd里面java -version的版本号和项目版本号是否一致，否则可能出现无效的目标版本号17之类的问题

# 程序打包
注意:  
jre目录是自行裁剪jdk的，把需要的模块裁剪即可，java.scripting,java.desktop,java.naming,java.security.jgss等模块是桌面程序必须的  
runtime目录是自行建立的，把需要的资源和类库放到此目录即可

###### 定制JRE(windows)
jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.sql,java.base,java.naming,java.logging,java.desktop,java.scripting,java.datatransfer,java.security.jgss,jdk.unsupported --output D:\Package\EasyZK\jre

###### 打包镜像(windows，已废弃，交给easypkg来执行)
jpackage --verbose -t app-image -n EasyZK --vendor oyzh --app-version '1.6.3' --description 'EasyZK Desktop Application.' -i D:\Package\EasyZK\runtime --main-jar easyzk-1.0.jar --icon D:\Package\EasyZK\app.ico --runtime-image D:\Package\EasyZK\jre -d D:\Package\EasyZK

###### 定制JRE(macos)
./jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.logging,java.base,java.sql,jdk.unsupported,java.scripting,java.desktop,java.naming,java.security.jgss --output /Users/oyzh/Desktop/Package/EasyZK/1.6.3/macos_amd64_jre

###### 打包镜像(macos)(打包的应用无法正常运行，已废弃，交给easypkg来执行)
./jpackage --verbose -t app-image -n EasyZK --vendor oyzh --app-version '1.6.3' --description 'EasyZK Desktop Application.' -i /Users/oyzh/Desktop/Package/EasyZK/runtime --main-jar easyzk-1.0.jar --icon /Users/oyzh/Desktop/Package/EasyZK/app.icns --runtime-image /Users/oyzh/Desktop/Package/EasyZK/jre -d /Users/oyzh/Desktop/Package/EasyZK/EasyZK

###### 定制JRE(linux)
./jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.logging,java.base,java.sql,jdk.unsupported,java.scripting,java.desktop,java.naming,java.security.jgss --output /home/oyzh/Desktop/Package/EasyZK/1.6.3/linux_amd64_jre

##### 打包镜像(linux，已废弃，交给easypkg来执行)
jpackage --verbose -t app-image -n EasyZK --vendor oyzh --app-version '1.6.3' --description 'EasyZK Desktop Application.' -i /Users/oyzh/Desktop/Package/EasyZK/runtime --main-jar easyzk-1.0.jar --icon /home/oyzh/Desktop/Package/EasyZK/app.png --runtime-image /Users/oyzh/Desktop/Package/EasyZK/jre -d /home/oyzh/Desktop/Package/EasyZK/EasyZK

###### docker启动zk(单个)
docker run -itd -p 2181:2181 zookeeper

###### docker启动zk(集群)
docker-compose -f .\zk-cluster-compose.yml up -d

###### mac无法启动解决方案1
chmod -R +x EasyZK.app

###### mac无法启动解决方案2
chmod -R 755 /路径/EasyZK.app(可拖入命令行窗口)

###### mac无法启动解决方案3
当在macOS上运行.app文件时提示“已损坏，无法打开”，你可以尝试以下几种解决方法：
1. 允许“任何来源”下载的App运行‌
   打开“系统偏好设置”->“安全性与隐私”->“通用”选项卡。
   检查是否已经启用了“任何来源”选项。如果没有启用，先点击左下角的小黄锁图标解锁，然后选中“任何来源”‌1。
   如果“任何来源”选项不可用，可以打开终端，输入命令sudo spctl --master-disable，然后按提示输入电脑的登录密码并回车，即可启用“任何来源”选项‌12。

###### jvm参数
--add-opens java.base/java.time.zone=ALL-UNNAMED
-Dprism.verbose=true
-verbose:gc
-XX:+UseZGC
-Xss512K
-Xmx1024m 
-Xms32m 
-XX:NewRatio=2 
-XX:MinHeapFreeRatio=8 
-XX:MaxHeapFreeRatio=20

###### 模块相关
官方链接 https://docs.oracle.com/en/java/javase/19/docs/api/
