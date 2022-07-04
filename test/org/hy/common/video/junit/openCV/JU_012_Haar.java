package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;





/**
 * 测试单元：人脸检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-30
 * @version     v1.0
 */
public class JU_012_Haar
{
    private static final Logger $Logger = new Logger(JU_012_Haar.class ,true);
    
    
    
    public JU_012_Haar()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void haar()
    {
        Mat v_MSource = Imgcodecs.imread(JU_012_Haar.class.getResource("JU_012_Haar.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        HighGui.imshow("识别眼睛" ,haarEyes(v_MSource));
        HighGui.waitKey(0);
        
        HighGui.imshow("识别人体" ,haarFullbody(v_MSource));
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    /**
     * 识别眼睛
     * 
     * @param i_MSource
     * @return
     */
    private Mat haarEyes(Mat i_MSource)
    {
        Mat               v_MTarget        = i_MSource.clone();
        CascadeClassifier v_Detector       = new CascadeClassifier("D:\\OpenCV\\sources\\data\\haarcascades\\haarcascade_eye.xml");
        MatOfRect         v_DetectorResult = new MatOfRect();        // 检测结果，矩形集
        
        v_Detector.detectMultiScale(v_MTarget ,v_DetectorResult);    // 检测眼睛
        
        if ( v_DetectorResult.toArray().length <= 0 )
        {
            return v_MTarget;
        }
        
        // 绘制检测结果
        Scalar v_Color = new Scalar(0 ,0 ,255);                     // 边框颜色
        for (Rect v_Rect : v_DetectorResult.toArray())
        {
            Point v_PStart = new Point(v_Rect.x ,v_Rect.y);
            Point v_PEnd   = new Point(v_Rect.x + v_Rect.width ,v_Rect.y + v_Rect.height);
            Imgproc.rectangle(v_MTarget ,v_PStart ,v_PEnd ,v_Color ,2);
        }
        
        return v_MTarget;
    }
    
    
    
    /**
     * 识别人体
     * 
     * @param i_MSource
     * @return
     */
    private Mat haarFullbody(Mat i_MSource)
    {
        Mat               v_MTarget        = i_MSource.clone();
        CascadeClassifier v_Detector       = new CascadeClassifier("D:\\OpenCV\\sources\\data\\haarcascades\\haarcascade_profileface.xml");
        MatOfRect         v_DetectorResult = new MatOfRect();        // 检测结果，矩形集
        
        v_Detector.detectMultiScale(v_MTarget ,v_DetectorResult);    // 检测眼睛
        
        if ( v_DetectorResult.toArray().length <= 0 )
        {
            return v_MTarget;
        }
        
        // 绘制检测结果
        Scalar v_Color = new Scalar(255 ,0 ,0);                     // 边框颜色
        for (Rect v_Rect : v_DetectorResult.toArray())
        {
            Point v_PStart = new Point(v_Rect.x ,v_Rect.y);
            Point v_PEnd   = new Point(v_Rect.x + v_Rect.width ,v_Rect.y + v_Rect.height);
            Imgproc.rectangle(v_MTarget ,v_PStart ,v_PEnd ,v_Color ,2);
        }
        
        return v_MTarget;
    }
    
}