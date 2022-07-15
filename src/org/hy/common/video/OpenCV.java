package org.hy.common.video;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.opencv.opencv_core.MatVector;
import org.hy.common.Help;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;

public class OpenCV
{
 
    private static List<CVColor> $LPRCVColors;
    
    
    private static       Scalar $BlackLowerColor;
                         
    private static       Scalar $BlackUpperColor;
                         
    private static       Scalar $WhiteLowerColor;
                         
    private static       Scalar $WhiteUpperColor;
    
    /** 红色 */
    public  static final Scalar $Color_Red;
    
    /** 绿色 */
    public  static final Scalar $Color_Green;
    
    
    
    static
    {
        $Color_Red   = new Scalar(0 ,0   ,255);
        
        $Color_Green = new Scalar(0 ,255 ,0);
        
        
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
     * 计算点落在方格内的数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Contours
     * @return
     */
    public static int [][] contoursCounter(List<MatOfPoint> i_Contours ,int i_BlockWCount ,int i_BlockHCount ,int i_BlockSize)
    {
        int [][] v_Counter = new int[i_BlockHCount][i_BlockWCount];
        
        for (MatOfPoint v_MP : i_Contours)
        {
            Point[] v_PArr = v_MP.toArray();
            
            for (Point v_P : v_PArr)
            {
                int v_WIndex = (int)v_P.x / i_BlockSize;
                int v_HIndex = (int)v_P.y / i_BlockSize;
                
                v_Counter[v_HIndex][v_WIndex]++;
            }
        }
        
        return v_Counter;
    }
    
    
    
    /**
     * 计算点落在方格内的数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Contours
     * @return
     */
    public static int [][] contoursCounter(MatVector i_Contours ,int i_BlockWCount ,int i_BlockHCount ,int i_BlockSize)
    {
        int [][] v_Counter = new int[i_BlockHCount][i_BlockWCount];
        
        for (long x=0L; x<i_Contours.size(); x++)
        {
            org.bytedeco.opencv.opencv_core.Mat v_MP = i_Contours.get(x);
            
        }
        
        return v_Counter;
    }
    
    
    
    /**
     * 计算点落在方格内的数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-11
     * @version     v1.0
     * 
     * @param i_Contours
     * @return
     */
    public static void contoursCounter(List<MatOfPoint> i_Contours ,int i_BlockSize ,int [][] io_Counter)
    {
        for (MatOfPoint v_MP : i_Contours)
        {
            Point[] v_PArr = v_MP.toArray();
            
            for (Point v_P : v_PArr)
            {
                int v_WIndex = (int)v_P.x / i_BlockSize;
                int v_HIndex = (int)v_P.y / i_BlockSize;
                
                io_Counter[v_HIndex][v_WIndex]++;
            }
        }
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
        Mat v_ImgMat    = new Mat(i_Rect.width, i_Rect.height, i_MSource.type());
        Mat v_ImgMatROI = new Mat(i_MSource ,i_Rect);
        
        v_ImgMatROI.copyTo(v_ImgMat);  // 从ROI中剪切图片
        v_ImgMatROI.release();
        v_ImgMatROI = null;
        
        return v_ImgMat;
    }
    
    
    
    /**
     * 训练数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-03
     * @version     v1.0
     * 
     * @param i_TrainDatas   训练数据
     * @param i_TrainLables  训练标签
     * @return
     */
    public static SVM svm(Mat i_TrainDatas ,Mat i_TrainLables)
    {
        SVM svm = SVM.create();
        svm.setC(1);
        svm.setP(0);
        svm.setNu(0);
        svm.setCoef0(0);
        svm.setGamma(1);
        svm.setDegree(0);
        
        /***
         * 核类型
         * CvSVM::LINEAR - 没有任何向映像至高维空间，线性区分（或回归）在原始特征空间中被完成，这是最快的选择。 d(x,y) = x•y == (x,y)
         * CvSVM::POLY - 多项式核: d(x,y)= (gamma*(x•y)+coef0)degree
         * CvSVM::RBF - 径向基，对于大多数情况都是一个较好的选择：d(x,y)= exp(-gamma*|x-y|2)
         * CvSVM::SIGMOID - sigmoid函数被用作核函数:d(x,y) = tanh(gamma*(x•y)+coef0)
         * ***/
        svm.setType(SVM.C_SVC);
        
        /***
         * 核类型
         * CvSVM::LINEAR - 没有任何向映像至高维空间，线性区分（或回归）在原始特征空间中被完成，这是最快的选择。 d(x,y) = x•y == (x,y)
         * CvSVM::POLY - 多项式核: d(x,y)= (gamma*(x•y)+coef0)degree
         * CvSVM::RBF - 径向基，对于大多数情况都是一个较好的选择：d(x,y)= exp(-gamma*|x-y|2)
         * CvSVM::SIGMOID - sigmoid函数被用作核函数:d(x,y) = tanh(gamma*(x•y)+coef0)
         * ***/
        svm.setKernel(SVM.LINEAR);
        
        // /训练参数迭代终止条件，训练类型，最大迭代次数 ，结果精确性精度
        TermCriteria criteria=new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER ,1000 ,0.001);
        svm.setTermCriteria(criteria);

        TrainData trainData = TrainData.create(i_TrainDatas ,Ml.ROW_SAMPLE ,i_TrainLables);
        svm.train(trainData.getSamples(), Ml.ROW_SAMPLE ,trainData.getResponses());
        return svm;
    }
    
    
    
