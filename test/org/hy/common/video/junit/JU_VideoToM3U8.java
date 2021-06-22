package org.hy.common.video.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.file.FileHelp;
import org.hy.common.video.VideoHelp;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：Mp4视频转M3U8(HLS)
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-09-27
 * @version     v1.0
 */
public class JU_VideoToM3U8
{
    
    private static Logger $Logger = Logger.getLogger(JU_VideoToM3U8.class);
    
    
    
    /**
     * 生成只有一级索引的M3U8
     */
    @Test
    public void toM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        String v_M3U8Name = VideoHelp.mp4ToM3U8("C:\\迅雷下载\\WZYB.mp4" ,"C:\\迅雷下载\\WZYB" ,3 ,"http://127.0.0.1/");
        
        $Logger.info("M3U8路径：" + v_M3U8Name);
    }
    
    
    
    /**
     * 生成两级索引的M3U8
     * 
     * 注：以下方案的方向和思路均是不正确的
     * 
     * @throws IOException
     */
    @Test
    public void toM3U8_TwoIndex() throws IOException
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        String        v_TSName      = VideoHelp.mp4ToTS("C:\\迅雷下载\\WZYB.mp4" ,"C:\\迅雷下载\\WZYB");
        List<String>  v_SplitVideos = VideoHelp.splits(v_TSName ,"C:\\迅雷下载\\WZYB" ,8 ,27);
        List<String>  v_M3U8Names   = new ArrayList<String>();
        StringBuilder v_M3U8All     = new StringBuilder();
        FileHelp      v_FileHelp    = new FileHelp();
        
        v_FileHelp.setOverWrite(true);
        (new File(v_TSName)).delete();
        
        v_M3U8All.append("#EXTM3U\n");
        
        for (String v_SName : v_SplitVideos)
        {
            String v_M3U8Name = VideoHelp.tsToM3U8(v_SName ,"C:\\迅雷下载\\WZYB" ,3);
            
            v_M3U8Names.add(v_M3U8Name);
            v_M3U8All.append("#EXT-X-STREAM-INF:PROGRAM-ID=1\n");
            v_M3U8All.append(v_M3U8Name);
            v_M3U8All.append("\n");
            
            (new File(v_SName)).delete();
        }
        
        v_FileHelp.create("C:\\迅雷下载\\WZYB\\WZYB.m3u8" ,v_M3U8All.toString());
        
        $Logger.info("M3U8路径：" + v_M3U8Names);
    }
    
}
