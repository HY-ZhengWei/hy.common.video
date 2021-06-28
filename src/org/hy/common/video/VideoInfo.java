package org.hy.common.video;

import java.io.File;

import org.hy.common.Help;

/**
 * 视频信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-09-26
 * @version     v1.0
 */
public class VideoInfo
{
    
    /** 宽度 */
    private Integer width;
    
    /** 高度 */
    private Integer height;
    
    /** 时长 */
    private String  duration;
    
    /** 比特率 */
    private String  bitrate;
    
    /** 视频文件名称 */
    private String  name;

    
    
    /**
     * 获取：宽度
     */
    public Integer getWidth()
    {
        return width;
    }

    
    /**
     * 获取：高度
     */
    public Integer getHeight()
    {
        return height;
    }
    
    
    /**
     * 获取：比特率
     */
    public String getBitrate()
    {
        return bitrate;
    }
    
    
    /**
     * 获取：时长
     */
    public String getDuration()
    {
        return duration;
    }
    
    
    /**
     * 获取：视频文件名称
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * 设置：宽度
     * 
     * @param width
     */
    public VideoInfo setWidth(Integer width)
    {
        this.width = width;
        return this;
    }

    
    /**
     * 设置：高度
     * 
     * @param height
     */
    public VideoInfo setHeight(Integer height)
    {
        this.height = height;
        return this;
    }
    
    
    /**
     * 获取：比特率
     */
    public VideoInfo setBitrate(String i_Bitrate)
    {
        this.bitrate = i_Bitrate;
        return this;
    }
    
    
    /**
     * 获取：时长
     */
    public VideoInfo setDuration(String i_Duration)
    {
        this.duration = i_Duration;
        return this;
    }
    
    
    /**
     * 获取：视频文件名称
     */
    public VideoInfo setName(String i_Name)
    {
        this.name = i_Name;
        return this;
    }
    
    
    /**
     * 删除视频文件名称
     */
    public boolean delete()
    {
        if ( !Help.isNull(this.name) )
        {
            File v_File = new File(this.name);
            return v_File.delete();
        }
        
        return false;
    }
    
}
