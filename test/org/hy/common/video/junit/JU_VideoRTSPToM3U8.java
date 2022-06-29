package org.hy.common.video.junit;

import org.hy.common.video.VideoHelp;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：解析RTSP视频流，转换为HLS直播流
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-08-07
 * @version     v1.0
 */
public class JU_VideoRTSPToM3U8
{
    private static Logger $Logger = Logger.getLogger(JU_VideoRTSPToM3U8.class);
    
    
    
    @Test
    public void RTSPToM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        VideoHelp.$IsBebug    = true;
        
        Process v_VideoProcess = VideoHelp.rtspToM3U8("rtsp://10.1.130.51:554/live/0/MAIN"
                                                ,"C:\\VideoDatas\\TingChe1\\2021-08-07\\20210807.m3u8"
                                                ,5
                                                ,2);
        
        $Logger.info("执行中：");
        
        try
        {
            Thread.sleep(1000 * 30);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        $Logger.info("执行完成。");
        v_VideoProcess.destroy();
    }
    
}
