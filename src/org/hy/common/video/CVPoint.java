package org.hy.common.video;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class CVPoint
{
    
    private Double  x;
    
    private Double  xMin;
    
    private Double  xMax;
    
    private Double  y;
    
    private Double  yMin;
    
    private Double  yMax;
    
    private boolean isChange;
    
    private Rect    rect;
    
    
    
    public CVPoint()
    {
        this.xMin     = Double.MAX_VALUE;
        this.xMax     = Double.MIN_VALUE;
        this.yMin     = Double.MAX_VALUE;
        this.yMax     = Double.MIN_VALUE;
        this.isChange = false;
    }
    
    
    
    @Override
    public CVPoint clone()
    {
        CVPoint v_New = new CVPoint();
        
        v_New.setX(   this.x);
        v_New.setXMin(this.xMin);
        v_New.setXMax(this.xMax);
        v_New.setY(   this.y);
        v_New.setYMin(this.yMin);
        v_New.setYMax(this.yMax);
        
        return v_New;
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
        
        v_New.setX(   this.x);
        v_New.setXMin(this.xMin + (this.xMax - this.xMin) * (1 - i_Rate));
        v_New.setXMax(this.xMax);
        v_New.setY(   this.y);
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
        
        v_New.setX(   this.x);
        v_New.setXMin(this.xMin);
        v_New.setXMax(this.xMin + (this.xMax - this.xMin) * i_Rate);
        v_New.setY(   this.y);
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
    public void setMaxMin(Point i_Point)
    {
        this.setXMin(i_Point.x);
        this.setXMax(i_Point.x);
        this.setYMin(i_Point.y);
        this.setYMax(i_Point.y);
    }

    
    
    public Double getX()
    {
        return x;
    }

    
    public void setX(Double x)
    {
        this.x = x;
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

    
    public Double getY()
    {
        return y;
    }

    
    public void setY(Double y)
    {
        this.y = y;
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
    
}
