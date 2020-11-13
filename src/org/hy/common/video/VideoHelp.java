package org.hy.common.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;





/**
 * 视频操作的相关工具类
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-14
 * @version     v1.0
 *              v2.0  2020-09-26  添加：获取视频信息（如，视频的宽度、高度）
 *              v3.0  2020-09-27  添加：将视频的前多少帧转换成一个动图（Animated Gif）
 */
public class VideoHelp
{
    
    public static String $FFMpegHome;
    
    
    
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
            System.out.println(i_VideoFile + " is not file"); 
            return false; 
        }
        else if ( !v_SourceFile.canRead() )
        {
            System.out.println(i_VideoFile + " can not read"); 
            return false; 
        }
        File v_GifFile = new File(i_GifFile);
        if ( v_GifFile.exists() )
        {
            System.out.println(i_GifFile + " is exists"); 
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
            e.printStackTrace(); 
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
     * @return
     */
    public static boolean toMP4(String i_SourceFile ,String i_SavePath ,int i_ResolutionX ,int i_ResolutionY) 
    { 
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() ) 
        { 
            System.out.println(i_SourceFile + " is not file"); 
            return false; 
        }
        else if ( !v_SourceFile.canRead() )
        {
            System.out.println(i_SourceFile + " can not read"); 
            return false; 
        }
        
        String v_SName = StringHelp.getFileShortName(v_SourceFile.getName());
        
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
        command.add(i_SavePath + Help.getSysPathSeparator() + v_SName + ".mp4"); 
        command.add("-y");                       // 源视频文件
        
        try 
        { 
            // 方案1 
            //        Process videoProcess = Runtime.getRuntime().exec(ffmpegPath + "ffmpeg -i " + oldfilepath 
            //                + " -ab 56 -ar 22050 -qscale 8 -r 15 -s 600x500 " 
            //                + outputPath + "a.flv"); 
            // 方案2 
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start(); 
            new PrintStream(videoProcess.getErrorStream()).start(); 
            new PrintStream(videoProcess.getInputStream()).start(); 
            videoProcess.waitFor(); 
            return true; 
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
            return false; 
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
     * @return
     */
    public static boolean splits(String i_SourceFile ,String i_SavePath ,int i_SplitSecond ,int i_SplitSize) 
    { 
        File v_SourceFile = new File(i_SourceFile);
        if ( !v_SourceFile.isFile() ) 
        { 
            System.out.println(i_SourceFile + " is not file"); 
            return false; 
        }
        else if ( !v_SourceFile.canRead() )
        {
            System.out.println(i_SourceFile + " can not read"); 
            return false; 
        }
        
        Date   v_Time    = new Date("2000-01-01 00:00:00");
        long   v_Start   = v_Time.getTime();
        String v_SName   = StringHelp.getFileShortName(v_SourceFile.getName());
        String v_Postfix = StringHelp.getFilePostfix(v_SourceFile.getName());
        
        for (int i=0; i<i_SplitSize; i++)
        {
            split(i_SourceFile
                 ,new Date(v_Start) 
                 ,new Date(v_Start + i_SplitSecond * 1000) 
                 ,i_SavePath + Help.getSysPathSeparator()
                             + v_SName
                             + "-" 
                             + StringHelp.lpad(i + 1 ,3 ,"0") 
                             + v_Postfix);
            v_Start += i_SplitSecond * 1000;
        }
        
        return true;
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
            e.printStackTrace(); 
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
            System.out.println(i_VideoFile + " is not file"); 
            return null; 
        }
        else if ( !v_SourceFile.canRead() )
        {
            System.out.println(i_VideoFile + " can not read"); 
            return null; 
        }
        
        List<String>   command  = new ArrayList<String>(); 
        BufferedReader v_Reader = null;
        
        command.add($FFMpegHome + Help.getSysPathSeparator() + "bin" + Help.getSysPathSeparator() + "ffmpeg"); 
        command.add("-i"); 
        command.add(i_VideoFile); 
        
        try 
        {
            String  v_Line       = "";
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start(); 
            new PrintStream(videoProcess.getErrorStream()).start(); 
            
            v_Reader = new BufferedReader(new InputStreamReader(videoProcess.getInputStream()));
            
            while ( (v_Line=v_Reader.readLine())!=null ) 
            {
                if ( Help.isNull(v_Line) )
                {
                    continue;
                }
                
                if ( !StringHelp.isContains(v_Line ,true ,"x" ,",") )
                {
                    continue;
                }
                
                String [] v_LinePamas = v_Line.split(",");
                
                for (String v_LP : v_LinePamas)
                {
                    if ( v_LP.trim().indexOf("x") >= 0 )
                    {
                        String [] v_WH = v_LP.split("x");
                        
                        if ( v_WH.length == 2 )
                        {
                            if ( Help.isNumber(v_WH[0].trim()) && Help.isNumber(v_WH[1].trim()) )
                            {
                                int v_Width  = Integer.parseInt(v_WH[0].trim());
                                int v_Height = Integer.parseInt(v_WH[1].trim());
                                
                                if ( v_Width > 0 && v_Height > 0 )
                                {
                                    VideoInfo v_Video = new VideoInfo();
                                    
                                    v_Video.setWidth( v_Width);
                                    v_Video.setHeight(v_Height);
                                    
                                    return v_Video;
                                }
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
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
        }
        
        return null;
    }
    
}