    /**
     * 判定点的四周是否有值，本点是否是一个孤独的点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-08
     * @version     v1.0
     * 
     * @param i_Counter
     * @param i_H
     * @param i_W
     * @return
     */
    public static boolean isOnlyOneBlock(int [][] i_Counter ,int i_H ,int i_W)
    {
        int v_BlockWCount = i_Counter[0].length;  // 宽度上分割的块数
        int v_BlockHCount = i_Counter.length;     // 高度上分割的块数
        
        int v_LT = 0;  /* 左上角 */
        int v_RT = 0;  /* 右上角 */
        
        int v_LB = 0;  /* 左下角 */
        int v_RB = 0;  /* 右下角 */
        
        int v_LM = 0;  /* 左中间 */
        int v_RM = 0;  /* 右中间 */
        int v_TM = 0;  /* 顶中间 */
        int v_BM = 0;  /* 底中间 */
        
        if ( i_H >= 1 )
        {
            v_TM = i_Counter[i_H - 1][i_W];
            if ( v_TM > 0 ) { return false; }
            
            if ( i_W >= 1 )
            {
                v_LT = i_Counter[i_H - 1][i_W - 1];
                if ( v_LT > 0 ) { return false; }
            }
            
            if ( i_W < v_BlockWCount - 1 )
            {
                v_RT = i_Counter[i_H - 1][i_W + 1];
                if ( v_RT > 0 ) { return false; }
            }
        }
        
        if ( i_H < v_BlockHCount - 1 )
        {
            v_BM = i_Counter[i_H + 1][i_W];
            if ( v_BM > 0 ) { return false; }
            
            if ( i_W >= 1 )
            {
                v_LB = i_Counter[i_H + 1][i_W - 1];
                if ( v_LB > 0 ) { return false; }
            }
            
            if ( i_W < v_BlockWCount - 1 )
            {
                v_RB = i_Counter[i_H + 1][i_W + 1];
                if ( v_RB > 0 ) { return false; }
            }
        }
        
        if ( i_W >= 1 )
        {
            v_LM = i_Counter[i_H][i_W - 1];
            if ( v_LM > 0 ) { return false; }
        }
        
        if ( i_W < v_BlockWCount - 1 )
        {
            v_RM = i_Counter[i_H][i_W + 1];
            if ( v_RM > 0 ) { return false; }
        }
        
        return true;
    }
    
    
    
