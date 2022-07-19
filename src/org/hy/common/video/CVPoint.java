package org.hy.common.video;

import org.hy.common.Help;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class CVPoint
{
    
    private Double  xMin;
    
    private Double  xMax;
    
    private Double  yMin;
    
    private Double  yMax;
    
    private boolean isChange;
    
    private boolean javaCVIsChange;
    
    private Rect    rect;
    
    // private org.bytedeco.opencv.opencv_core.Rect javaCVRect;
    
    private Integer count;
    
    private Double  xHelp;
    
    private Double  yHelp;
    
    private Integer faceNo;
    
    
    
    public CVPoint()
    {
        this.xMin           = Double.MAX_VALUE;
        this.xMax           = Double.MIN_VALUE;
        this.yMin           = Double.MAX_VALUE;
        this.yMax           = Double.MIN_VALUE;
        this.xHelp          = 0D;
        this.yHelp          = 0D;
        this.isChange       = false;
        this.javaCVIsChange = false;
        this.count          = 1;
    }
    
    
    
    public CVPoint(double i_XMin ,double i_YMin ,double i_XMax ,double i_YMax)
    {
        this.xMin           = i_XMin;
        this.xMax           = i_XMax;
        this.yMin           = i_YMin;
        this.yMax           = i_YMax;
        this.xHelp          = 0D;
        this.yHelp          = 0D;
        this.isChange       = true;
        this.javaCVIsChange = true;
        this.count          = 1;
    }
    
    
    
    public boolean isOK()
    {
        if ( this.xMin >= this.xMax )
        {
            return false;
        }
        
        if ( this.yMin >= this.yMax )
        {
            return false;
        }
        
        if ( this.xMin < 0 || this.yMin < 0 )
        {
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 组合成一个更大的矩形
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Other
     * @return
     */
    public CVPoint union(CVPoint i_Other)
    {
        this.setXMin(i_Other.getXMin());
        this.setXMax(i_Other.getXMax());
        this.setYMin(i_Other.getYMin());
        this.setYMax(i_Other.getYMax());
        this.setCount(this.getCount() + i_Other.getCount());
        
        this.xHelp = Math.min(this.xHelp ,i_Other.getXHelp());
        this.yHelp = Math.min(this.yHelp ,i_Other.getYHelp());
        
        return this;
    }
    
    
    
    /**
     * 组合成一个更大的矩形
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Other
     * @return
     */
    public CVPoint union(double i_XMin ,double i_YMin ,double i_XMax ,double i_YMax)
    {
        this.setXMin(i_XMin);
        this.setXMax(i_XMax);
        this.setYMin(i_YMin);
        this.setYMax(i_YMax);
        this.setCount(this.getCount() + 1);
        
        return this;
    }
    
    
    
    public CVPoint ceil(int i_BlockSize)
    {
        if ( this.xMin % i_BlockSize != 0 )
        {
            this.xMin = Help.round(this.xMin * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        if ( this.xMax % i_BlockSize != 0 )
        {
            this.xMax = Help.round(this.xMax * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        
        if ( this.yMin % i_BlockSize != 0 )
        {
            this.yMin = Help.round(this.yMin * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        if ( this.yMax % i_BlockSize != 0 )
        {
            this.yMax = Help.round(this.yMax * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        this.isChange = true;
        
        return this;
    }
    
    
    
    @Override
    public CVPoint clone()
    {
        CVPoint v_New = new CVPoint();
        
        v_New.setXMin( this.xMin);
        v_New.setXMax( this.xMax);
        v_New.setYMin( this.yMin);
        v_New.setYMax( this.yMax);
        v_New.setCount(this.count);
        v_New.setXHelp(this.xHelp);
        v_New.setYHelp(this.yHelp);
        
        return v_New;
    }
    
    
    
    /**
     * 按比例缩放
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-18
     * @version     v1.0
     * 
     * @param i_Value  缩放比例
     */
    public CVPoint scale(double i_Scale)
    {
        this.xMin    *= i_Scale;
        this.yMin    *= i_Scale;
        this.xMax    *= i_Scale;
        this.yMax    *= i_Scale;
        this.isChange = true;
        
        if ( this.xHelp != null )
        {
            this.xHelp *= i_Scale;
            this.yHelp *= i_Scale;
        }
        
        return this;
    }
    
    
    
    /**
     * 缩放
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Value  正值为扩大，负值为缩小
     */
    public CVPoint scale(double i_Value ,int i_MaxWidth ,int i_MaxHeight)
    {
        this.setXMin(Math.max(this.getXMin() - i_Value ,0));
        this.setXMax(Math.min(this.getXMax() + i_Value ,i_MaxWidth));
        this.setYMin(Math.max(this.getYMin() - i_Value ,0));
        this.setYMax(Math.min(this.getYMax() + i_Value ,i_MaxHeight));
        
        return this;
    }
    
    
    
    /**
     * 剪裁右侧
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-04
     * @version     v1.0
     * 
     * @param i_Rate  剪裁右侧的百分比
     * @return
     */
    public CVPoint cutRight(double i_Rate)
    {
        CVPoint v_New = new CVPoint();
        
        v_New.setXMin(this.xMin + (this.xMax - this.xMin) * (1 - i_Rate));
        v_New.setXMax(this.xMax);
        v_New.setYMin(this.yMin);
        v_New.setYMax(this.yMax);
        
        return v_New;
    }
    
    
    
    /**
     * 剪裁左侧
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-04
     * @version     v1.0
     * 
     * @param i_Rate  剪裁左侧的百分比
     * @return
     */
    public CVPoint cutLeft(double i_Rate)
    {
        CVPoint v_New = new CVPoint();
        
        v_New.setXMin(this.xMin);
        v_New.setXMax(this.xMin + (this.xMax - this.xMin) * i_Rate);
        v_New.setYMin(this.yMin);
        v_New.setYMax(this.yMax);
        
        return v_New;
    }
    
    
    
    public synchronized Rect toRect()
    {
        if ( this.isChange )
        {
            this.isChange = false;
            this.rect = new Rect(this.xMin.intValue()
                                ,this.yMin.intValue()
                                ,this.getWidth().intValue()
                                ,this.getHeight().intValue());
        }
        
        return this.rect;
    }
    
    
    
    /*
    public synchronized org.bytedeco.opencv.opencv_core.Rect javaCVToRect()
    {
        if ( this.javaCVIsChange )
        {
            this.javaCVIsChange = false;
            this.javaCVRect = new org.bytedeco.opencv.opencv_core.Rect(
                                 this.xMin.intValue()
                                ,this.yMin.intValue()
                                ,this.getWidth().intValue()
                                ,this.getHeight().intValue());
        }
        
        return this.javaCVRect;
    }
    */
    
    
    
    public double getRectArea()
    {
        if ( this.toRect() != null )
        {
            return this.toRect().area();
        }
        else
        {
            return 0D;
        }
    }
    
    
    
    /**
     * 获取宽度
     * 
     * @return
     */
    public Double getWidth()
    {
        return this.getXMax() - this.getXMin();
    }
    
    
    
    /**
     * 获取高度
     * 
     * @return
     */
    public Double getHeight()
    {
        return this.getYMax() - this.getYMin();
    }
    
    
    
    /**
     * 通过Point对象对比设置最大值、最小值
     * 
     * @param i_Point
     */
    public void setMaxMin(Point i_Point ,double i_Scale)
    {
        this.setXMin(i_Point.x / i_Scale);
        this.setXMax(i_Point.x / i_Scale);
        this.setYMin(i_Point.y / i_Scale);
        this.setYMax(i_Point.y / i_Scale);
    }

    
    
    public Double getXMin()
    {
        return this.xMin == Double.MAX_VALUE ? null : this.xMin;
    }

    
    public void setXMin(Double i_XMin)
    {
        this.xMin     = Math.min(this.xMin ,i_XMin);
        this.isChange = true;
    }

    
    public Double getXMax()
    {
        return this.xMax == Double.MIN_VALUE ? null : this.xMax;
    }

    
    public void setXMax(Double i_XMax)
    {
        this.xMax     = Math.max(this.xMax ,i_XMax);
        this.isChange = true;
    }

    
    public Double getYMin()
    {
        return this.yMin == Double.MAX_VALUE ? null : this.yMin;
    }

    
    public void setYMin(Double i_YMin)
    {
        this.yMin     = Math.min(this.yMin ,i_YMin);
        this.isChange = true;
    }

    
    public Double getYMax()
    {
        return this.yMax == Double.MIN_VALUE ? null : this.yMax;
    }

    
    public void setYMax(Double i_YMax)
    {
        this.yMax     = Math.max(this.yMax ,i_YMax);
        this.isChange = true;
    }

    
    public Integer getCount()
    {
        return count;
    }

    
    public void setCount(Integer count)
    {
        this.count = count;
    }

    
    public Double getXHelp()
    {
        return xHelp;
    }

    
    public void setXHelp(Double xHelp)
    {
        this.xHelp = xHelp;
    }

    
    public Double getYHelp()
    {
        return yHelp;
    }

    
    public void setYHelp(Double yHelp)
    {
        this.yHelp = yHelp;
    }


    public Integer getFaceNo()
    {
        return faceNo;
    }

    
    public void setFaceNo(Integer faceNo)
    {
        this.faceNo = faceNo;
    }
    
}
