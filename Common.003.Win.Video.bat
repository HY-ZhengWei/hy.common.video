

cd .\bin


rd /s/q .\org\hy\common\video\junit
rd /s/q .\inputPath
rd /s/q .\outputPath
del /q show.html


jar cvfm hy.common.video.jar MANIFEST.MF META-INF org 

copy hy.common.video.jar ..
del /q hy.common.video.jar
cd ..





cd .\src
jar cvfm hy.common.video-sources.jar MANIFEST.MF META-INF org 
copy hy.common.video-sources.jar ..
del /q hy.common.video-sources.jar
cd ..
