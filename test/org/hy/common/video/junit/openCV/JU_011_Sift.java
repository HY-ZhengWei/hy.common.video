package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：关键点检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_011_Sift
{
    private static final Logger $Logger = new Logger(JU_011_Sift.class ,true);
    
    
    
    public JU_011_Sift()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void sift()
    {
        Mat v_MSource = Imgcodecs.imread(JU_011_Sift.class.getResource("JU_010_Harris.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        MatOfKeyPoint v_PKey = new MatOfKeyPoint();   // 存储关键点
        SIFT          v_Sift = SIFT.create(500);
        v_Sift.detect(v_MSource ,v_PKey);             // 检测关键点
        
        // 绘制关键点
        Scalar v_Color = new Scalar(0 ,0 ,255);
        Features2d.drawKeypoints(v_MSource ,v_PKey ,v_MTarget ,v_Color ,Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS);
        
        HighGui.imshow("关键点检测：专利影响，不可商用" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void orb()
    {
        Mat v_MSource = Imgcodecs.imread(JU_011_Sift.class.getResource("JU_010_Harris.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);   // 灰度图
        HighGui.imshow("灰度图" ,v_MTarget);
        HighGui.waitKey(0);
        
        MatOfKeyPoint v_PKey = new MatOfKeyPoint();   // 存储关键点
        ORB           v_Orb  = ORB.create(100 ,1.2F ,8 ,31 ,0 ,2 ,ORB.HARRIS_SCORE ,31 ,30);
        v_Orb.detect(v_MTarget ,v_PKey);              // 检测关键点
        
        // 绘制关键点
        Scalar v_Color = new Scalar(0 ,0 ,255);
        Features2d.drawKeypoints(v_MSource ,v_PKey ,v_MTarget ,v_Color ,Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS);
        
        HighGui.imshow("关键点检测：没有专利的影响" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
        
    }
    
}