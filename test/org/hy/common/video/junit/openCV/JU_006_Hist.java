package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：直方图
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_006_Hist
{
    private static final Logger $Logger = new Logger(JU_006_Hist.class ,true);
    
    
    
    public JU_006_Hist()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void calcHist()
    {
        // 读取图片，并按灰度图读取
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_006_Hist.jpg").getFile().substring(1) ,Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("灰度图" ,v_MSource);
        HighGui.waitKey(0);
        
        List<Mat> v_MatList = new ArrayList<Mat>();
        v_MatList.add(v_MSource);
        
        float []   v_Range     = {0 ,256};
        MatOfFloat v_HistRange = new MatOfFloat(v_Range);   // 直方图的统计范围
        Mat        v_Hist      = new Mat();                 // 生成的直方图的数据
        Imgproc.calcHist(v_MatList ,new MatOfInt((int)v_Range[0]) ,new Mat() ,v_Hist ,new MatOfInt((int)v_Range[1]) ,v_HistRange);
        
        int    v_Width     = 512;                                                     // 直方图的宽
        int    v_Hight     = 400;                                                     // 直方图的高
        Scalar v_BGColor   = new Scalar(0 ,0 ,0) ;                                    // 黑色
        Mat    v_HistImage = new Mat(v_Hight ,v_Width ,CvType.CV_8UC3 ,v_BGColor);    // 直方图的黑色背景
        Core.normalize(v_Hist ,v_Hist ,0 ,v_HistImage.rows() ,Core.NORM_MINMAX);      // 将直方图数据归一化到[0 ,400]区间
        
        float [] v_HistData = new float[(int)(v_Hist.total() * v_Hist.channels())];
        v_Hist.get(0 ,0 ,v_HistData);                                                 // 将直方图的数据转存到 float[] 中，方便后面的处理
        
        // 绘制直方图
        int v_BinWidth = (int) Math.round((double)v_Width / v_Range[1]);              // 每个直方柱的宽度
        for (int x=1; x<v_Range[1]; x++)
        {
            Point  v_PointStart = new Point(v_BinWidth * x ,v_Hight);                              // 直方柱的开始点：柱的最底部
            Point  v_PointEnd   = new Point(v_BinWidth * x ,v_Hight - Math.round(v_HistData[x]));  // 直方柱的结束点：柱的最顶部
            Scalar v_Color      = new Scalar(255 ,255 ,255);
            Imgproc.line(v_HistImage ,v_PointStart ,v_PointEnd ,v_Color ,v_BinWidth);
        }
        
        HighGui.imshow("直方图" ,v_HistImage);
        HighGui.waitKey(0);
        
        $Logger.info(v_HistImage);
    }
    
    
    
    @Test
    public void equalizeHist()
    {
        // 读取图片，并按灰度图读取
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_006_Hist.jpg").getFile().substring(1) ,Imgcodecs.IMREAD_GRAYSCALE);
        Mat v_MTarget = new Mat();
        HighGui.imshow("灰度图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.equalizeHist(v_MSource ,v_MTarget);
        HighGui.imshow("直方图均衡化后的效果" ,v_MTarget);
        HighGui.waitKey(0);
    }
}
