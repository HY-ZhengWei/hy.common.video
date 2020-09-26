#!/bin/sh

cd ./bin


rm -R ./org/hy/common/video/junit
rm -R ./inputPath
rm -R ./outputPath
rm show.html


jar cvfm hy.common.video.jar MANIFEST.MF META-INF org

cp hy.common.video.jar ..
rm hy.common.video.jar
cd ..





cd ./src
jar cvfm hy.common.video-sources.jar MANIFEST.MF META-INF org 
cp hy.common.video-sources.jar ..
rm hy.common.video-sources.jar
cd ..
