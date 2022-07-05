package org.hy.common.video.junit.openCV;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;





/**
 * 测试单元：OpenCV与视频的融合
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-05
 * @version     v1.0
 */
public class JU_102_Video
{
    private static final Logger $Logger = new Logger(JU_102_Video.class ,true);
    
    
    /** 每次处理后的Mat历史列表 */
    private Map<String ,Mat> matHistorys;
    
    
    
    public JU_102_Video()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    public synchronized Mat newMat(String i_MatName)
    {
        Mat v_New = new Mat();
        
        if ( this.matHistorys == null )
        {
            this.matHistorys = new LinkedHashMap<String ,Mat>();
        }
        
        this.matHistorys.put(i_MatName ,v_New);
        return v_New;
    }
    
    
    
    public Mat getMatHistory(String i_MatName)
    {
        return this.matHistorys.get(i_MatName);
    }
    
    
    
    @Test
    public void video()
    {
        VideoCapture v_VideoCapture = new VideoCapture();
        
        v_VideoCapture.open(JU_101_Document.class.getResource("JU_102_Video.ts").getFile().substring(1));
        
        Mat     v_VideoImg = new Mat();
        int     v_Index    = 0;
        boolean v_IsRead   = v_VideoCapture.read(v_VideoImg);
        while ( v_VideoCapture.isOpened() && v_IsRead )
        {
            v_VideoImg = ai(v_VideoImg);
            HighGui.imshow("视频视频" + ++v_Index ,v_VideoImg);
            int v_Key = HighGui.waitKey(1);
            if ( v_Key == 27 )
            {
                break;
            }
            HighGui.destroyAllWindows();
            
            v_IsRead = v_VideoCapture.read(v_VideoImg);     // 视频文件的视频写入MatVideo中
        }
        
        v_VideoCapture.release();  // 释放内存
        
    }
    
    
    public Mat aiv2(Mat i_MSource)
    {
        /*
         * 初始化特征提取器
         * @param _winSize 特征提取检测的窗口大小
         * @param _blockSize 块大小，。
         * @param _blockStride 检测步长。
         * @param _cellSize 胞元，胞元是在块中。
         * @param _nbins 检测方向 在一个胞元内统计9个方向的梯度直方图，每个方向为180/9=20度。
         * 详细内容可参考：https://blog.csdn.net/qq_26898461/article/details/46786285
         */
        HOGDescriptor hog = new HOGDescriptor(new Size(64,128), new Size(16,16), new Size(8,8), new Size(8,8), 9);
        MatOfFloat descriptors = new MatOfFloat();
        /*
         * 计算给定图像的HOG描述符。
         * @param img类型CV_8U的矩阵，其中包含要计算HOG特征的图像。
         * @param描述符类型为CV_32F的矩阵
         * @param winStride窗口跨度。 它必须是跨步的倍数。
         * @param padding填充
         */
        hog.compute(i_MSource, descriptors ,new Size(0,0) ,new Size(0,0));
        
        
        System.out.println(descriptors.size());
        return descriptors;
    }
    
    
    
    /**
     * 识别眼睛
     * 
     * @param i_MSource
     * @return
     */
    public Mat ai(Mat i_MSource)
    {
        Mat               v_MTarget        = i_MSource.clone();
        CascadeClassifier v_Detector       = new CascadeClassifier("D:\\OpenCV\\sources\\data\\haarcascades\\haarcascade_fullbody.xml");
        MatOfRect         v_DetectorResult = new MatOfRect();        // 检测结果，矩形集
        
        v_Detector.detectMultiScale(v_MTarget ,v_DetectorResult);    // 检测眼睛
        
        if ( v_DetectorResult.toArray().length <= 0 )
        {
            return v_MTarget;
        }
        
        
        /*
        Size    v_WinSize           = new Size(1280 ,720);           // 图像大小
        Size    v_BlockSize         = new Size(16 ,16);              // 每一个Cell是 8*8 的，四个Cell组成一个Block，所以Block是16*16
        Size    v_BlockStride       = new Size(8 ,8);                // 移动一个Cell是多少，永远与Cell是同步
        Size    v_CellSize          = new Size(8 ,8);
        int     v_NBins             = 0;
        int     v_DerivAperture     = 1;
        double  v_WinSigma          = -1D;
        int     v_HistogramNormType = HOGDescriptor.L2Hys;
        double  v_L2HysThreshold    = 0.2D;
        boolean v_GammaCorrection   = false;                         // 是否使用伽马校正，false会快一些
        int     v_NLevels           = HOGDescriptor.DEFAULT_NLEVELS;
        boolean v_SignedGradient    = false;                         // 梯度
        
        HOGDescriptor v_Hog = new HOGDescriptor(v_WinSize ,v_BlockSize ,v_BlockStride ,v_CellSize ,v_NBins);
        try
        {
            MatOfFloat v_DefaultPeople = HOGDescriptor.getDefaultPeopleDetector();
            if ( v_Hog.checkDetectorSize() )
            {
                v_Hog.setSVMDetector(v_DefaultPeople.clone());
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        
        MatOfRect   v_FoundLocations = new MatOfRect();
        MatOfDouble v_FoundWeights   = new MatOfDouble();
        
        v_Hog.detectMultiScale(i_MSource ,v_FoundLocations ,v_FoundWeights);
        */
        
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
    
}