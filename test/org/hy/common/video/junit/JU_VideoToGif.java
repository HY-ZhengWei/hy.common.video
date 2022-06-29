package org.hy.common.video.junit;

import org.hy.common.video.VideoHelp;
import org.junit.Test;





/**
 * 测试单元：视频转Gif图片
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-09-27
 * @version     v1.0
 */
public class JU_VideoToGif
{
    
    @Test
    public void test_ToGif()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.toGif("C:\\Users\\hyzhe\\Desktop\\1.mp4" ,"C:\\Users\\hyzhe\\Desktop\\1.gif" ,24 * 44 ,"640x320");
    }
    
}