    public static void merge(List<CVPoint> io_Rectangles)
    {
        if ( io_Rectangles.size() <= 1 )
        {
            return;
        }
        
        for (int x=io_Rectangles.size() - 1; x>=0; x--)
        {
            CVPoint v_PA = io_Rectangles.get(x);
            
            for (int y=io_Rectangles.size() - 1; y>=0; y--)
            {
                if ( x == y )
                {
                    continue;
                }
                
                CVPoint v_PB    = io_Rectangles.get(y);
                boolean v_IsDel = false;
                
                // A在B的右下角，并相互有交叉区域
                if ( v_PB.getXMin() <= v_PA.getXMin() && v_PA.getXMin() <= v_PB.getXMax()
                  && v_PB.getYMin() <= v_PA.getYMin() && v_PA.getYMin() <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的左下角，并相互有交叉区域
                else if ( v_PB.getXMin() <= v_PA.getXMax() && v_PA.getXMax() <= v_PB.getXMax()
                       && v_PB.getYMin() <= v_PA.getYMin() && v_PA.getYMin() <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的右上角，并相互有交叉区域
                else if ( v_PB.getXMin() <= v_PA.getXMin() && v_PA.getXMin() <= v_PB.getXMax()
                       && v_PB.getYMin() <= v_PA.getYMax() && v_PA.getYMax() <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的左上角，并相互有交叉区域
                else if ( v_PB.getXMin() <= v_PA.getXMax() && v_PA.getXMax() <= v_PB.getXMax()
                       && v_PB.getYMin() <= v_PA.getYMax() && v_PA.getYMax() <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                
                // B在A的右下角，并相互有交叉区域
                else if ( v_PA.getXMin() <= v_PB.getXMin() && v_PB.getXMin() <= v_PA.getXMax()
                       && v_PA.getYMin() <= v_PB.getYMin() && v_PB.getYMin() <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的左下角，并相互有交叉区域
                else if ( v_PA.getXMin() <= v_PB.getXMax() && v_PB.getXMax() <= v_PA.getXMax()
                       && v_PA.getYMin() <= v_PB.getYMin() && v_PB.getYMin() <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的右上角，并相互有交叉区域
                else if ( v_PA.getXMin() <= v_PB.getXMin() && v_PB.getXMin() <= v_PA.getXMax()
                       && v_PA.getYMin() <= v_PB.getYMax() && v_PB.getYMax() <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的左上角，并相互有交叉区域
                else if ( v_PA.getXMin() <= v_PB.getXMax() && v_PB.getXMax() <= v_PA.getXMax()
                       && v_PA.getYMin() <= v_PB.getYMax() && v_PB.getYMax() <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                
                if ( v_IsDel )
                {
                    // 删除包含关系的、交叉关系的
                    io_Rectangles.remove(x);
                    
                    v_PB.setXMin(v_PA.getXMin());
                    v_PB.setXMax(v_PA.getXMax());
                    v_PB.setYMin(v_PA.getYMin());
                    v_PB.setYMax(v_PA.getYMax());
                    break;
                }
            }
        }
    }
    
    
    public static void merge2(List<CVPoint> io_Rectangles)
    {
        if ( io_Rectangles.size() <= 1 )
        {
            return;
        }
        
        for (int x=0; x<io_Rectangles.size(); x++)
        {
            CVPoint v_PA = io_Rectangles.get(x);
            
            for (int y=io_Rectangles.size()-1; y>=0; y--)
            {
                if ( x == y )
                {
                    continue;
                }
                
                CVPoint v_PB    = io_Rectangles.get(y);
                boolean v_IsDel = false;
                
                // A在B的右下角，并相互有交叉区域
                if ( v_PB.getXMin() <= Math.max(v_PA.getXMin() ,v_PB.getXMin()) && Math.max(v_PA.getXMin() ,v_PB.getXMin()) <= v_PB.getXMax()
                  && v_PB.getYMin() <= Math.max(v_PA.getYMin() ,v_PB.getYMin()) && Math.max(v_PA.getYMin() ,v_PB.getYMin()) <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的左下角，并相互有交叉区域
                else if ( v_PB.getXMin() <= Math.min(v_PA.getXMax() ,v_PB.getXMax()) && Math.min(v_PA.getXMax() ,v_PB.getXMax()) <= v_PB.getXMax()
                       && v_PB.getYMin() <= Math.max(v_PA.getYMin() ,v_PB.getYMin()) && Math.max(v_PA.getYMin() ,v_PB.getYMin()) <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的右上角，并相互有交叉区域
                else if ( v_PB.getXMin() <= Math.max(v_PA.getXMin() ,v_PB.getXMin()) && Math.max(v_PA.getXMin() ,v_PB.getXMin()) <= v_PB.getXMax()
                       && v_PB.getYMin() <= Math.min(v_PA.getYMax() ,v_PB.getYMax()) && Math.min(v_PA.getYMax() ,v_PB.getYMax()) <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                // A在B的左上角，并相互有交叉区域
                else if ( v_PB.getXMin() <= Math.min(v_PA.getXMax() ,v_PB.getXMax()) && Math.min(v_PA.getXMax() ,v_PB.getXMax()) <= v_PB.getXMax()
                       && v_PB.getYMin() <= Math.min(v_PA.getYMax() ,v_PB.getYMax()) && Math.min(v_PA.getYMax() ,v_PB.getYMax()) <= v_PB.getYMax() )
                {
                    v_IsDel = true;
                }
                
                // B在A的右下角，并相互有交叉区域
                else if ( v_PA.getXMin() <= Math.max(v_PB.getXMin() ,v_PA.getXMin()) && Math.max(v_PB.getXMin() ,v_PA.getXMin()) <= v_PA.getXMax()
                       && v_PA.getYMin() <= Math.max(v_PB.getYMin() ,v_PA.getYMin()) && Math.max(v_PB.getYMin() ,v_PA.getYMin()) <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的左下角，并相互有交叉区域
                else if ( v_PA.getXMin() <= Math.min(v_PB.getXMax() ,v_PA.getXMax()) && Math.min(v_PB.getXMax() ,v_PA.getXMax()) <= v_PA.getXMax()
                       && v_PA.getYMin() <= Math.max(v_PB.getYMin() ,v_PA.getYMin()) && Math.max(v_PB.getYMin() ,v_PA.getYMin()) <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的右上角，并相互有交叉区域
                else if ( v_PA.getXMin() <= Math.max(v_PB.getXMin() ,v_PA.getXMin()) && Math.max(v_PB.getXMin() ,v_PA.getXMin()) <= v_PA.getXMax()
                       && v_PA.getYMin() <= Math.min(v_PB.getYMax() ,v_PA.getYMax()) && Math.min(v_PB.getYMax() ,v_PA.getYMax()) <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                // B在A的左上角，并相互有交叉区域
                else if ( v_PA.getXMin() <= Math.min(v_PB.getXMax() ,v_PA.getXMin()) && Math.min(v_PB.getXMax() ,v_PA.getXMin()) <= v_PA.getXMax()
                       && v_PA.getYMin() <= Math.min(v_PB.getYMax() ,v_PA.getYMax()) && Math.min(v_PB.getYMax() ,v_PA.getYMax()) <= v_PA.getYMax() )
                {
                    v_IsDel = true;
                }
                
                if ( v_IsDel )
                {
                    // 删除包含关系的、交叉关系的
                    io_Rectangles.remove(y);
                    
                    v_PA.setXMin(v_PB.getXMin());
                    v_PA.setXMax(v_PB.getXMax());
                    v_PA.setYMin(v_PB.getYMin());
                    v_PA.setYMax(v_PB.getYMax());
                    break;
                }
            }
        }
    }
    
    
    
    /**
     * 绘制分割的方块
     * 
     * @param i_MTarget
     * @param i_BlockWCount
     * @param i_BlockHCount
     * @param i_BlockSize
     * @param i_BlockLineColor
     */
    public static void drawBlocks(Mat i_MTarget ,int i_BlockWCount ,int i_BlockHCount ,int i_BlockSize ,Scalar i_BlockLineColor)
    {
        int      v_Width          = i_MTarget.cols();             // 列数是宽度
        int      v_Height         = i_MTarget.rows();             // 行数是高度
        
        // 绘制竖线
        for (int v_W=1; v_W<i_BlockWCount; v_W++)
        {
            Imgproc.line(i_MTarget ,new Point(v_W * i_BlockSize ,0) ,new Point(v_W * i_BlockSize ,v_Height) ,i_BlockLineColor ,1);
        }
        // 绘制横线
        for (int v_H=1; v_H<i_BlockHCount; v_H++)
        {
            Imgproc.line(i_MTarget ,new Point(0 ,v_H * i_BlockSize) ,new Point(v_Width ,v_H * i_BlockSize) ,i_BlockLineColor ,1);
        }
    }
    
    
    
    /**
     * 计算出块号
     * 
     * @param i_X            当前X坐标
     * @param i_Y            当前Y坐标
     * @param i_BlockSize    块大小
     * @param i_BlockWCount  宽度上分割的块数
     * @return               最小下标从0开始
     */
    public static int calcBlockNo(double i_X ,double i_Y ,int i_BlockSize ,int i_BlockWCount)
    {
        double v_X = i_X;
        double v_Y = i_Y;
        
        if ( v_X % i_BlockSize != 0 )
        {
            v_X = Help.round(v_X * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        
        if ( v_Y % i_BlockSize != 0 )
        {
            v_Y = Help.round(v_Y * 1.0D / i_BlockSize ,0) * i_BlockSize;
        }
        
        int v_ColIndex = (int)(v_X / i_BlockSize);
        int v_RowIndex = (int)(v_Y / i_BlockSize);
        
        return v_RowIndex * i_BlockWCount + v_ColIndex;
    }
    
}
