package org.hy.common.video;





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
     * 设置：宽度
     * 
     * @param width 
     */
    public void setWidth(Integer width)
    {
        this.width = width;
    }

    
    /**
     * 设置：高度
     * 
     * @param height 
     */
    public void setHeight(Integer height)
    {
        this.height = height;
    }
    
}
