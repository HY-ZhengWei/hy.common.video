package org.hy.common.video;

import org.junit.Test;





/**
 * 测试单元：压缩视频
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-23
 * @version     v1.0
 */
public class JU_VideoZip
{
    
    @Test
    public void zip()
    {
        VideoHelp.$FFMpegHome = "D:\\WorkSpace_SearchDesktop\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.toMP4("D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\inputPath\\4.mp4"
                       ,"D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\outputPath" 
                       ,1080 
                       ,720);
    }
    
}
