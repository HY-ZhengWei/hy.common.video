package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：角点检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_010_Harris
{
    private static final Logger $Logger = new Logger(JU_010_Harris.class ,true);
    
    
    
    public JU_010_Harris()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void Harris()
    {
        Mat v_MSource = Imgcodecs.imread(JU_010_Harris.class.getResource("JU_010_Harris.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);   // 灰度图
        HighGui.imshow("灰度图" ,v_MTarget);
        HighGui.waitKey(0);
        
        MatOfPoint v_Corners        = new MatOfPoint(); // 存储检测结果
        boolean    v_HarrisDetector = true;             // 使用哪种算法
        Imgproc.goodFeaturesToTrack(v_MTarget ,v_Corners ,500 ,0.01 ,20.0 ,new Mat() ,3 ,v_HarrisDetector ,0.04);  // 角点检测
        
        // 标记角点
        Point [] v_PArr = v_Corners.toArray();
        for (int x=0; x<v_PArr.length; x++)
        {
            Point  v_Center = v_PArr[x];    // 圆心
            int    v_Radius = 4;            // 半径
            Scalar v_Color  = new Scalar(255 ,0 ,0);
            
            Imgproc.circle(v_MSource ,v_Center ,v_Radius ,v_Color ,2);
        }
        
        HighGui.imshow("角点检测" ,v_MSource);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
}