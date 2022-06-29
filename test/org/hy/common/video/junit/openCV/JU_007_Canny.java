package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：边缘检测与轮廓算法
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_007_Canny
{
    private static final Logger $Logger = new Logger(JU_007_Canny.class ,true);
    
    
    
    public JU_007_Canny()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void Canny()
    {
        // 读取图片，并按灰度图读取
        Mat v_MSource = Imgcodecs.imread(JU_007_Canny.class.getResource("JU_007_Canny.png").getFile().substring(1) ,Imgcodecs.IMREAD_GRAYSCALE);
        Mat v_MTarget = new Mat();
        HighGui.imshow("灰度图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.Sobel(v_MSource ,v_MTarget ,-1 ,0 ,1);
        HighGui.imshow("轮廓算法：Sobel" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.Scharr(v_MSource ,v_MTarget ,-1 ,0 ,1);
        HighGui.imshow("轮廓算法：Scharr" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.GaussianBlur(v_MSource ,v_MTarget ,new Size(31 ,5) ,80 ,3);   // 高斯模糊
        Imgproc.Laplacian(v_MSource ,v_MTarget ,0);
        HighGui.imshow("轮廓算法：Laplace拉普拉斯算法" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.Canny(v_MSource ,v_MTarget ,60 ,200);
        HighGui.imshow("边缘检测：Canny" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void Canny_FindContours()
    {
        // 读取图片，并按灰度图读取
        Mat v_MSource = Imgcodecs.imread(JU_007_Canny.class.getResource("JU_007_Canny.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_RGB2GRAY);   // 灰度图
        HighGui.imshow("灰度图" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.Canny(v_MTarget ,v_MTarget ,60 ,200);
        HighGui.imshow("边缘检测：Canny" ,v_MTarget);
        HighGui.waitKey(0);
        
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MTarget ,v_Contours ,new Mat() ,Imgproc.RETR_LIST ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        Scalar v_BGColor        = new Scalar(255 ,255 ,255);  // 背景色
        Scalar v_FGColor1       = new Scalar(0   ,0   ,0);    // 前景色
        Scalar v_FGColor2       = new Scalar(255 ,0   ,0);    // 前景色
        Mat    v_ContoursTarget = new Mat(v_MTarget.height() ,v_MTarget.width() ,CvType.CV_8SC3 ,v_BGColor);
        
        for (int x=0; x<v_Contours.size(); x++)
        {
            Imgproc.drawContours(v_ContoursTarget ,v_Contours ,x ,v_FGColor1 ,2);
            Imgproc.drawContours(v_MSource        ,v_Contours ,x ,v_FGColor2 ,1);
        }
        
        HighGui.imshow("轮廓识别：findContours" ,v_ContoursTarget);
        HighGui.waitKey(0);
        
        HighGui.imshow("轮廓识别：findContours" ,v_MSource);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
}
