package org.hy.common.video.junit.openCV;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.TablePartitionRID;
import org.hy.common.video.CVPoint;
import org.hy.common.video.OpenCV;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;





/**
 * 测试单元：人脸识别
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-14
 * @version     v1.0
 */
public class JU_108_Face
{
    private static final Logger                $Logger         = new Logger(JU_108_Face.class ,true);
    
    /** 人脸检测分类器 */
    private static final CascadeClassifier     $Detector;
    
    /** 块缓存区。防止重复开辟内存空间 */
    private static final Map<String ,int [][]> $BlocksCache    = new HashMap<String ,int [][]>();
    
    
    
    private boolean              isRunning;
    
    /** 间隔帧数。即不是每一帧均进行AI算法，可以忽略一些。默认值为：5 */
    private int                  frameInterval;
    
    /** 未知人脸图片的保存路径 */
    private String               savePathFaceUnknown;
    
    /** 已知人脸数据的路径 */
    private String               savePathFaceDB;
    
    /** 每张人脸照片的大小（单位：像素） */
    private int                  faceSize;
    
    /** 人脸编号与人员名称的关系 */
    private Map<Integer ,String> faceNameMap;
    
    private SVM                  svm;
    
    
    
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        $Detector = new CascadeClassifier("D:\\OpenCV\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
    }
    
    
    public JU_108_Face()
    {
        this.frameInterval       = 0;
        this.savePathFaceUnknown = "D:\\VideoDatas\\face\\unknown";
        this.savePathFaceDB      = "D:\\VideoDatas\\face\\FaceDatabase";
        this.faceSize            = 100;
        this.faceNameMap         = new HashMap<Integer ,String>();
    }
    
    
    
    @Test
    public void face()
    {
        String v_VideoURL = "rtsp://admin:wzyb9114@10.1.130.210/Streaming/Channels/101";
        v_VideoURL = JU_008_Template.class.getResource("JU_108_Face.mp4").getFile().substring(1);
        
        step00_SVM_01_Train();
        step00_SVM_02_LoadSVM();
        step01_ReadVideo(v_VideoURL);
    }
    
    
    
    /**
     * 开始训练
     */
    private void step00_SVM_01_Train()
    {
        List<Mat>     v_TrainDatas  = new ArrayList<Mat>();
        List<Integer> v_TrainLabels = new ArrayList<Integer>();
        
        File    v_FaceDBHomeDir = new File(this.savePathFaceDB);
        File [] v_FaceDBFile    = v_FaceDBHomeDir.listFiles();
        for (File v_ItemDir : v_FaceDBFile)
        {
            if ( v_ItemDir.isDirectory() )
            {
                String [] v_ItemDirName = v_ItemDir.getName().split("_");
                Integer   v_FaceNo      = Integer.parseInt(v_ItemDirName[0]);
                String    v_FaceName    = v_ItemDirName[1];
                
                this.faceNameMap.put(v_FaceNo ,v_FaceName);
                step00_SVM_01_MakeTrainDatas(v_ItemDir.getAbsolutePath() ,v_FaceNo ,v_TrainDatas ,v_TrainLabels);
            }
        }
        
        
        Mat v_MTrainDatas  = new Mat();
        Mat v_MTrainLabels = new Mat(v_TrainLabels.size(), 1, CvType.CV_32SC1);
        
        Core.vconcat(v_TrainDatas, v_MTrainDatas);
        for (int i = 0; i < v_TrainLabels.size(); i++)
        {
            int [] v_TrainLabel = { v_TrainLabels.get(i) };
            v_MTrainLabels.put(i, 0, v_TrainLabel);
        }
        
        SVM v_SVM = OpenCV.svm(v_MTrainDatas ,v_MTrainLabels);
        v_SVM.save(this.savePathFaceDB + Help.getSysPathSeparator() + "HY.xml");
        
        for (Mat v_TrainData : v_TrainDatas)
        {
            v_TrainData.release();
            v_TrainData = null;
        }
        
        v_TrainDatas   .clear();
        v_TrainLabels  .clear();
        v_MTrainDatas .release();
        v_MTrainLabels.release();
        v_TrainDatas   = null;
        v_TrainLabels  = null;
        v_MTrainDatas  = null;
        v_MTrainLabels = null;
    }
    
    
    
