package org.hy.common.video.junit;

import org.hy.common.video.VideoHelp;
import org.junit.Test;

public class JU_VideoToM3U8
{
    
    @Test
    public void toM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        String v_TSName   = VideoHelp.mp4ToTS("C:\\迅雷下载\\WZYB.mp4" ,"C:\\迅雷下载\\WZYB");
        String v_M3U8Name = VideoHelp.tsToM3U8(v_TSName ,"C:\\迅雷下载\\WZYB" ,3);
    }
    
}
