package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：识别颜色
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-04
 * @version     v1.0
 */
public class JU_014_Color
{
    private static final Logger $Logger = new Logger(JU_014_Color.class ,true);
    
    
    
    public JU_014_Color()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void color()
    {
        Mat v_MSource = Imgcodecs.imread(JU_014_Color.class.getResource("JU_014_Color_Colors.jpg").getFile().substring(1));
        
        Scalar v_BlueLowerColor  = new Scalar(100 ,43  ,46);
        Scalar v_BlueUpperColor  = new Scalar(124 ,255 ,255);
        
        Mat v_MHSV = new Mat();
        Mat v_Mask = new Mat();
        
        Imgproc.cvtColor(v_MSource ,v_MHSV ,Imgproc.COLOR_BGR2HSV);
        Core.inRange(v_MHSV ,v_BlueLowerColor ,v_BlueUpperColor ,v_Mask);
        
        Core.bitwise_and(v_MHSV ,v_Mask ,v_Mask);
        
        HighGui.imshow("1" ,v_MSource);
        HighGui.imshow("2" ,v_MHSV);
        HighGui.imshow("3" ,v_Mask);
        
        
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    
    
    
    @Test
    public void color_Blue()
    {
        Mat v_MSource = Imgcodecs.imread(JU_014_Color.class.getResource("JU_014_Color_Blue.jpg").getFile().substring(1));
        
        HighGui.imshow("原图" ,v_MSource);
        
//        for (int x=0; x<v_MSource.rows(); x++)
//        {
//            for (int y=0; y<v_MSource.cols(); y++)
//            {
//                $Logger.info(x + "," + y + " = " + v_MSource.get(x ,y)[0] + " ," + v_MSource.get(x ,y)[1] + " ," + v_MSource.get(x ,y)[2]);
//            }
//        }
        
        Mat v_MHSV = new Mat();
        Imgproc.cvtColor(v_MSource ,v_MHSV ,Imgproc.COLOR_BGR2HSV);
        HighGui.imshow("HSV" ,v_MHSV);
        
        for (int x=0; x<v_MHSV.rows(); x++)
        {
            for (int y=0; y<v_MHSV.cols(); y++)
            {
                $Logger.info(x + "," + y + " = " + v_MHSV.get(x ,y)[0] + " ," + v_MHSV.get(x ,y)[1] + " ," + v_MHSV.get(x ,y)[2]);
            }
        }
        
        // 因为我们读取的是彩色图，直方图均衡化需要在HSV空间做
        List<Mat> v_HSVSplit = new ArrayList<Mat>();
        Core.split(v_MHSV ,v_HSVSplit);
        Imgproc.equalizeHist(v_HSVSplit.get(2) ,v_HSVSplit.get(2));
        Core.merge(v_HSVSplit ,v_MHSV);
        
        Scalar v_BlueLowerColor  = new Scalar(100 ,43  ,46);
        Scalar v_BlueUpperColor  = new Scalar(124 ,255 ,255);
        
        Mat v_RangeRet = new Mat();
        Core.inRange(v_MHSV ,v_BlueLowerColor ,v_BlueUpperColor ,v_RangeRet);
        Scalar v_MeanRet = Core.mean(v_RangeRet);
        
        HighGui.imshow("inRange" ,v_RangeRet);
        
        $Logger.info(v_MeanRet);
        
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    
    
    
    @Test
    public void color_Green()
    {
        Mat v_MSource = Imgcodecs.imread(JU_014_Color.class.getResource("JU_014_Color_Green.jpg").getFile().substring(1));
        
        HighGui.imshow("原图" ,v_MSource);
        
        Mat v_MHSV = new Mat();
        Imgproc.cvtColor(v_MSource ,v_MHSV ,Imgproc.COLOR_BGR2HSV);
        HighGui.imshow("HSV" ,v_MHSV);
        
        // 因为我们读取的是彩色图，直方图均衡化需要在HSV空间做
        List<Mat> v_HSVSplit = new ArrayList<Mat>();
        Core.split(v_MHSV ,v_HSVSplit);
        Imgproc.equalizeHist(v_HSVSplit.get(2) ,v_HSVSplit.get(2));
        Core.merge(v_HSVSplit ,v_MHSV);
        
        Scalar v_GreenLowerColor = new Scalar(35  ,43  ,46);
        Scalar v_GreenUpperColor = new Scalar(77 ,255 ,255);
        
        Mat v_RangeRet = new Mat();
        Core.inRange(v_MHSV ,v_GreenLowerColor ,v_GreenUpperColor ,v_RangeRet);
        Scalar v_MeanRet = Core.mean(v_RangeRet);
        
        HighGui.imshow("inRange" ,v_RangeRet);
        $Logger.info(v_MeanRet);
        
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    
    
    
    @Test
    public void color_Red()
    {
        Mat v_MSource = Imgcodecs.imread(JU_014_Color.class.getResource("JU_014_Color_Red.jpg").getFile().substring(1));
        
        HighGui.imshow("原图" ,v_MSource);
        
        Mat v_MHSV = new Mat();
        Imgproc.cvtColor(v_MSource ,v_MHSV ,Imgproc.COLOR_BGR2HSV);
        HighGui.imshow("HSV" ,v_MHSV);
        
        // 因为我们读取的是彩色图，直方图均衡化需要在HSV空间做
        List<Mat> v_HSVSplit = new ArrayList<Mat>();
        Core.split(v_MHSV ,v_HSVSplit);
        Imgproc.equalizeHist(v_HSVSplit.get(2) ,v_HSVSplit.get(2));
        Core.merge(v_HSVSplit ,v_MHSV);
        
        Scalar v_RedLowerColor1 = new Scalar(0  ,43  ,46);
        Scalar v_RedUpperColor1 = new Scalar(10 ,255 ,255);
        
        Scalar v_RedLowerColor2 = new Scalar(156  ,43  ,46);
        Scalar v_RedUpperColor2 = new Scalar(180 ,255 ,255);
        
        Mat v_RangeRet = new Mat();
        Core.inRange(v_MHSV ,v_RedLowerColor1 ,v_RedUpperColor1 ,v_RangeRet);
        Scalar v_MeanRet = Core.mean(v_RangeRet);
        
        HighGui.imshow("inRange" ,v_RangeRet);
        $Logger.info(v_MeanRet);
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        
        
        v_RangeRet = new Mat();
        Core.inRange(v_MHSV ,v_RedLowerColor2 ,v_RedUpperColor2 ,v_RangeRet);
        v_MeanRet = Core.mean(v_RangeRet);
        
        HighGui.imshow("inRange" ,v_RangeRet);
        $Logger.info(v_MeanRet);
        
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    
}