package org.hy.common.video;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpenCV
{
 
    private static List<CVColor> $LPRCVColors;
    
    
    private static Scalar $BlackLowerColor;
    
    private static Scalar $BlackUpperColor;
    
    private static Scalar $WhiteLowerColor;
    
    private static Scalar $WhiteUpperColor;
    
    
    
    static
    {
        $BlackLowerColor = new Scalar(0    ,0    ,0);
        $BlackUpperColor = new Scalar(180  ,255  ,46);
        
        $WhiteLowerColor = new Scalar(0    ,0    ,46);
        $WhiteUpperColor = new Scalar(180  ,43   ,255);
        
        $LPRCVColors = new ArrayList<CVColor>();
        
        Scalar v_YellowLowerColor = new Scalar(26  ,43  ,46);
        Scalar v_YellowUpperColor = new Scalar(34  ,255 ,255);
        
        Scalar v_BlueLowerColor   = new Scalar(100 ,43  ,46);
        Scalar v_BlueUpperColor   = new Scalar(124 ,255 ,255);
        
        Scalar v_GreenLowerColor  = new Scalar(35  ,43  ,46);
        Scalar v_GreenUpperColor  = new Scalar(77  ,255 ,255);
        
        $LPRCVColors.add(new CVColor("黄牌" ,v_YellowLowerColor ,v_YellowUpperColor ,44D / 14D ,7));
        $LPRCVColors.add(new CVColor("蓝牌" ,v_BlueLowerColor   ,v_BlueUpperColor   ,44D / 14D ,7));
        $LPRCVColors.add(new CVColor("绿牌" ,v_GreenLowerColor  ,v_GreenUpperColor  ,48D / 14D ,9));
    }
    
    
    
    public static double resizeHeight(Mat i_MSource ,Mat io_MTarget ,int i_Height)
    {
        double v_Scale  = Help.division(i_Height ,i_MSource.height());
        int    v_Width  = (int)(i_MSource.width() * v_Scale);
        
        Imgproc.resize(i_MSource ,io_MTarget ,new Size(v_Width ,i_Height));
        
        return v_Scale;
    }
    
    
    
    public static double resizeWidth(Mat i_MSource ,Mat i_MTarget ,int i_Width)
    {
        double v_Scale  = Help.division(i_Width ,i_MSource.width());
        int    v_Height = (int)(i_MSource.height() * v_Scale);
        
        Imgproc.resize(i_MSource ,i_MTarget ,new Size(i_Width ,v_Height));
        
        return v_Scale;
    }
    
    
    
    public static List<LPRInfo> findLPR(Mat i_MSource ,List<CVPoint> io_Rectangles)
    {
        double        v_MinMean  = 90D;                                  // 太小颜色的识别，将不被允许，最小颜色匹配从90开始
        List<LPRInfo> v_LPRs     = new ArrayList<LPRInfo>();
        int           v_MaxCount = io_Rectangles.size() < 16 ? io_Rectangles.size() : 16;
        int           v_Count    = 0;
        Mat           v_MSource  = new Mat();
        
        Help.toSort(io_Rectangles ,"rectArea DESC");
        Imgproc.cvtColor(i_MSource ,v_MSource ,Imgproc.COLOR_BGR2HSV);   // 注意：BGR，不是RGB
        
        for (CVPoint v_CVPoint : io_Rectangles)
        {
            if ( v_CVPoint.getWidth()  <= v_CVPoint.getHeight()
              || v_CVPoint.getWidth()  <= 1
              || v_CVPoint.getHeight() <= 1 )
            {
                // 通过大小初步排除
                continue;
            }
            
            Mat v_ImgBlock = OpenCV.cutImage(v_MSource ,v_CVPoint);
            
            //HighGui.imshow("颜色识别" ,v_ImgBlock);
            //HighGui.waitKey(0);
            
            for (CVColor v_CVColor : $LPRCVColors)
            {
                double v_SizeValue = Math.abs(v_CVPoint.getWidth() / v_CVPoint.getHeight() - v_CVColor.getWithdHeightScale());
                if ( v_SizeValue > 1.0D )
                {
                    // 通过宽高比，再与标准车牌宽高比的差排除
                    continue;
                }
                
                // 整体颜色识别
                Scalar v_MeanRet = findColor(v_ImgBlock ,v_CVColor);
                if ( v_MeanRet.val[0] > v_MinMean )
                {
                    Mat    v_ImgRight = OpenCV.cutImage(v_MSource ,v_CVPoint.cutRight(1.0D / v_CVColor.getCount()));
                    Scalar v_MeanRetR = findColor(v_ImgRight ,v_CVColor);
                    if ( v_MeanRetR.val[0] < v_MinMean )
                    {
                        // 右侧颜色识别
                        break;
                    }
                    
                    Mat    v_ImgLeft  = OpenCV.cutImage(v_MSource ,v_CVPoint.cutLeft(1.0D / v_CVColor.getCount()));
                    Scalar v_MeanRetL = findColor(v_ImgLeft ,v_CVColor);
                    if ( v_MeanRetL.val[0] < v_MinMean )
                    {
                        // 左侧颜色识别
                        break;
                    }
                    
                    Mat    v_MText    = new Mat();
                    Scalar v_MeanText = null;
                    if ( "蓝牌".equals(v_CVColor.getName()) )
                    {
                        Core.inRange(v_ImgBlock ,$WhiteLowerColor ,$WhiteUpperColor ,v_MText);
                    }
                    else if ( "绿牌".equals(v_CVColor.getName()) )
                    {
                        Core.inRange(v_ImgBlock ,$BlackLowerColor ,$BlackUpperColor ,v_MText);
                    }
                    v_MeanText = Core.mean(v_MText);
                    if ( v_MeanText.val[0] < 35D )
                    {
                        // 文字颜色识别
                        break;
                    }
                    
                    // 通过颜色识别
                    v_LPRs.add(new LPRInfo(v_CVColor.getName()
                                          ,OpenCV.cutImage(i_MSource ,v_CVPoint)
                                          ,v_MText
                                          ,v_CVPoint.getRectArea()
                                          ,v_MeanRet.val[0]
                                          ,v_SizeValue));
                }
            }
            
            if ( ++v_Count > v_MaxCount )
            {
                if ( !Help.isNull(v_LPRs) )
                {
                    break;
                }
            }
        }
        
        return v_LPRs;
    }
    
    
    
    public static Scalar findColor(Mat i_Image ,CVColor i_CVColor)
    {
        Mat v_RangeRet = new Mat();
        Core.inRange(i_Image ,i_CVColor.getLower() ,i_CVColor.getUpper() ,v_RangeRet);
        return Core.mean(v_RangeRet);
    }
    
    
    
    
    /**
     * 计算轮廓最大的四边形
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-03
     * @version     v1.0
     * 
     * @param i_Contours
     * @return
     */
    public static List<CVPoint> contoursMaxRectangle(List<MatOfPoint> i_Contours ,double i_Scale)
    {
        List<CVPoint> v_Ret = new ArrayList<CVPoint>();
        
        for (MatOfPoint v_MP : i_Contours)
        {
            Point[] v_PArr = v_MP.toArray();
            CVPoint v_CVP  = new CVPoint();
            
            for (Point v_P : v_PArr)
            {
                v_CVP.setMaxMin(v_P ,i_Scale);
            }
            
            v_Ret.add(v_CVP);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 从大图中剪裁出指定尺寸的小图
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-03
     * @version     v1.0
     * 
     * @param i_MSource
     * @param i_CVPoint
     * @return
     */
    public static Mat cutImage(Mat i_MSource ,CVPoint i_CVPoint)
    {
        return cutImage(i_MSource ,i_CVPoint.toRect());
    }
    
    
    
    /**
     * 从大图中剪裁出指定尺寸的小图
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-03
     * @version     v1.0
     * 
     * @param i_MSource
     * @param i_Rect
     * @return
     */
    public static Mat cutImage(Mat i_MSource ,Rect i_Rect)
    {
        Mat v_ImgMat    = new Mat(i_Rect.width, i_Rect.height, CvType.CV_8UC3);
        Mat v_ImgMatROI = new Mat(i_MSource ,i_Rect);
        
        v_ImgMatROI.copyTo(v_ImgMat);  // 从ROI中剪切图片
        
        return v_ImgMat;
    }
    
}
