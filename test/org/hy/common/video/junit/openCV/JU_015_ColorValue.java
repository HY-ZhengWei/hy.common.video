package org.hy.common.video.junit.openCV;

import java.util.Map;

import org.hy.common.Counter;
import org.hy.common.Help;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：测试颜色
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-06
 * @version     v1.0
 */
public class JU_015_ColorValue
{
    private static final Logger $Logger = new Logger(JU_015_ColorValue.class ,true);
    
    
    
    public JU_015_ColorValue()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void colorValue()
    {
        Mat v_MSource = Imgcodecs.imread(JU_015_ColorValue.class.getResource("JU_015_ColorValue.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        Imgproc.bilateralFilter(v_MSource ,v_MTarget ,15 ,15 ,15);
        Imgproc.cvtColor(v_MTarget ,v_MTarget ,Imgproc.COLOR_BGR2HSV);   // 注意：BGR，不是RGB
        Counter<String> v_Counter = new Counter<String>();
        
        int v_Count = 0;
        for (int x=0; x<v_MTarget.rows(); x++)
        {
            for (int y=0; y<v_MTarget.cols(); y++)
            {
                double v_H = v_MTarget.get(x ,y)[0];
                double v_S = v_MTarget.get(x ,y)[1];
                double v_V = v_MTarget.get(x ,y)[2];
                
                if ( v_H == 0 && v_S == 0 && v_V == 0 )
                {
                    continue;
                }
                
                v_Counter.put(v_H + " ," + v_S + " ," + v_V);
                v_Count++;
                // System.out.println(v_H + " ," + v_S + " ," + v_V);
            }
        }
        
        Map<String ,Long> v_ColorValue = Help.toSortByMap(v_Counter);
        for (Map.Entry<String ,Long> v_Item : v_ColorValue.entrySet())
        {
            System.out.println(v_Item.getValue() + " = " + v_Item.getKey() + " 比例" + (v_Count));
        }
        
        HighGui.imshow("HSV" ,v_MTarget);
        HighGui.waitKey(0);
        
        
        Scalar v_LowerColor   = new Scalar(0   ,0   ,50);
        Scalar v_UpperColor   = new Scalar(165 ,20  ,80);
        Core.inRange(v_MTarget ,v_LowerColor ,v_UpperColor ,v_MTarget);
        HighGui.imshow("HSV" ,v_MTarget);
        HighGui.waitKey(0);
    }
    
}