package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.video.CVPoint;
import org.hy.common.video.OpenCV;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;





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
        
        v_VideoCapture.open(JU_102_Video.class.getResource("JU_102_Video.ts").getFile().substring(1));
        
        List<Mat>     v_VideoDatas = new ArrayList<Mat>();
        Mat           v_VideoImg = new Mat();
        int           v_Index    = 0;
        boolean       v_IsRead   = v_VideoCapture.read(v_VideoImg);
        Scalar        v_Color    = new Scalar(0 ,0 ,255);
        List<CVPoint> v_CVPoints = null;
        while ( v_VideoCapture.isOpened() && v_IsRead )
        {
            if ( v_Index++ % 4 == 0 )
            {
                v_CVPoints = aiv3(v_VideoImg);
            }
            drawRect(v_VideoImg ,v_CVPoints ,v_Color ,2);
            v_VideoDatas.add(v_VideoImg);
            v_VideoImg = new Mat();
//            HighGui.imshow("视频视频" + ++v_Index ,v_VideoImg);
//            int v_Key = HighGui.waitKey(1);
//            if ( v_Key == 27 )
//            {
//                break;
//            }
//            HighGui.destroyAllWindows();
            
            v_IsRead = v_VideoCapture.read(v_VideoImg);     // 视频文件的视频写入MatVideo中
        }
        
        v_VideoCapture.release();  // 释放内存
        
        
        /*
        cv_FOURCC(‘D’,‘I’,‘V’,‘X’)  MPEG-4编码
        CV_FOURCC(‘P’,‘I’,‘M’,‘1’)  MPEG-1编码
        CV_FOURCC(‘M’,‘J’,‘P’,‘G’)  JPEG编码（运行效果一般）
        CV_FOURCC(‘M’, ‘P’, ‘4’, ‘2’)   MPEG-4.2编码
        CV_FOURCC(‘D’, ‘I’, ‘V’, ‘3’)   MPEG-4.3编码
        CV_FOURCC(‘U’, ‘2’, ‘6’, ‘3’)   H263编码
        CV_FOURCC(‘I’, ‘2’, ‘6’, ‘3’)   H263I编码
        CV_FOURCC(‘F’, ‘L’, ‘V’, ‘1’)   FLV1编码
        */
        
        VideoWriter v_VideoWrite = new VideoWriter();
        Size v_VidoeSize   = new Size();
        v_VidoeSize.width  = 1280;                                   // v_VideoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        v_VidoeSize.height = 720;                                    // v_VideoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int    v_Fourcc    = VideoWriter.fourcc('M', 'P', '4', 'V'); // v_VideoCapture.get(Videoio.CAP_PROP_FOURCC);
        double v_Fps       = 25;                                     // v_VideoCapture.get(Videoio.CAP_PROP_FPS);
        v_VideoWrite.open("C:\\Users\\hyzhe\\Desktop\\LiKu.ts" ,v_Fourcc ,v_Fps ,v_VidoeSize);
        for (Mat v_Data : v_VideoDatas)
        {
            v_VideoWrite.write(v_Data);
        }
        v_VideoWrite.release();
    }
    
    
    
    public List<CVPoint> aiColor(Mat i_MSource ,Scalar i_LowerColor ,Scalar i_UpperColor)
    {
        Mat v_MTarget = new Mat();
        
        double v_Scale = OpenCV.resizeHeight(i_MSource ,v_MTarget ,320);
        
//        HighGui.imshow("缩小" ,v_MTarget);
//        HighGui.waitKey(0);
        
        // Imgproc.bilateralFilter(v_MTarget ,v_MTarget ,7 ,7 ,7);
        Imgproc.cvtColor(v_MTarget ,v_MTarget ,Imgproc.COLOR_BGR2HSV);   // 注意：BGR，不是RGB
        Core.inRange(v_MTarget ,i_LowerColor ,i_UpperColor ,v_MTarget);
        
//        HighGui.imshow("原图" ,i_MSource);
//        HighGui.imshow("轮廓" ,v_MTarget);
//        HighGui.waitKey(0);
//        HighGui.destroyAllWindows();
        
        Imgproc.Canny(v_MTarget ,v_MTarget ,0 ,255);                                      // 边缘检测
        Imgproc.threshold(v_MTarget ,v_MTarget ,127 ,255 ,Imgproc.THRESH_BINARY);         // 二值化：大于125的值变成255，否则变成0
        Mat v_Kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        // Imgproc.dilate(v_MTarget ,v_MTarget ,v_Kernel ,new Point(-1 ,-1) ,2);
        // Imgproc.erode(v_MTarget ,v_MTarget ,v_Kernel ,new Point(-1 ,-1) ,2);
        Imgproc.morphologyEx(v_MTarget, v_MTarget, Imgproc.MORPH_CLOSE ,new Mat() ,new Point(-1 ,-1) ,1);
        
        // 获取轮廓
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MTarget ,v_Contours ,new Mat() ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        List<CVPoint> v_MaxRectangles = OpenCV.contoursMaxRectangle(v_Contours ,v_Scale);
        
        
        
        // Help.toSort(v_MaxRectangles ,"rectArea DESC");
        List<CVPoint> v_OKRectangles = new ArrayList<CVPoint>();
        for (CVPoint v_CVPoint : v_MaxRectangles)
        {
            // Imgproc.rectangle(i_MSource ,v_CVPoint.toRect() ,v_Color ,2);
//          HighGui.imshow("1" ,i_MSource);
//          HighGui.imshow("2" ,v_MTarget);
//          HighGui.waitKey(0);
//          HighGui.destroyAllWindows();
            
            try
            {
                if ( v_CVPoint.getWidth()  >  v_CVPoint.getHeight()
                  || v_CVPoint.getWidth()  <= 35
                  || v_CVPoint.getHeight() <= 35
                  || v_CVPoint.getWidth()  >  200
                  || v_CVPoint.getHeight() >  240 )
                {
                    // 通过大小初步排除
                    continue;
                }
            }
            catch (Exception exce)
            {
                continue;
            }
            
            double v_SizeValue = v_CVPoint.getWidth() / v_CVPoint.getHeight();
            if ( 0.4D > v_SizeValue || v_SizeValue > 0.9D )
            {
                // 通过宽高比，再与标准车牌宽高比的差排除
                continue;
            }
            
            v_OKRectangles.add(v_CVPoint);
        }
        
        Help.toSort(v_OKRectangles ,"rectArea DESC");
        int v_FaultTolerant = 10;
        for (int x=v_OKRectangles.size()-1; x>=1; x--)
        {
            CVPoint v_PX = v_OKRectangles.get(x);
            
            for (int y=0; y<x && y<v_OKRectangles.size(); y++)
            {
                CVPoint v_PY    = v_OKRectangles.get(y);
                boolean v_IsDel = false;
                
                // X在Y的右下角，并相互有交叉区域
                if ( v_PY.getXMin() - v_FaultTolerant < v_PX.getXMin() && v_PX.getXMin() < v_PY.getXMax() + v_FaultTolerant
                  && v_PY.getYMin() - v_FaultTolerant < v_PX.getYMin() && v_PX.getYMin() < v_PY.getYMax() + v_FaultTolerant )
                {
                    v_IsDel = true;
                }
                // X在Y的左下角，并相互有交叉区域
                else if ( v_PY.getXMin() - v_FaultTolerant < v_PX.getXMax() && v_PX.getXMax() < v_PY.getXMax() + v_FaultTolerant
                       && v_PY.getYMin() - v_FaultTolerant < v_PX.getYMin() && v_PX.getYMin() < v_PY.getYMax() + v_FaultTolerant )
                {
                    v_IsDel = true;
                }
                // X在Y的右上角，并相互有交叉区域
                else if ( v_PY.getXMin() - v_FaultTolerant < v_PX.getXMin() && v_PX.getXMin() < v_PY.getXMax() + v_FaultTolerant
                       && v_PY.getYMin() - v_FaultTolerant < v_PX.getYMax() && v_PX.getYMax() < v_PY.getYMax() + v_FaultTolerant )
                {
                    v_IsDel = true;
                }
                // X在Y的左上角，并相互有交叉区域
                else if ( v_PY.getXMin() - v_FaultTolerant < v_PX.getXMax() && v_PX.getXMax() < v_PY.getXMax() + v_FaultTolerant
                       && v_PY.getYMin() - v_FaultTolerant < v_PX.getYMax() && v_PX.getYMax() < v_PY.getYMax() + v_FaultTolerant )
                {
                    v_IsDel = true;
                }
                
                if ( v_PY.getXMin() - v_FaultTolerant < v_PX.getXMin() && v_PX.getXMax() < v_PY.getXMax() + v_FaultTolerant )
                {
                    if ( v_PY.getYMin() - v_FaultTolerant < v_PX.getYMin() && v_PX.getYMax() < v_PY.getYMax() + v_FaultTolerant )
                    {
                        v_IsDel = true;
                    }
                }
                
                if ( v_IsDel )
                {
                    // 删除包含关系的、交叉关系的
                    v_OKRectangles.remove(x);
                    
                    v_PY.setXMin(v_PY.getXMin());
                    v_PY.setXMax(v_PY.getXMax());
                    v_PY.setYMin(v_PY.getYMin());
                    v_PY.setYMax(v_PY.getYMax());
                    break;
                }
            }
        }
        
//        if ( v_OKRectangles.size() >= 3 )
//        {
//            HighGui.imshow("原图" ,i_MSource);
//            HighGui.imshow("1.3" ,v_MTarget);
//            HighGui.waitKey(0);
//            HighGui.destroyAllWindows();
//        }
        
        return v_OKRectangles;
    }
    
    
    
    public List<CVPoint> aiBody(Mat i_MSource ,Scalar i_LowerColor ,Scalar i_UpperColor)
    {
        Mat               v_MTarget        = i_MSource.clone();
        CascadeClassifier v_Detector       = new CascadeClassifier("D:\\OpenCV\\sources\\data\\haarcascades_cuda\\haarcascade_eye.xml");
        MatOfRect         v_DetectorResult = new MatOfRect();        // 检测结果，矩形集
        
        v_Detector.detectMultiScale(v_MTarget ,v_DetectorResult);
        
        List<CVPoint> v_Ret = new ArrayList<CVPoint>();
        
        if ( v_DetectorResult.toArray().length <= 0 )
        {
            return v_Ret;
        }
        
        for (Rect v_Rect : v_DetectorResult.toArray())
        {
            CVPoint v_CVPoint = new CVPoint();
            
            v_CVPoint.setXMin((double)v_Rect.x);
            v_CVPoint.setXMax((double)v_Rect.x + v_Rect.width);
            v_CVPoint.setYMin((double)v_Rect.y);
            v_CVPoint.setYMax((double)v_Rect.y + v_Rect.height);
            
//            Scalar v_Color = new Scalar(0 ,0 ,255);
//            Imgproc.rectangle(i_MSource ,v_CVPoint.toRect() ,v_Color ,1);
//            HighGui.imshow("1.3" ,i_MSource);
//            HighGui.waitKey(0);
            
            try
            {
                if ( v_CVPoint.getWidth()  >  v_CVPoint.getHeight()
                  || v_CVPoint.getWidth()  <= 30
                  || v_CVPoint.getHeight() <= 30
                  || v_CVPoint.getWidth()  >  100
                  || v_CVPoint.getHeight() >  120 )
                {
                    // 通过大小初步排除
                    //continue;
                }
            }
            catch (Exception exce)
            {
                continue;
            }
            
            double v_SizeValue = v_CVPoint.getWidth() / v_CVPoint.getHeight();
            if ( v_SizeValue < 0.4D )
            {
                // 通过宽高比，再与标准车牌宽高比的差排除
                //continue;
            }
            
            v_Ret.add(v_CVPoint);
        }
        
        return v_Ret;
    }
    
    
    public void drawRect(Mat i_MSource ,List<CVPoint> i_CVPoints ,Scalar i_Color ,int i_LineSize)
    {
        for (CVPoint v_CVP : i_CVPoints)
        {
            CVPoint v_NewCVP = v_CVP.clone();
            v_NewCVP.setYMin(v_NewCVP.getYMin() >= 30                   ? v_NewCVP.getYMin() - 30 : 0);
            v_NewCVP.setYMax(v_NewCVP.getYMax() < i_MSource.cols() + 20 ? v_NewCVP.getYMax() + 20 : i_MSource.cols());
            
            v_NewCVP.setXMin(v_NewCVP.getXMin() >= 15                   ? v_NewCVP.getXMin() - 15 : 0);
            v_NewCVP.setXMax(v_NewCVP.getXMax() < i_MSource.rows() + 15 ? v_NewCVP.getXMax() + 15 : i_MSource.rows());
            
            Imgproc.rectangle(i_MSource ,v_NewCVP.toRect() ,i_Color ,i_LineSize);
        }
        
//        HighGui.imshow("原图" ,i_MSource);
//        HighGui.imshow("膨胀" ,v_MTarget);
//        HighGui.waitKey(0);
//        HighGui.destroyAllWindows();
    }
    
    
    
    public List<CVPoint> aiv3(Mat i_MSource)
    {
        List<CVPoint> v_Ret = new ArrayList<CVPoint>();
        
        Scalar v_BlueLowerColor = new Scalar(110 ,75  ,75);
        Scalar v_BlueUpperColor = new Scalar(120 ,255 ,255);
        Scalar v_BGLowerColor   = new Scalar(0   ,0   ,50);
        Scalar v_BGUpperColor   = new Scalar(180 ,255 ,70);
        
        v_Ret.addAll(aiColor(i_MSource ,v_BlueLowerColor ,v_BlueUpperColor));
        //v_Ret.addAll(aiColor(i_MSource ,v_BGLowerColor   ,v_BGUpperColor));
        //v_Ret.addAll(aiBody(i_MSource  ,v_BGLowerColor   ,v_BGUpperColor));
        
        return v_Ret;
               
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
        int     v_NBins             = 9;
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