package org.hy.common.video.junit.ocr;

import org.opencv.core.Mat;

public class LPRInfo
{
    
    private String type;
    
    private Mat    carNumberImage;
    
    private Mat    carNumberText;
    
    private double areaValue;
    
    private double colorValue;
    
    private double sizeValue;
    
    private double value;
    
    
    
    public LPRInfo(String i_Type ,Mat i_CarNumberImage ,Mat i_CarNumberText ,double i_AreaValue ,double i_ColorValue ,double i_SizeValue)
    {
        this.type           = i_Type;
        this.carNumberImage = i_CarNumberImage;
        this.carNumberText  = i_CarNumberText;
        this.areaValue      = i_AreaValue;
        this.colorValue     = i_ColorValue;
        this.sizeValue      = i_SizeValue;
    }
    
    
    public String getType()
    {
        return type;
    }

    
    public void setType(String type)
    {
        this.type = type;
    }

    
    public Mat getCarNumberImage()
    {
        return carNumberImage;
    }

    
    public void setCarNumberImage(Mat carNumberImage)
    {
        this.carNumberImage = carNumberImage;
    }

    
    public double getColorValue()
    {
        return colorValue;
    }

    
    public void setColorValue(double colorValue)
    {
        this.colorValue = colorValue;
    }

    
    public double getSizeValue()
    {
        return sizeValue;
    }

    
    public void setSizeValue(double sizeValue)
    {
        this.sizeValue = sizeValue;
    }

    
    public double getAreaValue()
    {
        return areaValue;
    }

    
    public void setAreaValue(double areaValue)
    {
        this.areaValue = areaValue;
    }

    
    public double getValue()
    {
        return value;
    }

    
    public void setValue(double value)
    {
        this.value = value;
    }

    
    public Mat getCarNumberText()
    {
        return carNumberText;
    }

    
    public void setCarNumberText(Mat carNumberText)
    {
        this.carNumberText = carNumberText;
    }
    
}
