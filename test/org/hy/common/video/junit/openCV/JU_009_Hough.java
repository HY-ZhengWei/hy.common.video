package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：霍夫变换 & 霍夫线检测 &霍夫圆
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_009_Hough
{
    private static final Logger $Logger = new Logger(JU_009_Hough.class ,true);
    
    
    
    public JU_009_Hough()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void HoughLines()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_004_Perspective.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.Canny(v_MSource ,v_MTarget ,50 ,200 ,3 ,false);
        HighGui.imshow("边缘检测" ,v_MTarget);
        HighGui.waitKey(0);
        
        Mat v_Lines = new Mat(); // 存储检测结果
        Imgproc.HoughLines(v_MTarget ,v_Lines ,1 ,Math.PI / 180 ,150);  // 霍夫线检测
        
        // 绘制线
        for (int x=0; x<v_Lines.rows(); x++)
        {
            double v_Rho   = v_Lines.get(x ,0)[0];
            double v_Theta = v_Lines.get(x ,0)[1];
            double v_A     = Math.cos(v_Theta);
            double v_B     = Math.sin(v_Theta);
            double v_X0    = v_A * v_Rho;
            double v_Y0    = v_B * v_Rho;
            Point  v_P1    = new Point(Math.round(v_X0 + 1000 * (-v_B)) ,Math.round(v_Y0 + 1000 * v_A));
            Point  v_P2    = new Point(Math.round(v_X0 - 1000 * (-v_B)) ,Math.round(v_Y0 - 1000 * v_A));
            Scalar v_Color = new Scalar(0 ,0 ,255);
            
            Imgproc.line(v_MSource ,v_P1 ,v_P2 ,v_Color ,2);
        }
        
        HighGui.imshow("霍夫线检测" ,v_MSource);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void HoughCircles()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_004_Perspective.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_RGB2GRAY);   // 灰度图
        HighGui.imshow("灰度图" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.medianBlur(v_MTarget ,v_MTarget ,5);   // 中值滤波
        HighGui.imshow("中值滤波" ,v_MTarget);
        HighGui.waitKey(0);
        
        Mat v_Circles = new Mat(); // 存储检测结果
        Imgproc.HoughCircles(v_MTarget ,v_Circles ,Imgproc.HOUGH_GRADIENT ,1.0 ,v_MTarget.rows() / 24.0 ,100.0 ,30.0 ,1 ,100);  // 霍夫圆检测
        
        // 绘制线
        for (int x=0; x<v_Circles.cols(); x++)
        {
            double [] v_Circle = v_Circles.get(x ,0);
            Point     v_Center = new Point(Math.round(v_Circle[0]) ,Math.round(v_Circle[1]));    // 圆心
            int       v_Radius = Math.round(Math.round(v_Circle[2]));                            // 半径
            Scalar    v_Color  = new Scalar(255 ,0 ,0);
            
            Imgproc.circle(v_MSource ,v_Center ,v_Radius ,v_Color ,3 ,8 ,0);
        }
        
        HighGui.imshow("霍夫圆检测" ,v_MSource);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
}