    /**
     * 生成训练数据
     * 
     * @param i_DirFullName
     * @param i_TrainLabel
     * @param io_TrainDatas
     * @param io_TrainLabels
     */
    private void step00_SVM_01_MakeTrainDatas(String i_DirFullName ,int i_TrainLabel ,List<Mat> io_TrainDatas ,List<Integer> io_TrainLabels)
    {
        File    v_SVMDir   = new File(i_DirFullName);
        File [] v_SVMFiles = v_SVMDir.listFiles();
        
        for (File v_SVMFile : v_SVMFiles)
        {
            Mat v_MSource = Imgcodecs.imread(v_SVMFile.getAbsolutePath());
            Mat v_MTarget = new Mat();
            
            Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);
            Imgproc.resize(v_MTarget ,v_MTarget ,new Size(this.faceSize ,this.faceSize));
            v_MTarget.convertTo(v_MTarget, CvType.CV_32FC1);
            
            /*
             * cn为新的通道数，如果cn = 0，表示通道数不会改变。
             * 参数rows为新的行数，如果rows = 0，表示行数不会改变。
             * 
             * 我们将参数定义为reshape(1, 1)的结果就是原图像对应的矩阵将被拉伸成一个一行的向量，作为特征向量。
             */
            Mat v_Reshape = v_MTarget.reshape(0, 1);
            
            io_TrainDatas.add(v_Reshape);
            io_TrainLabels.add(i_TrainLabel);
            
            v_MSource.release();
            v_MTarget.release();
            v_MSource = null;
            v_MTarget = null;
        }
    }
    
    
    
    
    /**
     * 加载SVM模型
     */
    private void step00_SVM_02_LoadSVM()
    {
        this.svm = SVM.load(this.savePathFaceDB + Help.getSysPathSeparator() + "HY.xml");
    }
    
    
    
    /**
     * 人脸检测
     * 
     * @param i_MSource
     * @return
     */
    private Integer step00_SVM_03Check(Mat i_MSource)
    {
        Mat v_MTarget = new Mat();
        
        Imgproc.cvtColor(i_MSource ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(v_MTarget ,v_MTarget ,new Size(this.faceSize ,this.faceSize));
        v_MTarget.convertTo(v_MTarget, CvType.CV_32FC1);
        
        Mat v_Reshape = v_MTarget.reshape(1, 1);
        float v_Ret = this.svm.predict(v_Reshape);
        
        v_MTarget.release();
        v_MTarget = null;
        return (int)v_Ret;
    }
    
        
    
    /**
     * 读取视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-14
     * @version     v1.0
     * 
     * @param i_VideoURL
     */
    private void step01_ReadVideo(String i_VideoURL)
    {
        Date v_STime = new Date();
        $Logger.info("AI 开始 " + i_VideoURL);
        
        VideoCapture  v_VideoCapture = new VideoCapture();
        int           v_MIndex       = 0;                           // 帧编号
        Mat           v_MCurrent     = null;                        // 当前帧
        Mat           v_MOld         = null;                        // 之前处理的帧
        Mat           v_MShow        = null;                        // 输出显示的帧
        int           v_AICount      = 0;                           // AI分析数
        int           v_DrawCount    = 0;                           // 绘制AI数
        List<CVPoint> v_CVPoints     = null;
        List<Mat>     v_VideoDatas   = new ArrayList<Mat>();
        boolean       v_IsRead       = false;
        
        try
        {
            v_VideoCapture.open(i_VideoURL);
            this.isRunning = true;
            
            while ( this.isRunning )
            {
                v_MCurrent = new Mat();
                v_IsRead   = v_VideoCapture.read(v_MCurrent);
                if ( !v_IsRead )
                {
                    break;
                }
                
                List<CVPoint> v_FacePoints = null;
                if ( this.frameInterval == 0 || v_MIndex++ % this.frameInterval == 0 )
                {
                    if ( v_CVPoints != null )
                    {
                        v_CVPoints.clear();
                        v_CVPoints = null;
                    }
                    
                    if ( v_MOld != null )
                    {
                        v_CVPoints = step02_AI_Move(v_MOld ,v_MCurrent);
                        v_AICount++;
                        v_MOld.release();
                        
                        v_FacePoints = step03_AI_Face(v_MCurrent ,v_CVPoints);
                    }
                    v_MOld = v_MCurrent;
                }
                
                
                v_MShow = v_MOld.clone();
                if ( v_FacePoints != null )
                {
                    // step04_DrawRect(v_MShow ,v_CVPoints ,OpenCV.$Color_Green ,2);
                    step04_DrawFace(v_MShow ,v_FacePoints ,OpenCV.$Color_Green ,2);
                    v_DrawCount++;
                }
                
                v_VideoDatas.add(v_MShow);
                
                /*
                HighGui.imshow("视频视频" + ++v_MIndex ,v_MShow);
                int v_Key = HighGui.waitKey(1);
                if ( v_Key == 27 )
                {
                    break;
                }
                HighGui.destroyAllWindows();
                */
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally
        {
            if ( v_CVPoints != null )
            {
                v_CVPoints.clear();
                v_CVPoints = null;
            }
            
            if ( v_MOld != null )
            {
                v_MOld.release();
                v_MOld = null;
            }
            
            if ( v_MCurrent != null )
            {
                v_MCurrent.release();
                v_MCurrent = null;
            }
            
            try
            {
                v_VideoCapture.release();
                v_VideoCapture = null;
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
        }
        
        $Logger.info("AI 分析完成 " + " 用时：" + Date.toTimeLen(Date.getNowTime().getTime() - v_STime.getTime()) + "   " + v_VideoDatas.size() + "帧 " + v_AICount + "次AI " + v_DrawCount + "次绘制");
        
        String v_SaveVideoName = "D:\\VideoDatas\\face.mp4";
        // v_DrawCount = 0;   // 暂时不保存
        step05_SaveVideo(v_DrawCount ,v_VideoDatas ,v_SaveVideoName);
    }
        
    
    
    private static List<CVPoint> step02_AI_Move(Mat i_M1 ,Mat i_M2)
    {
        Date v_STime  = new Date();
        long v_TLen01 = 0;
        long v_TLen02 = 0;
        long v_TLen03 = 0;
        long v_TLen04 = 0;
        long v_TLen05 = 0;
        long v_TLen06 = 0;
        
        Mat  v_M1Gray = new Mat();
        Mat  v_M2Gray = new Mat();
        
//        Imgproc.resize(i_M1 ,v_M1Gray ,new Size(1280 ,720));
//        Imgproc.resize(i_M2 ,v_M2Gray ,new Size(1280 ,720));
        
        Imgproc.cvtColor(i_M1 ,v_M1Gray ,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(i_M2 ,v_M2Gray ,Imgproc.COLOR_BGR2GRAY);
        
        Mat v_MSubtract = new Mat();
        Core.subtract(v_M1Gray ,v_M2Gray ,v_MSubtract);
        Imgproc.threshold(v_MSubtract ,v_MSubtract ,40 ,255 ,Imgproc.THRESH_BINARY);
        
        v_M1Gray.release();
        v_M2Gray.release();
        v_M1Gray = null;
        v_M2Gray = null;
        
        // 1280 * 720
        int      v_Width          = i_M2.cols();             // 列数是宽度
        int      v_Height         = i_M2.rows();             // 行数是高度
        int      v_BlockSize      = 16;                      // 块大小
        int      v_BlockWCount    = v_Width  / v_BlockSize;  // 宽度上分割的块数
        int      v_BlockHCount    = v_Height / v_BlockSize;  // 高度上分割的块数
        int [][] v_Counter        = $BlocksCache.get(v_BlockHCount + "," + v_BlockWCount);
        
        if ( v_Counter == null )
        {
            v_Counter = new int[v_BlockHCount][v_BlockWCount];
            $BlocksCache.put(v_BlockHCount + "," + v_BlockWCount ,v_Counter);
        }
        else
        {
            blocksZero(v_Counter);
        }
        
        // 获取轮廓
        List<MatOfPoint> v_Contours  = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Mat              v_Hierarchy = new Mat();
        Imgproc.findContours(v_MSubtract ,v_Contours ,v_Hierarchy ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        v_MSubtract.release();
        v_Hierarchy.release();
        v_MSubtract = null;
        v_Hierarchy = null;
        
        
        v_TLen01 = Date.getNowTime().getTime() - v_STime.getTime();
        v_STime  = new Date();
        
        
        OpenCV.contoursCounter(v_Contours ,v_BlockSize ,v_Counter);
        v_Contours.clear();
        v_Contours = null;
        
        
        v_TLen02 = Date.getNowTime().getTime() - v_STime.getTime();
        v_STime  = new Date();
        
        
        // 确定轮廓大小，合并相邻的块为大的轮廓
        Map<Integer ,CVPoint>              v_CVPoints   = new HashMap<Integer ,CVPoint>();
        TablePartitionRID<String ,Integer> v_CVPointMap = new TablePartitionRID<String ,Integer>();
        int                                v_CVPMapNo   = 0;
        for (int v_H=0; v_H<v_BlockHCount; v_H++)
        {
            for (int v_W=0; v_W<v_BlockWCount; v_W++)
            {
                int v_Count = v_Counter[v_H][v_W];
                if ( v_Count <= 0 )
                {
                    continue;
                }
                
                if ( OpenCV.isOnlyOneBlock(v_Counter ,v_H ,v_W) )
                {
                    continue;
                }
                
                double  v_XMin = v_BlockSize * (v_W + 0);
                double  v_XMax = v_BlockSize * (v_W + 1);
                double  v_YMin = v_BlockSize * (v_H + 0);
                double  v_YMax = v_BlockSize * (v_H + 1);
                Integer v_PKey = v_CVPointMap.getRow(v_XMin + "" ,v_YMin + "");
                CVPoint v_CVP  = v_PKey == null ? null : v_CVPoints.get(v_PKey);
                
                if ( v_CVP == null )
                {
                    v_CVP  = new CVPoint(v_XMin ,v_YMin ,v_XMax ,v_YMax);
                    v_PKey = ++v_CVPMapNo;
                    v_CVPoints.put(v_PKey ,v_CVP);
                }
                else
                {
                    v_CVP.union(v_XMin ,v_YMin ,v_XMax ,v_YMax);
                }
                
                for (double v_XIndex = Math.max(v_CVP.getXHelp() ,v_CVP.getXMin()); v_XIndex <= v_CVP.getXMax(); v_XIndex = v_XIndex + v_BlockSize)
                {
                    for (double v_YIndex = Math.max(v_CVP.getYHelp() ,v_CVP.getYMin()); v_YIndex <= v_CVP.getYMax(); v_YIndex = v_YIndex + v_BlockSize)
                    {
                        CVPoint v_CVPTemp  = null;
                        Integer v_PKeyTemp = null;
                        
                        v_PKeyTemp = v_CVPointMap.getRow((v_XIndex + v_BlockSize) + "" ,v_YIndex + "");
                        if ( v_PKey != v_PKeyTemp )
                        {
                            v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                            if ( v_CVPTemp == null )
                            {
                                v_CVPointMap.putRow((v_XIndex + v_BlockSize) + "" ,v_YIndex + "" ,v_PKey);
                            }
                            else if ( v_CVPTemp != v_CVP )
                            {
                                v_CVP.union(v_CVPTemp);
                                v_CVPoints.put(v_PKeyTemp ,v_CVP);
                            }
                        }
                        
                        v_PKeyTemp = v_CVPointMap.getRow((v_XIndex + v_BlockSize) + "" ,(v_YIndex + v_BlockSize) + "");
                        if ( v_PKey != v_PKeyTemp )
                        {
                            v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                            if ( v_CVPTemp == null )
                            {
                                v_CVPointMap.putRow((v_XIndex + v_BlockSize) + "" ,(v_YIndex + v_BlockSize) + "" ,v_PKey);
                            }
                            else if ( v_CVPTemp != v_CVP )
                            {
                                v_CVP.union(v_CVPTemp);
                            }
                        }
                        
                        v_PKeyTemp = v_CVPointMap.getRow(v_XIndex + "" ,(v_YIndex + v_BlockSize) + "");
                        if ( v_PKey != v_PKeyTemp )
                        {
                            v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                            if ( v_CVPTemp == null )
                            {
                                v_CVPointMap.putRow(v_XIndex + "" ,(v_YIndex + v_BlockSize) + "" ,v_PKey);
                            }
                            else if ( v_CVPTemp != v_CVP )
                            {
                                v_CVP.union(v_CVPTemp);
                            }
                        }
                    }
                }
                
                v_CVP.setXHelp(v_CVP.getXMax());
                v_CVP.setYHelp(v_CVP.getYMax());
            }
        }
        v_Counter = null;
        
        
        v_TLen03 = Date.getNowTime().getTime() - v_STime.getTime();
        v_STime  = new Date();
        
        
        List<CVPoint> v_CVPointList = Help.toList(v_CVPoints);
        Help.toDistinct(v_CVPointList ,"rectArea DESC");
        
        
        v_TLen04 = Date.getNowTime().getTime() - v_STime.getTime();
        v_STime  = new Date();
        
        
        // 排除不符合要求的轮廓
        for (int x=v_CVPointList.size()-1; x>=0; x--)
        {
            CVPoint v_CVPoint = v_CVPointList.get(x);
            
            if ( v_CVPoint.getCount() <= 4 )
            {
                if ( v_CVPoint.getYMin() > v_BlockSize * 2 )
                {
                    Integer v_PKey = v_CVPointMap.getRow(v_CVPoint.getXMin() + "" ,(v_CVPoint.getYMin() - v_BlockSize) + "");
                    CVPoint v_CVP  = v_PKey == null ? null : v_CVPoints.get(v_PKey);
                    if ( v_CVP != null )
                    {
                        v_CVP.union(v_CVPoint);
                        v_CVPointList.remove(x);
                        continue;
                    }

                    v_PKey = v_CVPointMap.getRow(v_CVPoint.getXMin() + "" ,(v_CVPoint.getYMin() - v_BlockSize * 2) + "");
                    v_CVP  = v_PKey == null ? null : v_CVPoints.get(v_PKey);
                    if ( v_CVP != null )
                    {
                        v_CVP.union(v_CVPoint);
                        v_CVPointList.remove(x);
                        continue;
                    }
                }

                v_CVPointList.remove(x);
            }
            else
            {
                if ( v_CVPoint.getHeight() <= v_BlockSize * 2 )
                {
                    v_CVPointList.remove(x);
                }
                else if ( v_CVPoint.getWidth() >= v_Width / 3 )
                {
                    v_CVPointList.remove(x);
                }
            }
        }
        
        
        v_TLen05 = Date.getNowTime().getTime() - v_STime.getTime();
        v_STime  = new Date();
        
        
        v_CVPoints  .clear();
        v_CVPointMap.clear();
        v_CVPoints   = null;
        v_CVPointMap = null;
        
        OpenCV.merge(v_CVPointList);
        OpenCV.merge(v_CVPointList);
        
        v_TLen06 = Date.getNowTime().getTime() - v_STime.getTime();
        /*
        $Logger.info("AI 6步用时：" + Date.toTimeLen(v_TLen01)
                            + "  " + Date.toTimeLen(v_TLen02)
                            + "  " + Date.toTimeLen(v_TLen03)
                            + "  " + Date.toTimeLen(v_TLen04)
                            + "  " + Date.toTimeLen(v_TLen05)
                            + "  " + Date.toTimeLen(v_TLen06));
        */
        return v_CVPointList;
    }
    
    
    
    private List<CVPoint> step03_AI_Face(Mat i_MSource ,List<CVPoint> i_CVPoints)
    {
        List<CVPoint> v_RetPoints = new ArrayList<CVPoint>();
        
        for (CVPoint v_CVP : i_CVPoints)
        {
            Mat           v_CutImg     = OpenCV.cutImage(i_MSource ,v_CVP);
            List<CVPoint> v_FacePoints = step03_AI_Face_HaarFace(v_CutImg);
            
            if ( !Help.isNull(v_FacePoints) )
            {
                for (CVPoint v_Face : v_FacePoints)
                {
                    Mat     v_CutFace = OpenCV.cutImage(v_CutImg ,v_Face);
                    Integer v_FaceNo  = step00_SVM_03Check(v_CutFace);
                    
                    if ( v_FaceNo == null )
                    {
                        OpenCV.resizeWidth(v_CutFace ,v_CutFace ,this.faceSize);
                        
                        String v_SaveFile = this.savePathFaceUnknown + Help.getSysPathSeparator() + Date.getNowTime().getFullMilli_ID() + ".jpg";  // 注意：文件名称，不支持中文
                        Imgcodecs.imwrite(v_SaveFile ,v_CutFace);
                        
                        v_CutFace.release();
                        v_CutFace = null;
                    }
                    else
                    {
                        CVPoint v_FacePoint = new CVPoint(v_CVP.getXMin() + v_Face.getXMin()
                                                         ,v_CVP.getYMin() + v_Face.getYMin()
                                                         ,v_CVP.getXMin() + v_Face.getXMax()
                                                         ,v_CVP.getYMin() + v_Face.getYMax());
                        
                        v_FacePoint.setFaceNo(v_FaceNo);
                        v_RetPoints.add(v_FacePoint);
                    }
                }
                
                v_FacePoints.clear();
                v_FacePoints = null;
            }
            
            v_CutImg.release();
            v_CutImg = null;
        }
        
        return v_RetPoints;
    }
    
    
    
    /**
     * 识别人脸
     * 
     * @param i_MSource
     * @return
     */
    private static List<CVPoint> step03_AI_Face_HaarFace(Mat i_MSource)
    {
        MatOfRect     v_DetectorResult = new MatOfRect();               // 检测结果，矩形集
        List<CVPoint> v_CVPoints       = null;
        
        try
        {
            $Detector.detectMultiScale(i_MSource ,v_DetectorResult);
            
            if ( v_DetectorResult.toArray().length >= 1 )
            {
                v_CVPoints = new ArrayList<CVPoint>();
                
                // 绘制检测结果
                for (Rect v_Rect : v_DetectorResult.toArray())
                {
                    v_CVPoints.add(new CVPoint(v_Rect.x ,v_Rect.y ,v_Rect.x + v_Rect.width ,v_Rect.y + v_Rect.height));
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally
        {
            if ( v_DetectorResult != null )
            {
                v_DetectorResult.release();
                v_DetectorResult = null;
            }
        }
        
        return v_CVPoints;
    }
    
    
    
    private static void step04_DrawRect(Mat io_MSource ,List<CVPoint> i_CVPoints ,Scalar i_Color ,int i_LineSize)
    {
        List<MatOfPoint> v_Points = new ArrayList<MatOfPoint>();
        
        
        Imgproc.polylines(io_MSource ,v_Points ,true ,i_Color ,i_LineSize);
        
        for (CVPoint v_CVP : i_CVPoints)
        {
            Imgproc.rectangle(io_MSource ,v_CVP.toRect() ,i_Color ,i_LineSize);
        }
    }
    
    
    
    private void step04_DrawFace(Mat io_MSource ,List<CVPoint> i_CVPoints ,Scalar i_Color ,int i_LineSize)
    {
        for (CVPoint v_CVP : i_CVPoints)
        {
            Imgproc.putText(io_MSource
                           ,"马龙"// this.faceNameMap.get(v_CVP.getFaceNo())
                           ,new Point(v_CVP.getXMin() + v_CVP.getWidth() / 2.0D ,v_CVP.getYMin() - 16D)
                           ,4
                           ,1.0D
                           ,i_Color
                           ,i_LineSize);
            Imgproc.rectangle(io_MSource ,v_CVP.toRect() ,i_Color ,i_LineSize);
        }
    }
    
    
    
    private static void step05_SaveVideo(int i_DrawCount ,List<Mat> io_VideoDatas ,String i_SaveVideoName)
    {
        VideoWriter v_VideoWrite = null;
        try
        {
            if ( i_DrawCount >= 1 )
            {
                v_VideoWrite = new VideoWriter();
                Size v_VidoeSize   = new Size();
                v_VidoeSize.width  = 1488;                                   // v_VideoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
                v_VidoeSize.height = 720;                                    // v_VideoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
                int    v_Fourcc    = VideoWriter.fourcc('M', 'P', '4', '2'); // VideoWriter.fourcc('F', 'L', 'V', '1'); // v_VideoCapture.get(Videoio.CAP_PROP_FOURCC);
                double v_Fps       = 24;                                     // v_VideoCapture.get(Videoio.CAP_PROP_FPS);
                v_VideoWrite.open(i_SaveVideoName ,v_Fourcc ,v_Fps ,v_VidoeSize);
                for (Mat v_Data : io_VideoDatas)
                {
                    v_VideoWrite.write(v_Data);
                    v_Data.release();
                    v_Data = null;
                }
            }
            else
            {
                for (Mat v_Data : io_VideoDatas)
                {
                    v_Data.release();
                    v_Data = null;
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally
        {
            io_VideoDatas.clear();
            io_VideoDatas = null;
            
            try
            {
                if ( v_VideoWrite != null )
                {
                    v_VideoWrite.release();
                    v_VideoWrite = null;
                }
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
        }
    }
    
    
    
    private static void blocksZero(int [][] i_Counter)
    {
        for (int v_H=0; v_H<i_Counter.length; v_H++)
        {
            for (int v_W=0; v_W<i_Counter[v_H].length; v_W++)
            {
                i_Counter[v_H][v_W] = 0;
            }
        }
    }
    
}
