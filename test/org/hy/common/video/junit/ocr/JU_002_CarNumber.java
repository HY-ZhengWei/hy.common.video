package org.hy.common.video.junit.ocr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.video.CVPoint;
import org.hy.common.video.LPRInfo;
import org.hy.common.video.OpenCV;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;





/**
 * 测试单元：车牌号的识别
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-01
 * @version     v1.0
 */
public class JU_002_CarNumber
{
    private static final Logger $Logger = new Logger(JU_002_CarNumber.class ,true);
    
    
    
    private ITesseract tesseract;
    
    /** 每次处理后的Mat历史列表 */
    private Map<String ,Mat> matHistorys;
    
    
    
    public JU_002_CarNumber() throws IOException
    {
        File v_TessDataHome = new File("C:\\WorkSpace\\hy.common.video\\lib\\OCR\\5.2.1");
        
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(v_TessDataHome.getCanonicalPath() + Help.getSysPathSeparator() + "tessdata");  // 设置Tess4J下的tessdata目录
        this.tesseract.setLanguage("chi_sim");                                                                    // 指定需要识别的语言
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void carNumbers() throws TesseractException
    {
        List<String> v_Images = new ArrayList<String>();
        
        v_Images.add(JU_002_CarNumber.class.getResource("JU_001_CarNumber_GreenA.jpg").getFile().substring(1));
        v_Images.add(JU_002_CarNumber.class.getResource("JU_001_CarNumber_GreenB.jpg").getFile().substring(1));
        v_Images.add(JU_002_CarNumber.class.getResource("JU_001_CarNumber_BlueA.jpg") .getFile().substring(1));
        v_Images.add(JU_002_CarNumber.class.getResource("JU_001_CarNumber_BlueB.jpg") .getFile().substring(1));
        
        for (String v_Image : v_Images)
        {
            LPRInfo       v_LPR    = openCV_v4(v_Image);
            BufferedImage v_BImage = toOCRv2(v_LPR);
            String        v_Text   = this.tesseract.doOCR(v_BImage);
            
            v_Text = StringHelp.replaceAll(v_Text ,new String[] {" ", "."} ,StringHelp.$ReplaceNil).toUpperCase().trim();
            
            if ( "绿牌".equals(v_LPR.getType()) )
            {
                if ( v_Text.length() >= 8 )
                {
                    v_Text = v_Text.substring(v_Text.length() - 7);
                }
            }
            else if ( "蓝牌".equals(v_LPR.getType()) )
            {
                if ( v_Text.length() >= 7 )
                {
                    v_Text = v_Text.substring(v_Text.length() - 6);
                }
            }
            
            
            $Logger.info(v_Text);
            
            
            Mat v_MSource = Imgcodecs.imread(v_Image);
            int    v_Height = 240;  // 缩放后的高度
            double v_Scale  = Help.division(v_Height ,v_MSource.height());
            int    v_Width  = (int)(v_MSource.width() * v_Scale);
            
            Mat v_Resize = new Mat();
            Imgproc.resize(v_MSource ,v_Resize ,new Size(v_Width ,v_Height));
            HighGui.imshow("缩略图" ,v_Resize);
            HighGui.waitKey(0);
        }
    }
    
    
    
    @Test
    public void carNumber() throws TesseractException
    {
        BufferedImage v_BImage = toOCRv2(openCV_v4(JU_002_CarNumber.class.getResource("JU_001_CarNumber_GreenC.jpg").getFile().substring(1)));
        
        String v_Text = this.tesseract.doOCR(v_BImage);
        v_Text = StringHelp.replaceAll(v_Text ,new String[] {" ", "."} ,StringHelp.$ReplaceNil).toUpperCase();
        $Logger.info(v_Text);
    }
    
    
    
    public BufferedImage toOCRv2(LPRInfo i_LPRInfo)
    {
        Mat v_MStart = i_LPRInfo.getCarNumberText();
        Mat v_MEnd   = i_LPRInfo.getCarNumberText();
        
        int    v_Height = 240;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MStart.height());
        int    v_Width  = (int)(v_MStart.width() * v_Scale);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-缩放原图");
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        //HighGui.imshow("OCR-缩放原图" ,v_MEnd);
        //HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        int v_PaddingHeight = v_Height / 10;
        int v_PaddingWidth  = v_MStart.width() / 40;
        v_MEnd = OpenCV.cutImage(v_MStart ,new Rect(v_PaddingWidth ,v_PaddingHeight ,v_MStart.width() - v_PaddingWidth * 2 ,v_MStart.height() - v_PaddingHeight * 2));
        //HighGui.imshow("OCR-剪裁四周" ,v_MEnd);
        //HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-双边滤波");
        Imgproc.bilateralFilter(v_MStart ,v_MEnd ,15 ,15 ,15);
        //HighGui.imshow("OCR-双边滤波" ,v_MEnd);
        //HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-膨胀");
        Imgproc.dilate(v_MStart ,v_MEnd ,new Mat() ,new Point(-1 ,-1) ,3);
        //HighGui.imshow("OCR-膨胀" ,v_MEnd);
        //HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,0 ,255 ,Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        //HighGui.imshow("OCR-二值化" ,v_MEnd);
        //HighGui.waitKey(0);
        
        return matToBufferedImage(v_MEnd ,".jpg");
    }
    
    
    
    
    public BufferedImage toOCR(LPRInfo i_LPRInfo)
    {
        Mat v_MStart = i_LPRInfo.getCarNumberImage();
        Mat v_MEnd   = i_LPRInfo.getCarNumberImage();
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-灰度图");
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        HighGui.imshow("OCR-灰度图" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-双边滤波");
        Imgproc.bilateralFilter(v_MStart ,v_MEnd ,15 ,15 ,15);
        HighGui.imshow("OCR-双边滤波" ,v_MEnd);
        HighGui.waitKey(0);
        
        
        if ( "蓝牌".equals(i_LPRInfo.getType()) )
        {
            v_MStart = v_MEnd;
            v_MEnd   = newMat("OCR-膨胀");
            Imgproc.dilate(v_MStart ,v_MEnd ,new Mat());
            HighGui.imshow("OCR-膨胀" ,v_MEnd);
            HighGui.waitKey(0);
        }
        else if ( "绿牌".equals(i_LPRInfo.getType()) )
        {
            v_MStart = v_MEnd;
            v_MEnd   = newMat("OCR-腐蚀");
            Imgproc.erode(v_MStart ,v_MEnd ,new Mat() ,new Point(-1 ,-1) ,2);
            HighGui.imshow("OCR-腐蚀" ,v_MEnd);
            HighGui.waitKey(0);
        }
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("OCR-二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,0 ,255 ,Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        HighGui.imshow("OCR-二值化" ,v_MEnd);
        HighGui.waitKey(0);
        
        return matToBufferedImage(v_MEnd ,".jpg");
    }
    
    
    
    public LPRInfo openCV_v4(String i_ImageFullName) throws TesseractException
    {
        Mat    v_MSource = Imgcodecs.imread(i_ImageFullName);
        Mat    v_MStart  = v_MSource;
        Mat    v_MEnd    = v_MSource;
        String v_Name    = "";
        int    v_Step    = 0;
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        int    v_Height = 400;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MSource.height());
        int    v_Width  = (int)(v_MSource.width() * v_Scale);
        
        /*
        v_Name   = ++v_Step + "缩放原图";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        HighGui.imshow(v_Name ,v_MEnd);
        */
        
        v_Name   = ++v_Step + "灰度";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        //HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "高斯模糊";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        // 第四个参数为零，表示不计算y方向的梯度，原因是车牌上的数字是竖方向较长，重点在于得到竖方向上的边界值
        Imgproc.GaussianBlur(v_MStart, v_MEnd, new Size(3 ,3) ,0 ,0);
        //HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "双边滤波";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.bilateralFilter(v_MStart ,v_MEnd ,15 ,15 ,15);
        //HighGui.imshow(v_Name ,v_MEnd);
        
        /*
        v_Name   = ++v_Step + "膨胀";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.dilate(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "腐蚀";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.erode(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "对比";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Core.absdiff(getMatHistory((v_Step - 1) + "腐蚀"), getMatHistory((v_Step - 2) + "膨胀"), v_MEnd);
        HighGui.imshow(v_Name ,v_MEnd);
        */
        
        // 使用Canny就不使用Sobel了
        // 对比了，也不就用Canny了
        v_Name   = ++v_Step + "边缘检测";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.Canny(v_MStart ,v_MEnd ,0 ,255);
        //HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "二值化";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.threshold(v_MStart ,v_MEnd ,127 ,255 ,Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        //HighGui.imshow(v_Name ,v_MEnd);
        
        
        
        
        Mat v_Kernel = null;
        // 形态学运算
        // 由于部分图像得到的轮廓边缘不整齐，因此再进行一次膨胀操作
        v_Name   = ++v_Step + "膨胀";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        v_Kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(v_MStart ,v_MEnd ,v_Kernel ,new Point(-1 ,-1) ,0);
        //HighGui.imshow(v_Name ,v_MEnd);
        /*
        // 先闭运算将车牌数字部分连接，再开运算将不是块状的或是较小的部分去掉
        v_Name   = ++v_Step + "闭值运算";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        v_Kernel = Mat.ones(3 ,3 ,CvType.CV_8U);          // 全1，全零运算
        Imgproc.morphologyEx(v_MStart, v_MEnd, Imgproc.MORPH_CLOSE ,new Mat() ,new Point(-1 ,-1) ,1);
        HighGui.imshow(v_Name ,v_MEnd);
        
        v_Name   = ++v_Step + "开值运算";
        v_MStart = v_MEnd;
        v_MEnd   = newMat(v_Name);
        Imgproc.morphologyEx(v_MStart, v_MEnd, Imgproc.MORPH_OPEN ,new Mat() ,new Point(-1 ,-1) ,1);
        HighGui.imshow(v_Name ,v_MEnd);
        */
        
        
        
        // 获取轮廓
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MEnd ,v_Contours ,new Mat() ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        List<CVPoint> v_MaxRectangles = OpenCV.contoursMaxRectangle(v_Contours ,1D);
        /*
        Scalar        v_Color         = new Scalar(0 ,0 ,255);
        for (CVPoint v_CVPoint : v_MaxRectangles)
        {
            Mat v_Temp = v_MSource.clone();
            Imgproc.rectangle(v_Temp ,v_CVPoint.toRect() ,v_Color ,2);
            HighGui.imshow("dddddd" ,v_Temp);
            HighGui.waitKey(0);
        }
        */
        
        
        
        // 颜色识别出车牌区域
        /*
        Help.toSort(v_MaxRectangles ,"rectArea DESC");
        Scalar v_BlueLowerColor  = new Scalar(100 ,43  ,46);
        Scalar v_BlueUpperColor  = new Scalar(124 ,255 ,255);
        Scalar v_GreenLowerColor = new Scalar(35  ,43  ,46);
        Scalar v_GreenUpperColor = new Scalar(77 ,255 ,255);
        Scalar v_Color           = new Scalar(0 ,0 ,255);
        List<CVColor> v_CVColors = new ArrayList<CVColor>();
        v_CVColors.add(new CVColor("蓝牌" ,v_BlueLowerColor  ,v_BlueUpperColor  ,44D / 14D));
        v_CVColors.add(new CVColor("绿牌" ,v_GreenLowerColor ,v_GreenUpperColor ,48D / 14D));
        
        double v_MaxMeanBlue     = 50D;   // 太小颜色的识别，将不被允许，最小颜色匹配从50开始
        double v_MaxMeanGreen    = 50D;
        Mat    v_MBlue           = null;
        Mat    v_MGreen          = null;
        int    v_MaxCount        = v_MaxRectangles.size() < 20 ? v_MaxRectangles.size() : 20;
        int    v_Count           = 0;
        for (CVPoint v_CVPoint : v_MaxRectangles)
        {
            if ( v_CVPoint.getWidth() <= v_CVPoint.getHeight() )
            {
                continue;
            }
            
            Mat v_ImgBlock = OpenCV.cutImage(v_MSource ,v_CVPoint);
            Mat v_ImgTemp  = new Mat();
            Imgproc.cvtColor(v_ImgBlock ,v_ImgTemp ,Imgproc.COLOR_BGR2HSV);
            
            for (CVColor v_CVColor : v_CVColors)
            {
                Mat v_RangeRet = new Mat();
                Core.inRange(v_ImgTemp ,v_CVColor.getLower() ,v_CVColor.getUpper() ,v_RangeRet);
                Scalar v_MeanRet = Core.mean(v_RangeRet);
                
                if ( "蓝牌".equals(v_CVColor.getName()) )
                {
                    if ( v_MeanRet.val[0] > v_MaxMeanBlue )
                    {
                        v_MaxMeanBlue = v_MeanRet.val[0];
                        v_MBlue      = v_ImgBlock;
                    }
                }
                else if ( "绿牌".equals(v_CVColor.getName()) )
                {
                    if ( v_MeanRet.val[0] > v_MaxMeanGreen )
                    {
                        v_MaxMeanGreen = v_MeanRet.val[0];
                        v_MGreen       = v_ImgBlock;
                    }
                }
            }
            
            if ( v_MaxMeanGreen > v_MaxMeanBlue )
            {
                v_MBlue = null;
            }
            else if ( v_MaxMeanBlue > v_MaxMeanGreen )
            {
                v_MGreen = null;
            }
            
            
            $Logger.info((v_CVPoint.getWidth() / v_CVPoint.getHeight()) - 44D/14D);
            $Logger.info(v_MaxMeanBlue + " , " + v_MaxMeanGreen);
            Mat v_Temp = v_MSource.clone();
            Imgproc.rectangle(v_Temp ,v_CVPoint.toRect() ,v_Color ,2);
            //HighGui.imshow("颜色识别" ,v_Temp);
            //HighGui.waitKey(0);
            
            
            if ( ++v_Count > v_MaxCount )
            {
                if ( v_MBlue != null || v_MGreen != null )
                {
                    break;
                }
            }
        }
        */
        
        List<LPRInfo> v_LPRs = OpenCV.findLPR(v_MSource ,v_MaxRectangles);
        if ( !Help.isNull(v_LPRs) )
        {
            HighGui.imshow("颜色识别：" + v_LPRs.get(0).getType() ,v_LPRs.get(0).getCarNumberImage());
            HighGui.waitKey(0);
            return v_LPRs.get(0);
        }
        
        return null;
    }
    
    
    
    public BufferedImage openCV_v3(String i_ImageFullName) throws TesseractException
    {
        Mat v_MSource = Imgcodecs.imread(i_ImageFullName);
        Mat v_MStart  = null;
        Mat v_MEnd    = null;
        
        int    v_Height = 400;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MSource.height());
        int    v_Width  = (int)(v_MSource.width() * v_Scale);
        
        v_MStart = v_MSource;
        v_MEnd   = newMat("缩放原图");
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        HighGui.imshow("缩放原图" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("灰度图");
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        HighGui.imshow("灰度图" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("双边滤波");
        Imgproc.bilateralFilter(v_MStart ,v_MEnd ,15 ,15 ,15);
        HighGui.imshow("双边滤波" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("膨胀");
        Imgproc.dilate(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow("膨胀" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("腐蚀");
        Imgproc.erode(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow("腐蚀" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("对比");
        Core.absdiff(getMatHistory("腐蚀"), getMatHistory("双边滤波"), v_MEnd);
        HighGui.imshow("对比" ,v_MEnd);
        HighGui.waitKey(0);
        
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,0 ,255 ,Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        HighGui.imshow("二值化" ,v_MEnd);
        HighGui.waitKey(0);
        
//        v_MStart = v_MEnd;
//        v_MEnd   = newMat("边缘化");
//        Imgproc.Sobel(v_MStart ,v_MEnd ,CvType.CV_8U ,1 ,0);
//        HighGui.imshow("边缘化" ,v_MEnd);
//        HighGui.waitKey(0);
//
//        v_MStart = v_MEnd;
//        v_MEnd   = newMat("高斯模糊");
//        Imgproc.GaussianBlur(v_MStart, v_MEnd, new Size(5 ,5) ,2);
//        HighGui.imshow("高斯模糊" ,v_MEnd);
//        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("膨胀2");
        Imgproc.dilate(v_MStart ,v_MEnd ,Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)));
        HighGui.imshow("膨胀2" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("梯度-轮廓线");
        Mat v_Element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3));
        Imgproc.morphologyEx(v_MStart, v_MEnd, Imgproc.MORPH_CLOSE, v_Element);
        HighGui.imshow("梯度-轮廓线" ,v_MEnd);
        HighGui.waitKey(0);
        
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("边缘检测");
        Imgproc.Canny(v_MStart ,v_MEnd ,60 ,200);
        HighGui.imshow("边缘检测" ,v_MEnd);
        HighGui.waitKey(0);
        
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MEnd ,v_Contours ,new Mat() ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        Map<Double ,MatOfPoint> v_AreaMap = new HashMap<Double ,MatOfPoint>();
        for (int x=v_Contours.size() - 1; x>=0; x--)
        {
            MatOfPoint   v_MP     = v_Contours.get(x);
            MatOfPoint2f v_MP2f   = new MatOfPoint2f();
            v_MP.convertTo(v_MP2f ,CvType.CV_32F);
            
            double       v_Area   = Imgproc.contourArea(v_MP ,true);
            double       v_Peri   = Imgproc.arcLength(v_MP2f ,true);  // 计算轮廓近似值
            MatOfPoint2f v_Approx = new MatOfPoint2f();
            
            Imgproc.approxPolyDP(v_MP2f ,v_Approx ,0.02D * v_Peri ,true);
            if ( v_Approx.rows() == 4 )
            {
                v_AreaMap.put(v_Area ,v_MP);
            }
            
//            $Logger.info(v_Approx);
//            $Logger.info(v_Approx.rows() + "," + v_Approx.cols() + "," + v_Approx.toArray().length);
//
//            List<MatOfPoint> v_MPTemp = new ArrayList<MatOfPoint>();
//            v_MPTemp.add(v_MP);
//            Mat v_MTemp = getMatHistory("缩放原图").clone();
//            Imgproc.drawContours(v_MTemp ,v_MPTemp ,-1 ,new Scalar(0 ,0 ,255) ,2);
//            HighGui.imshow("轮廓定位" ,v_MTemp);
//            HighGui.waitKey(0);
        }
        
        v_AreaMap  = Help.toReverse(v_AreaMap);
        v_Contours = Help.toList(v_AreaMap);
        
        for (int x=0; x<v_Contours.size(); x++)
        {
            MatOfPoint   v_MP   = v_Contours.get(x);
            MatOfPoint2f v_MP2f = new MatOfPoint2f();
            v_MP.convertTo(v_MP2f ,CvType.CV_32F);
            
            List<MatOfPoint> v_MPTemp = new ArrayList<MatOfPoint>();
            v_MPTemp.add(v_MP);
            v_MEnd = getMatHistory("缩放原图").clone();
            Imgproc.drawContours(v_MEnd ,v_MPTemp ,-1 ,new Scalar(0 ,0 ,255) ,2);
            HighGui.imshow("轮廓定位" ,v_MEnd);
            HighGui.waitKey(0);
            
            Point [][] v_MPMax = calcMaxOutline(v_MP.toArray());
            int  v_ImgWidth  = (int)v_MPMax[1][1].x - (int)v_MPMax[1][0].x;
            int  v_ImgHeight = (int)v_MPMax[1][3].y - (int)v_MPMax[1][0].y;
            int  v_ImgStartX = (int)v_MPMax[1][0].x;
            int  v_ImgStartY = (int)v_MPMax[1][0].y;
            Rect v_Rect      = new Rect(v_ImgStartX ,v_ImgStartY ,v_ImgWidth ,v_ImgHeight);
            Mat  v_imgMat    = new Mat(v_ImgWidth, v_ImgHeight, CvType.CV_8UC3);
            Mat  v_imgMatROI = new Mat(getMatHistory("缩放原图") ,v_Rect);
            
            v_imgMatROI.copyTo(v_imgMat);  // 从ROI中剪切图片
            
            HighGui.imshow("剪切图片" ,v_imgMat);
            HighGui.waitKey(0);
            
            
            // String v_Text = this.tesseract.doOCR(toOCR(v_imgMat));
            // $Logger.info(v_Text);
            
//            Mat v_Perspective = Imgproc.getPerspectiveTransform(new MatOfPoint2f(v_MPMax[0]) ,new MatOfPoint2f(v_MPMax[1]));  // 获取透视转换矩阵
//            Mat v_Temp        = new Mat();
//            Imgproc.warpPerspective(getMatHistory("双边滤波") ,v_Temp ,v_Perspective ,new Size(v_MPMax[1][2].x ,v_MPMax[1][2].y));
//
//            HighGui.imshow("透视转换" ,v_Temp);
//            HighGui.waitKey(0);
        }
        
        
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,0 ,255 ,Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        HighGui.imshow("二值化" ,v_MEnd);
        HighGui.waitKey(0);
        
        return matToBufferedImage(v_MEnd ,".jpg");
    }
    
    
    
    public static Point [][] calcMaxOutline(Point [] i_Points)
    {
        double v_WidthMin  = Double.MAX_VALUE;
        double v_WidthMax  = 0D;
        double v_HeightMin = Double.MAX_VALUE;
        double v_HeightMax = 0D;
        
        for (Point v_P : i_Points)
        {
            v_WidthMin  = Math.min(v_WidthMin  ,v_P.x);
            v_WidthMax  = Math.max(v_WidthMax  ,v_P.x);
            v_HeightMin = Math.min(v_HeightMin ,v_P.y);
            v_HeightMax = Math.max(v_HeightMax ,v_P.y);
        }
        
        Point    v_PLeftTop        = new Point(v_WidthMin ,v_HeightMin);
        Point    v_PRightTop       = new Point(v_WidthMax ,v_HeightMin);
        Point    v_PLeftBotton     = new Point(v_WidthMin ,v_HeightMax);
        Point    v_PRightBotton    = new Point(v_WidthMax ,v_HeightMax);
        Point [] v_RetMaxRectangle = {v_PLeftTop ,v_PRightTop ,v_PRightBotton ,v_PLeftBotton};

        // 下图是四边形的顶点编号
        // A----B
        // |    |
        // D----C
        Point v_PA = new Point(Double.MAX_VALUE ,Double.MAX_VALUE);
        Point v_PB = new Point(0D               ,Double.MAX_VALUE);
        Point v_PC = new Point(0D               ,0D);
        Point v_PD = new Point(Double.MAX_VALUE ,0D);
        for (Point v_P : i_Points)
        {
            // 找最小Y点的最小X点，即四边形的A点
            if ( v_P.y == v_HeightMin )
            {
                if ( v_P.x < v_PA.x )
                {
                    v_PA = v_P;
                }
            }
            // 找最小X点的最小Y点，即四边形的A点
            if ( v_P.x == v_WidthMin )
            {
                if ( v_P.y < v_PA.y )
                {
                    v_PA = v_P;
                }
            }
            
            // 找最小Y点的最大X点，即四边形的B点
            if ( v_P.y == v_HeightMin )
            {
                if ( v_P.x > v_PB.x )
                {
                    v_PB = v_P;
                }
            }
            // 找最大X点的最小Y点，即四边形的B点
            if ( v_P.x == v_WidthMax )
            {
                if ( v_P.y < v_PB.y )
                {
                    v_PB = v_P;
                }
            }
            
            // 找最大Y点的最大X点，即四边形的C点
            if ( v_P.y == v_HeightMax )
            {
                if ( v_P.x > v_PC.x )
                {
                    v_PC = v_P;
                }
            }
            // 找最大X点的最大Y点，即四边形的C点
            if ( v_P.x == v_WidthMax )
            {
                if ( v_P.y > v_PC.y )
                {
                    v_PC = v_P;
                }
            }
            
            // 找最大Y点的最小X点，即四边形的D点
            if ( v_P.y == v_HeightMax )
            {
                if ( v_P.x < v_PD.x )
                {
                    v_PD = v_P;
                }
            }
            // 找最小X点的最大Y点，即四边形的D点
            if ( v_P.x == v_WidthMin )
            {
                if ( v_P.y > v_PD.y )
                {
                    v_PD = v_P;
                }
            }
        }
        
        Point []   v_RetMaxOutline = {v_PA ,v_PB ,v_PC ,v_PD};
        Point [][] v_Ret           = {v_RetMaxOutline ,v_RetMaxRectangle};
        
        return v_Ret;
    }
    
    
    
    public BufferedImage openCV_v2(String i_ImageFullName)
    {
        Mat v_MSource = Imgcodecs.imread(i_ImageFullName);
        Mat v_MStart  = null;
        Mat v_MEnd    = null;
        
        int    v_Height = 400;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MSource.height());
        int    v_Width  = (int)(v_MSource.width() * v_Scale);
        
        v_MStart = v_MSource;
        v_MEnd   = newMat("缩放原图");
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("灰度图");
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        HighGui.imshow("灰度图" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("双边滤波");
        Imgproc.bilateralFilter(v_MStart ,v_MEnd ,15 ,15 ,15);
        HighGui.imshow("双边滤波" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("边缘检测");
        Imgproc.Canny(v_MStart ,v_MEnd ,30 ,200);
        HighGui.imshow("边缘检测" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("膨胀");
        Imgproc.dilate(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow("膨胀" ,v_MEnd);
        HighGui.waitKey(0);
        
        
        
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MEnd ,v_Contours ,new Mat() ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        Map<Double ,MatOfPoint> v_AreaMap = new HashMap<Double ,MatOfPoint>();
        for (int x=v_Contours.size() - 1; x>=0; x--)
        {
            MatOfPoint   v_MP     = v_Contours.get(x);
            MatOfPoint2f v_MP2f   = new MatOfPoint2f();
            v_MP.convertTo(v_MP2f ,CvType.CV_32F);
            
            double       v_Area   = Imgproc.contourArea(v_MP ,true);
            double       v_Peri   = Imgproc.arcLength(v_MP2f ,true);  // 计算轮廓近似值
            MatOfPoint2f v_Approx = new MatOfPoint2f();
            
            Imgproc.approxPolyDP(v_MP2f ,v_Approx ,0.06D * v_Peri ,true);
            if ( v_Approx.rows() == 4 )
            {
                v_AreaMap.put(v_Area ,v_MP);
            }
            
            $Logger.info(v_Approx);
            $Logger.info(v_Approx.rows() + "," + v_Approx.cols() + "," + v_Approx.toArray().length);
            
            List<MatOfPoint> v_MPTemp = new ArrayList<MatOfPoint>();
            v_MPTemp.add(v_MP);
            Mat v_MTemp = getMatHistory("缩放原图").clone();
            Imgproc.drawContours(v_MTemp ,v_MPTemp ,-1 ,new Scalar(0 ,0 ,255) ,2);
            HighGui.imshow("轮廓定位" ,v_MTemp);
            HighGui.waitKey(0);
        }
        
        v_AreaMap  = Help.toReverse(v_AreaMap);
        v_Contours = Help.toList(v_AreaMap);
        
        for (int x=0; x<v_Contours.size(); x++)
        {
            MatOfPoint       v_MP     = v_Contours.get(x);
            List<MatOfPoint> v_MPTemp = new ArrayList<MatOfPoint>();
            
            v_MPTemp.add(v_MP);
            Imgproc.drawContours(getMatHistory("缩放原图") ,v_MPTemp ,-1 ,new Scalar(0 ,0 ,255) ,1);
            HighGui.imshow("轮廓定位" ,getMatHistory("缩放原图"));
            HighGui.waitKey(0);
        }
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,125 ,255 ,Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        HighGui.imshow("二值化" ,v_MEnd);
        HighGui.waitKey(0);
        
        return matToBufferedImage(v_MEnd ,".jpg");
    }
    
    
    
    public BufferedImage openCV(String i_ImageFullName)
    {
        Mat v_MSource = Imgcodecs.imread(i_ImageFullName);
        Mat v_MStart  = null;
        Mat v_MEnd    = null;
        
        int    v_Height = 400;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MSource.height());
        int    v_Width  = (int)(v_MSource.width() * v_Scale);
        
        v_MStart = v_MSource;
        v_MEnd   = newMat("缩放原图");
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("灰度图");
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        HighGui.imshow("灰度图" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("高斯滤波");
        Imgproc.GaussianBlur(v_MStart ,v_MEnd ,new Size(1 ,1) ,0 ,0);  // 高斯滤波。卷积核5越小越清晰
        HighGui.imshow("高斯滤波" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("膨胀");
        Imgproc.dilate(v_MStart ,v_MEnd ,new Mat());
        HighGui.imshow("膨胀" ,v_MEnd);
        HighGui.waitKey(0);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("边缘检测");
        Imgproc.Canny(v_MStart ,v_MEnd ,60 ,200);
        HighGui.imshow("边缘检测" ,v_MEnd);
        HighGui.waitKey(0);
        
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MEnd ,v_Contours ,new Mat() ,Imgproc.RETR_LIST ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        
        Map<Double ,MatOfPoint> v_AreaMap = new HashMap<Double ,MatOfPoint>();
        for (int x=v_Contours.size() - 1; x>=0; x--)
        {
            MatOfPoint   v_MP     = v_Contours.get(x);
            MatOfPoint2f v_MP2f   = new MatOfPoint2f();
            v_MP.convertTo(v_MP2f ,CvType.CV_32F);
            
            double       v_Area   = Imgproc.contourArea(v_MP ,true);
            double       v_Peri   = Imgproc.arcLength(v_MP2f ,true);  // 计算轮廓近似值
            MatOfPoint2f v_Approx = new MatOfPoint2f();
            
            Imgproc.approxPolyDP(v_MP2f ,v_Approx ,0.02D * v_Peri ,true);
            if ( v_Approx.rows() == 4 )
            {
                v_AreaMap.put(v_Area ,v_MP);
            }
        }
        
        v_AreaMap  = Help.toReverse(v_AreaMap);
        v_Contours = Help.toList(v_AreaMap);
        
        for (int x=0; x<v_Contours.size(); x++)
        {
            MatOfPoint       v_MP     = v_Contours.get(x);
            List<MatOfPoint> v_MPTemp = new ArrayList<MatOfPoint>();
            
            v_MPTemp.add(v_MP);
            Imgproc.drawContours(getMatHistory("缩放原图") ,v_MPTemp ,-1 ,new Scalar(0 ,0 ,255) ,1);
            HighGui.imshow("轮廓定位" ,getMatHistory("缩放原图"));
            HighGui.waitKey(0);
        }
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("二值化");
        Imgproc.threshold(v_MStart ,v_MEnd ,125 ,255 ,Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        HighGui.imshow("二值化" ,v_MEnd);
        HighGui.waitKey(0);
        
        return matToBufferedImage(v_MEnd ,".jpg");
    }
    
    
    
    /**
     * 将OpenCV的Mat转为Tesseract能用的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-01
     * @version     v1.0
     * 
     * @param i_Mat
     * @param i_FileExtension  图片文件的格式。支持: ".jpg"  ".png"
     * @return
     */
    public static BufferedImage matToBufferedImage(Mat i_Mat ,String i_FileExtension)
    {
        MatOfByte v_MatOfByte = new MatOfByte();
        Imgcodecs.imencode(i_FileExtension ,i_Mat ,v_MatOfByte);
        
        byte []       v_Bytes = v_MatOfByte.toArray();
        BufferedImage v_Ret   = null;
        InputStream   v_In    = null;
        
        try
        {
            v_In = new ByteArrayInputStream(v_Bytes);
            v_Ret = ImageIO.read(v_In);
        }
        catch (IOException e)
        {
            $Logger.error(e);
        }
        finally
        {
            try
            {
                if ( v_In != null )
                {
                    v_In.close();
                }
                v_In = null;
            }
            catch (Exception exce)
            {
                // Nothing.
            }
        }
        
        return v_Ret;
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
    
}
