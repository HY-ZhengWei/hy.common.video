package org.hy.common.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.log.Logger;





/**
 * 视频操作的相关工具类
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-14
 * @version     v1.0
 *              v2.0  2020-09-26  添加：获取视频信息（如，视频的宽度、高度）
 *              v3.0  2020-09-27  添加：将视频的前多少帧转换成一个动图（Animated Gif）
 *              v4.0  2021-06-21  添加：Mp4转M3U8(HLS)的视频
 *              v5.0  2021-06-28  添加：Flv转M3U8(HLS)的视频
 */
public class VideoHelp
{
    
    private static final Logger $Logger = new Logger(VideoHelp.class);
    
    public static String  $FFMpegHome;
    
    public static boolean $IsBebug = true;
    
    
    
    /**
     * 将视频的前多少帧转换成一个动图（Animated Gif）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-09-27
     * @version     v1.0
     *
     * @param i_VideoFile        视频文件
     * @param i_GifFile          保存生成的Gif动图
     * @param i_Frame            视频的前多少帧
     * @param i_ResolutionRatio  视频分辨率。如 320x240、640x480
     * @return
     */
    public static boolean toGif(String i_VideoFile ,String i_GifFile ,int i_Frame ,String i_ResolutionRatio)
    {
        File v_SourceFile = new File(i_VideoFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_VideoFile + " is not file");
            return false;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_VideoFile + " can not read");
            return false;
        }
        File v_GifFile = new File(i_GifFile);
        if ( v_GifFile.exists() )
        {
            $Logger.warn(i_GifFile + " is exists");
            return false;
        }
        
        List<String> command = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");                       // 如果输出文件已存在则覆盖
        command.add(i_VideoFile);
        command.add("-vframes");
        command.add("" + i_Frame);
        command.add("-s");
        command.add(i_ResolutionRatio);
        command.add("-y");
        command.add("-f");
        command.add("gif");
        command.add(i_GifFile);
        
        if ( !$IsBebug )
        {
            command.add("-loglevel");
            command.add("quiet");
        }
        
        try
        {
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            new PrintStream(videoProcess.getErrorStream()).start();
            new PrintStream(videoProcess.getInputStream()).start();
            videoProcess.waitFor();
            return true;
        }
        catch (Exception e)
        {
            $Logger.error(e);
            return false;
        }
    }
    
    
    
    /**
     * 将视频转为MP4格式的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-11-23
     * @version     v1.0
     *
     * @param i_SourceFile   原视频文件
     * @param i_SavePath     保存路径
     * @param i_ResolutionX  视频分辨率1080x720中的：1080
     * @param i_ResolutionY  视频分辨率1080x720中的：720
     * @return               成功时返回生成的Mp4全路径
     */
    public static VideoInfo toMP4(String i_SourceFile ,String i_SavePath ,int i_ResolutionX ,int i_ResolutionY)
    {
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_SourceFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_SourceFile + " can not read");
            return null;
        }
        
        String v_SName = StringHelp.getFileShortName(v_SourceFile.getName());
        String v_Mp4   = i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator()) + v_SName + ".mp4";
        
        List<String> command = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");                       // 如果输出文件已存在则覆盖
        command.add(i_SourceFile);
        command.add("-c:v");
        command.add("libx264");
        command.add("-mbd");
        command.add("0");
        command.add("-c:a");
        command.add("aac");
        command.add("-strict");
        command.add("-2");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-movflags");
        command.add("faststart");
        command.add("-s");                       // 设置帧大小 格式为WXH 缺省160X128.下面的简写也可以直接使用
        command.add(i_ResolutionX + "x" + i_ResolutionY);
        command.add(v_Mp4);
        command.add("-y");                       // 源视频文件
        
        VideoInfo v_Video = executeCommand(command);
        if ( v_Video != null )
        {
            v_Video.setName(v_Mp4);
        }
        
        return v_Video;
    }
    
    
    
    /**
     * 创建M3U8索引文件
     * 
     * @param i_M3U8SaveName
     * @param i_TSVideo
     * @return
     */
    public static boolean createM3U8File(String i_M3U8SaveName ,VideoInfo i_TSVideo)
    {
        if ( i_TSVideo == null || Help.isNull(i_TSVideo.getName()) )
        {
            return false;
        }
        
        Long v_TimeValue = Date.toTimeValue(i_TSVideo.getDuration());
        if ( v_TimeValue == null )
        {
            return false;
        }
        
        String v_Content = "#EXTM3U\r\n"
                         + "#EXT-X-VERSION:3\r\n"
                         + "#EXT-X-MEDIA-SEQUENCE:0\r\n"
                         + "#EXT-X-ALLOW-CACHE:YES\r\n"
                         + "#EXT-X-TARGETDURATION:15\r\n"
                         + "#EXTINF:" + (v_TimeValue.longValue() / 1000d) + ",\r\n"
                         + i_TSVideo.getName() + "\r\n"
                         + "#EXT-X-ENDLIST\r\n";
        
        FileHelp v_FileHelp = new FileHelp();
        try
        {
            v_FileHelp.create(i_M3U8SaveName ,v_Content);
            return true;
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        return false;
    }
    
    
    
    /**
     * 将视频FLV转为TS格式的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-28
     * @version     v1.0
     *
     * @param i_SourceFile   原视频文件
     * @param i_SavePath     保存路径
     * @return               成功时返回生成的TS全路径
     */
    public static VideoInfo flvToTS(String i_SourceFile ,String i_SavePath)
    {
        // ffmpeg -i C:\迅雷下载\a.flv -vcodec libx264 C:\迅雷下载\a.ts
        // ffmpeg -i C:\迅雷下载\a.flv -c copy -bsf h264_mp4toannexb C:\迅雷下载\c.ts   不想编码的
        
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_SourceFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_SourceFile + " can not read");
            return null;
        }
        
        String v_FlvName = StringHelp.getFileShortName(v_SourceFile.getName());
        String v_TSName  = i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator()) + v_FlvName + ".ts";
        
        List<String> command = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");                       // 如果输出文件已存在则覆盖
        command.add(i_SourceFile);
        command.add("-vcodec");
        command.add("libx264");
        command.add(v_TSName);
        
        VideoInfo v_Video = executeCommand(command);
        if ( v_Video != null )
        {
            v_Video.setName(v_TSName);
        }
        
        return v_Video;
    }
    
    
    
    /**
     * 将视频MP4转M3U8格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-21
     * @version     v1.0
     * 
     * @param i_SourceFile   原视频文件
     * @param i_SavePath     保存路径
     * @param i_SplitTimeLen  视频分割时长（单位：秒）
     * @param i_TSUrl         附加M3U8索引文件中TS访问路径（可为空，表示不附加信息）
     * @return                成功时返回生成的M3U8全路径
     */
    public static String mp4ToM3U8(String i_SourceFile ,String i_SavePath ,int i_SplitTimeLen ,String i_TSUrl)
    {
        return mp4ToM3U8(i_SourceFile ,i_SavePath ,i_SplitTimeLen ,i_TSUrl ,false ,false);
    }
    
    
    
    /**
     * 将视频MP4转M3U8格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-25
     * @version     v1.0
     * 
     * @param i_SourceFile    原视频文件
     * @param i_SavePath      保存路径
     * @param i_SplitTimeLen  视频分割时长（单位：秒）
     * @param i_TSUrl         附加M3U8索引文件中TS访问路径（可为空，表示不附加信息）
     * @param i_IsDelMp4      是否删除原Mp4文件
     * @param i_IsDelTSFull   是否删除中间转换用的TS完整视频
     * @return                成功时返回生成的M3U8全路径
     */
    public static String mp4ToM3U8(String i_SourceFile ,String i_SavePath ,int i_SplitTimeLen ,String i_TSUrl ,boolean i_IsDelMp4 ,boolean i_IsDelTSFull)
    {
        VideoInfo v_TSAll = VideoHelp.mp4ToTS(i_SourceFile ,i_SavePath);
        if ( v_TSAll == null )
        {
            return null;
        }
        
        String v_M3U8Name = VideoHelp.tsToM3U8(v_TSAll.getName() ,i_SavePath ,i_SplitTimeLen ,i_TSUrl);
        
        if ( i_IsDelMp4 )
        {
            try
            {
                File v_Mp4File = new File(i_SourceFile);
                v_Mp4File.delete();
            }
            catch (Exception e)
            {
                $Logger.error(e);
            }
        }
        
        if ( i_IsDelTSFull )
        {
            // 删除中间转换用的TS完整视频
            v_TSAll.delete();
        }
        
        return v_M3U8Name;
    }
    
    
    
    /**
     * 将视频MP4转为TS格式的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-21
     * @version     v1.0
     *
     * @param i_SourceFile   原视频文件
     * @param i_SavePath     保存路径
     * @return               成功时返回生成的TS全路径
     */
    public static VideoInfo mp4ToTS(String i_SourceFile ,String i_SavePath)
    {
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_SourceFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_SourceFile + " can not read");
            return null;
        }
        
        String v_SName  = StringHelp.getFileShortName(v_SourceFile.getName());
        String v_TSName = i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator()) + v_SName + ".ts";
        
        List<String> command = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-y");
        command.add("-i");                       // 如果输出文件已存在则覆盖
        command.add(i_SourceFile);
        command.add("-vcodec");
        command.add("copy");
        command.add("-acodec");
        command.add("copy");
        command.add("-vbsf");
        command.add("h264_mp4toannexb");
        command.add(v_TSName);
        
        if ( !$IsBebug )
        {
            command.add("-loglevel");
            command.add("quiet");
        }
        
        VideoInfo v_Video = executeCommand(command);
        if ( v_Video != null )
        {
            v_Video.setName(v_TSName);
        }
        
        return v_Video;
    }
    
    
    
    /**
     * 将视频TS转为M3U8格式的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-21
     * @version     v1.0
     *
     * @param i_SourceFile    原视频文件
     * @param i_SavePath      保存路径
     * @param i_SplitTimeLen  视频分割时长（单位：秒）
     * @return                成功时返回生成的M3U8全路径
     */
    public static String tsToM3U8(String i_SourceFile ,String i_SavePath ,int i_SplitTimeLen)
    {
        return tsToM3U8(i_SourceFile ,i_SavePath ,i_SplitTimeLen ,null);
    }
    
    
    
    /**
     * 将视频TS转为M3U8格式的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-21
     * @version     v1.0
     *
     * @param i_SourceFile    原视频文件
     * @param i_SavePath      保存路径
     * @param i_SplitTimeLen  视频分割时长（单位：秒）
     * @param i_TSUrl         附加M3U8索引文件中TS访问路径（可为空，表示不附加信息）
     * @return                成功时返回生成的M3U8全路径
     */
    public static String tsToM3U8(String i_SourceFile ,String i_SavePath ,int i_SplitTimeLen ,String i_TSUrl)
    {
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_SourceFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_SourceFile + " can not read");
            return null;
        }
        
        String v_SName    = StringHelp.getFileShortName(v_SourceFile.getName());
        String v_M3U8Temp = Help.getSysTempPath() + Help.getSysPathSeparator() + Date.getNowTime().getTime() + v_SName + ".m3u8";
        String v_M3U8     = i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator()) + v_SName + ".m3u8";
        
        List<String> command = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");                       // 如果输出文件已存在则覆盖
        command.add(i_SourceFile);
        command.add("-c");
        command.add("copy");
        command.add("-map");
        command.add("0");
        command.add("-f");
        command.add("segment");
        command.add("-segment_list");
        command.add(v_M3U8Temp);
        command.add("-segment_time");
        command.add("" + i_SplitTimeLen);
        command.add(i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator()) + v_SName + "_" + i_SplitTimeLen + "s_%3d.ts");
        
        if ( !$IsBebug )
        {
            command.add("-loglevel");
            command.add("quiet");
        }
        
        try
        {
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            new PrintStream(videoProcess.getErrorStream()).start();
            new PrintStream(videoProcess.getInputStream()).start();
            videoProcess.waitFor();
            
            if ( !Help.isNull(i_TSUrl) )
            {
                FileHelp v_FileHelp = new FileHelp();
                v_FileHelp.setOverWrite(true);
                
                String v_M3U8Content = v_FileHelp.getContent(v_M3U8Temp ,"UTF-8" ,true);
                v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,v_SName ,i_TSUrl + v_SName);
                
                v_FileHelp.create(v_M3U8 ,v_M3U8Content);
                
                File v_M3U8TempFile = new File(v_M3U8Temp);
                v_M3U8TempFile.delete();
            }
            
            return v_M3U8;
        }
        catch (Exception e)
        {
            $Logger.error(e);
            return null;
        }
    }
    
    
    
    /**
     * 将视频文件分割成多个小的视频文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-11-14
     * @version     v1.0
     *
     * @param i_SourceFile   原视频文件
     * @param i_SavePath     保存路径
     * @param i_SplitSecond  每个分割视频文件的秒数
     * @param i_SplitSize    总分割数量
     * @return               返回所有分割小视频文件的全路径列表
     */
    public static List<String> splits(String i_SourceFile ,String i_SavePath ,int i_SplitSecond ,int i_SplitSize)
    {
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.warn(i_SourceFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.warn(i_SourceFile + " can not read");
            return null;
        }
        
        Date         v_Time        = new Date("2000-01-01 00:00:00");
        long         v_Start       = v_Time.getTime();
        String       v_SName       = StringHelp.getFileShortName(v_SourceFile.getName());
        String       v_Postfix     = StringHelp.getFilePostfix(v_SourceFile.getName());
        List<String> v_Ret         = new ArrayList<String>();
        int          v_SplitSecond = i_SplitSecond + 1;  // 用户想每9秒分割一次，所以是0秒到10秒间（不包含10秒）都为一个分段
        
        for (int i=0; i<i_SplitSize; i++)
        {
            String v_SplitName = i_SavePath + (i_SavePath.endsWith(Help.getSysPathSeparator()) ? "" : Help.getSysPathSeparator())
                               + v_SName
                               + "-"
                               + StringHelp.lpad(i + 1 ,3 ,"0")
                               + v_Postfix;
            
            split(i_SourceFile
                 ,new Date(v_Start)
                 ,new Date(v_Start + v_SplitSecond * 1000)
                 ,v_SplitName);
            
            v_Ret.add(v_SplitName);
            
            v_Start += v_SplitSecond * 1000;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 截取视频文件中的部分内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-11-14
     * @version     v1.0
     *
     * @param i_SourceFile    视频文件
     * @param i_StratTime     截取开始时间
     * @param i_EndTime       截取结束时间
     * @param i_SaveFileName  保存名称及路径
     * @return
     */
    public static boolean split(String i_SourceFile ,Date i_StratTime ,Date i_EndTime ,String i_SaveFileName)
    {
        List<String> command = new ArrayList<String>();

        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");
        command.add(i_SourceFile);
        command.add("-vcodec");                  // copy表示使用跟原视频一样的视频编解码器
        command.add("copy");
        command.add("-acodec");                  // copy表示使用跟原视频一样的音频编解码器。
        command.add("copy");
        command.add("-ss");                      // 设置从视频的哪个时间点开始截取
        command.add(i_StratTime.getHMS());
        command.add("-to");                      // 截到视频的哪个时间点结束
        command.add(i_EndTime.getHMS());
        command.add(i_SaveFileName);
        command.add("-y");
        
        if ( !$IsBebug )
        {
            command.add("-loglevel");
            command.add("quiet");
        }
        
        try
        {
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            new PrintStream(videoProcess.getErrorStream()).start();
            new PrintStream(videoProcess.getInputStream()).start();
            videoProcess.waitFor();
            return true;
        }
        catch (Exception e)
        {
            $Logger.error(e);
            return false;
        }
    }
    
    
    
    /**
     * 获取视频信息（如，视频的宽度、高度）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-09-26
     * @version     v1.0
     *
     * @param i_VideoFile  视频文件的路径
     * @return
     */
    public static VideoInfo getVideoInfo(String i_VideoFile)
    {
        File v_SourceFile = new File(i_VideoFile);
        if ( !v_SourceFile.isFile() )
        {
            $Logger.error(i_VideoFile + " is not file");
            return null;
        }
        else if ( !v_SourceFile.canRead() )
        {
            $Logger.error(i_VideoFile + " can not read");
            return null;
        }
        
        List<String> command  = new ArrayList<String>();
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg");
        command.add("-i");
        command.add(i_VideoFile);
        
        return executeCommand(command);
    }
    
    
    
    /**
     * 执行并解释命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-28
     * @version     v1.0
     * 
     * @param i_Command
     * @return
     */
    private static VideoInfo executeCommand(List<String> i_Command)
    {
        BufferedReader    v_Reader = null;
        InputStream       v_Input  = null;
        InputStreamReader v_InputR = null;
        try
        {
            Process videoProcess = new ProcessBuilder(i_Command).redirectErrorStream(true).start();
            new PrintStream(videoProcess.getErrorStream()).start();
            v_Input  = videoProcess.getInputStream();
            v_InputR = new InputStreamReader(v_Input);
            v_Reader = new BufferedReader(v_InputR);
            
            VideoInfo v_Video          = new VideoInfo();
            String    v_Line           = "";
            boolean   v_IsFindDuration = false;    // 是否解释出时长
            boolean   v_IsFindXY       = false;    // 是否解释出宽高
            while ( (v_Line=v_Reader.readLine())!=null )
            {
                if ( Help.isNull(v_Line) )
                {
                    continue;
                }
                
                // 解释出时长
                if ( !v_IsFindDuration )
                {
                    // Duration: 00:00:58.26, start: 11720.738844, bitrate: 2032 kb/s
                    boolean v_IsHaveDuration = StringHelp.isContains(v_Line ,true ,"Duration" ,",");
                    if ( v_IsHaveDuration )
                    {
                        String [] v_LinePamas = v_Line.trim().split(",");
                        for (String v_LP : v_LinePamas)
                        {
                            String [] v_NameValue = v_LP.trim().split(":");
                            if ( v_NameValue.length >= 2 )
                            {
                                v_NameValue[0] = v_NameValue[0].trim();
                                
                                if ( "Duration".equals(v_NameValue[0]) )
                                {
                                    v_Video.setDuration(v_LP.substring(v_LP.indexOf(":") + 1).trim());
                                    v_IsFindDuration = true;
                                }
                                else if ( "bitrate".equals(v_NameValue[0]) )
                                {
                                    v_Video.setBitrate(v_NameValue[1].trim());
                                    v_IsFindDuration = true;
                                }
                            }
                        }
                    }
                }
                
                if ( !v_IsFindXY )
                {
                    // Stream #0:0[0x1e0]: Video: h264 (Main), yuvj420p(pc, bt709, progressive), 1280x720, 22 fps, 25 tbr, 90k tbn, 44 tbc
                    boolean v_IsHaveX = StringHelp.isContains(v_Line ,true ,"x" ,",");
                    if ( v_IsHaveX )
                    {
                        String [] v_LinePamas = v_Line.trim().split(",");
                        for (String v_LP : v_LinePamas)
                        {
                            String [] v_NameValue = v_LP.split("x");
                            if ( v_NameValue.length == 2 )
                            {
                                v_NameValue[0] = v_NameValue[0].trim();
                                v_NameValue[1] = v_NameValue[1].trim();
                                if ( Help.isNumber(v_NameValue[0]) && Help.isNumber(v_NameValue[1]) )
                                {
                                    int v_Width  = Integer.parseInt(v_NameValue[0]);
                                    int v_Height = Integer.parseInt(v_NameValue[1]);
                                    
                                    if ( v_Width > 0 && v_Height > 0 )
                                    {
                                        v_Video.setWidth( v_Width);
                                        v_Video.setHeight(v_Height);
                                        
                                        v_IsFindXY = true;
                                    }
                                }
                            }
                        }
                    }
                }
                
                if ( v_IsFindDuration && v_IsFindXY )
                {
                    return v_Video;
                }
            }
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        finally
        {
            if ( v_Reader != null )
            {
                try
                {
                    v_Reader.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_Reader = null;
            }
            
            if ( v_InputR != null )
            {
                try
                {
                    v_InputR.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_InputR = null;
            }
            
            if ( v_Input != null )
            {
                try
                {
                    v_Input.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_Input = null;
            }
        }
        
        return null;
    }
    
}
