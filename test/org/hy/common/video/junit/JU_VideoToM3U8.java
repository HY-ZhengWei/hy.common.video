package org.hy.common.video.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.StringHelp;
import org.hy.common.file.FileHelp;
import org.hy.common.video.VideoHelp;
import org.hy.common.video.VideoInfo;
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
     * Mp4解码为TS，再TS转M3U8
     */
    @Test
    public void mp4ToTSToM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        String v_M3U8Name = VideoHelp.mp4ToM3U8("D:\\大数据下载\\FMS.mp4" ,"D:\\大数据下载\\FMS" ,1 ,"http://127.0.0.1/msFile/file/play/FMS/");
        
        $Logger.info("M3U8路径：" + v_M3U8Name);
    }
    
    
    
    /**
     * Flv解码为Mp4，Mp4解码为TS，再TS转M3U8
     */
    @Test
    public void flvToMp4ToTSToM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        VideoHelp.$IsBebug    = false;
        
        Date      v_BTime    = new Date();
        VideoInfo v_Mp4Name  = VideoHelp.toMP4("C:\\迅雷下载\\A.flv" ,"C:\\迅雷下载" ,1280 ,720);
        String    v_M3U8Name = VideoHelp.mp4ToM3U8(v_Mp4Name.getName() ,"C:\\迅雷下载" ,10 ,"http://127.0.0.1/msFile/file/play/demoVideo/");
        Date      v_ETime    = new Date();
        
        $Logger.info("M3U8路径：" + v_M3U8Name + "。\t用时：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
    }
    
    
    
    /**
     * Flv解码为TS，再TS转M3U8
     */
    @Test
    public void flvToTSToM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        Date v_BTime = new Date();
        Date v_ETime = new Date();
        
        
        VideoInfo v_TSName = VideoHelp.flvToTS("C:\\迅雷下载\\A.flv" ,"C:\\迅雷下载");
        v_ETime = new Date();
        $Logger.info("用时：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        
        
        String v_M3U8Name = VideoHelp.tsToM3U8(v_TSName.getName() ,"C:\\迅雷下载" ,10 ,"http://127.0.0.1/msFile/file/play/demoVideo/");
        v_ETime = new Date();
        $Logger.info("用时：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        $Logger.info("M3U8路径：" + v_M3U8Name + "。" );
    }
    
    
    
    /**
     * Flv解码为TS，再TS创建M3U8
     */
    @Test
    public void flvToTSCreateM3U8()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        Date v_BTime = new Date();
        Date v_ETime = new Date();
        
        
        VideoInfo v_TSName = VideoHelp.flvToTS("C:\\迅雷下载\\A.flv" ,"C:\\迅雷下载\\");
        v_ETime = new Date();
        $Logger.info("用时：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        
        
        String v_M3U8Name = "C:\\迅雷下载\\A.m3u8";
        VideoHelp.createM3U8File(v_M3U8Name ,v_TSName.setName(StringHelp.replaceAll(v_TSName.getName() ,"C:\\迅雷下载\\" ,"http://127.0.0.1/msFile/file/play/demoVideo/")));
        v_ETime = new Date();
        $Logger.info("用时：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        $Logger.info("M3U8路径：" + v_M3U8Name + "。" );
    }
    
    
    
    @Test
    public void mp4ToTS()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.mp4ToTS("C:\\素材库\\loading.mp4" ,"C:\\素材库\\loading");
    }
    
    
    
    /**
     * 生成两级索引的M3U8
     * 
     * 注：以下方案的方向和思路均是不正确的
     * 
     * @throws IOException
     */
    public void toM3U8_TwoIndex() throws IOException
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoInfo     v_TSName      = VideoHelp.mp4ToTS("C:\\素材库\\loading.mp4" ,"C:\\素材库\\loading");
        List<String>  v_SplitVideos = VideoHelp.splits(v_TSName.getName() ,"C:\\素材库\\loading" ,8 ,27);
        List<String>  v_M3U8Names   = new ArrayList<String>();
        StringBuilder v_M3U8All     = new StringBuilder();
        FileHelp      v_FileHelp    = new FileHelp();
        
        v_FileHelp.setOverWrite(true);
        (new File(v_TSName.getName())).delete();
        
        v_M3U8All.append("#EXTM3U\n");
        
        for (String v_SName : v_SplitVideos)
        {
            String v_M3U8Name = VideoHelp.tsToM3U8(v_SName ,"C:\\素材库\\loading" ,3);
            
            v_M3U8Names.add(v_M3U8Name);
            v_M3U8All.append("#EXT-X-STREAM-INF:PROGRAM-ID=1\n");
            v_M3U8All.append(v_M3U8Name);
            v_M3U8All.append("\n");
            
            (new File(v_SName)).delete();
        }
        
        v_FileHelp.create("C:\\素材库\\loading\\loading.m3u8" ,v_M3U8All.toString());
        
        $Logger.info("M3U8路径：" + v_M3U8Names);
    }
    
}
