package org.hy.common.video;

import org.opencv.core.Scalar;

public class CVColor
{
    
    private String name;
    
    private Scalar lower;
    
    private Scalar upper;
    
    private Double withdHeightScale;
    
    private int    count;
    
    
    public CVColor()
    {
        
    }
    
    
    public CVColor(String i_Name ,Scalar i_Lower ,Scalar i_Upper)
    {
        this(i_Name ,i_Lower ,i_Upper ,0D ,0);
    }
    
    
    public CVColor(String i_Name ,Scalar i_Lower ,Scalar i_Upper ,Double i_WithdHeightScale ,int i_Count)
    {
        this.name             = i_Name;
        this.lower            = i_Lower;
        this.upper            = i_Upper;
        this.withdHeightScale = i_WithdHeightScale;
        this.count            = i_Count;
    }

    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public Scalar getLower()
    {
        return lower;
    }

    
    public void setLower(Scalar lower)
    {
        this.lower = lower;
    }

    
    public Scalar getUpper()
    {
        return upper;
    }

    
    public void setUpper(Scalar upper)
    {
        this.upper = upper;
    }

    
    public Double getWithdHeightScale()
    {
        return withdHeightScale;
    }

    
    public void setWithdHeightScale(Double withdHeightScale)
    {
        this.withdHeightScale = withdHeightScale;
    }

    
    public int getCount()
    {
        return count;
    }

    
    public void setCount(int count)
    {
        this.count = count;
    }
    
}
