package org.hy.common.video.junit;

import org.hy.common.video.VideoHelp;
import org.junit.Test;





/**
 * 测试单元：转码成MP4格式的视频
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-23
 * @version     v1.0
 */
public class JU_VideoToMP4
{
    
    @Test
    public void toMP4()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.toMP4("C:\\WorkSpace\\hy.common.video\\test\\inputPath\\4.mp4"
                       ,"C:\\WorkSpace\\hy.common.video\\test\\outputPath"
                       ,720
                       ,480);
    }
    
}
