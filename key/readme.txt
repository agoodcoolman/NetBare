jarsigner是Java的签名工具，JDK自带
-verbose参数表示：显示出签名详细信息
-keystore表示使用当前目录中的android.keystore签名证书文件。
-signedjar app-release.apk app-dubug.apk 表示签名后生成的APK名称为app-release.apk ，未签名的APK Android软件名称为app-dubug.apk
-androidauto.keystore表示签名文件的别名，生成证书的时候有书写


jarsigner -verbose -keystore ./jin.jks  -signedjar netbare-sample-release_legu_sign.apk netbare-sample-release_legu.apk jin