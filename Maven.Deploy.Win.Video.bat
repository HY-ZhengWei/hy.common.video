start mvn deploy:deploy-file -Dfile=hy.common.video.jar                              -DpomFile=./src/META-INF/maven/org/hy/common/video/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty
start mvn deploy:deploy-file -Dfile=hy.common.video-sources.jar -Dclassifier=sources -DpomFile=./src/META-INF/maven/org/hy/common/video/pom.xml -DrepositoryId=thirdparty -Durl=http://HY-ZhengWei:8081/repository/thirdparty