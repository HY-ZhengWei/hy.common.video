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
        VideoHelp.$FFMpegHome = "D:\\WorkSpace_SearchDesktop\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.toGif("E:\\迅雷下载\\外国小姐姐翻唱《李白》.mp4" ,"E:\\迅雷下载\\liBai.gif" ,24 * 10 ,"640x480");
    }
    
}
