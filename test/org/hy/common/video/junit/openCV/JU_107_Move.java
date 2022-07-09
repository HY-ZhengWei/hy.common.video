package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.TablePartitionRID;
import org.hy.common.video.CVPoint;
import org.hy.common.video.OpenCV;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;





/**
 * 测试单元：运动检测
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-08
 * @version     v1.0
 */
public class JU_107_Move
{
    
    public JU_107_Move()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void move()
    {
        VideoCapture v_VideoCapture = new VideoCapture();
        
        v_VideoCapture.open(JU_107_Move.class.getResource("JU_107_Move.flv").getFile().substring(1));
        
        List<Mat>     v_VideoDatas = new ArrayList<Mat>();
        Mat           v_M1         = new Mat();                   // 前一帧
        Mat           v_M2         = null;                        // 后一帧
        Mat           v_MShow      = null;
        int           v_Index      = 0;
        boolean       v_IsRead     = v_VideoCapture.read(v_M1);
        Scalar        v_Color      = new Scalar(0 ,0 ,255);
        List<CVPoint> v_CVPoints   = null;
        while ( v_VideoCapture.isOpened() && v_IsRead )
        {
            v_M2     = new Mat();
            v_IsRead = v_VideoCapture.read(v_M2);
            
            if ( v_Index++ % 4 == 0 )
            {
                if ( v_CVPoints != null )
                {
                    v_CVPoints.clear();
                    v_CVPoints = null;
                }
                
                v_CVPoints = aiMove(v_M1 ,v_M2);
                v_M1       = v_M2;
            }
            
            
            v_MShow = v_M2.clone();
            drawRect(v_MShow ,v_CVPoints ,v_Color ,2);
            HighGui.imshow("视频视频" + ++v_Index ,v_MShow);
            int v_Key = HighGui.waitKey(1);
            if ( v_Key == 27 )
            {
                break;
            }
            HighGui.destroyAllWindows();
        }
        
        v_VideoCapture.release();  // 释放内存
    }
    
    
    
    private static void drawRect(Mat io_MSource ,List<CVPoint> i_CVPoints ,Scalar i_Color ,int i_LineSize)
    {
        for (CVPoint v_CVP : i_CVPoints)
        {
            Imgproc.rectangle(io_MSource ,v_CVP.toRect() ,i_Color ,i_LineSize);
        }
    }
    
    
    
    private List<CVPoint> aiMove(Mat i_M1 ,Mat i_M2)
    {
        Mat v_M1Gray = new Mat();
        Mat v_M2Gray = new Mat();
        
        Imgproc.GaussianBlur(i_M1 ,v_M1Gray ,new Size(3 ,3) ,0 ,0);  // 高斯噪声。卷积核3越小越清晰
        Imgproc.GaussianBlur(i_M2 ,v_M2Gray ,new Size(3 ,3) ,0 ,0);  // 高斯噪声。卷积核3越小越清晰
        
        Imgproc.cvtColor(i_M1 ,v_M1Gray ,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(i_M2 ,v_M2Gray ,Imgproc.COLOR_BGR2GRAY);
        
        Mat v_MSubtract = new Mat();
        Core.subtract(v_M1Gray ,v_M2Gray ,v_MSubtract);
        
        
        Imgproc.threshold(v_MSubtract ,v_MSubtract ,25 ,255 ,Imgproc.THRESH_BINARY);
        
        
//        HighGui.imshow("二值化" ,v_MSubtract);
//        HighGui.waitKey(0);
        
        
        // 1280 * 720
        int      v_Width          = i_M2.cols();             // 列数是宽度
        int      v_Height         = i_M2.rows();             // 行数是高度
        int      v_BlockSize      = 16;                      // 块大小
        int      v_BlockWCount    = v_Width  / v_BlockSize;  // 宽度上分割的块数
        int      v_BlockHCount    = v_Height / v_BlockSize;  // 高度上分割的块数
        Scalar   v_BlockLineColor = new Scalar(255 ,255 ,255);
        int [][] v_Counter        = new int[v_BlockHCount][v_BlockWCount];
        
        
        // 获取轮廓
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MSubtract ,v_Contours ,new Mat() ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        Scalar v_MMaskColor = new Scalar(255 ,255 ,255);
        Mat v_MMask = i_M2.clone();
        
        v_Counter = OpenCV.contoursCounter(v_Contours ,v_BlockWCount ,v_BlockHCount ,v_BlockSize);
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
                
                for (double v_XIndex = v_CVP.getXMin(); v_XIndex <= v_CVP.getXMax(); v_XIndex = v_XIndex + v_BlockSize)
                {
                    for (double v_YIndex = v_CVP.getYMin(); v_YIndex <= v_CVP.getYMax(); v_YIndex = v_YIndex + v_BlockSize)
                    {
                        CVPoint v_CVPTemp  = null;
                        Integer v_PKeyTemp = null;
                        
                        v_PKeyTemp = v_CVPointMap.getRow((v_XIndex + v_BlockSize) + "" ,v_YIndex + "");
                        v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                        if ( v_CVPTemp != null && v_CVPTemp != v_CVP )
                        {
                            v_CVP.union(v_CVPTemp);
                            v_CVPoints.put(v_PKeyTemp ,v_CVP);
                        }
                        v_CVPointMap.putRow((v_XIndex + v_BlockSize) + "" ,v_YIndex + "" ,v_PKey);
                        
                        
                        v_PKeyTemp = v_CVPointMap.getRow((v_XIndex + v_BlockSize) + "" ,(v_YIndex + v_BlockSize) + "");
                        v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                        if ( v_CVPTemp != null && v_CVPTemp != v_CVP )
                        {
                            v_CVP.union(v_CVPTemp);
                        }
                        v_CVPointMap.putRow((v_XIndex + v_BlockSize) + "" ,(v_YIndex + v_BlockSize) + "" ,v_PKey);
                        
                        
                        v_PKeyTemp = v_CVPointMap.getRow(v_XIndex + "" ,(v_YIndex + v_BlockSize) + "");
                        v_CVPTemp  = v_PKeyTemp == null ? null : v_CVPoints.get(v_PKeyTemp);
                        if ( v_CVPTemp != null && v_CVPTemp != v_CVP )
                        {
                            v_CVP.union(v_CVPTemp);
                        }
                        v_CVPointMap.putRow(v_XIndex + "" ,(v_YIndex + v_BlockSize) + "" ,v_PKey);
                    }
                }
//                Imgproc.rectangle(v_MMask ,v_CVP.toRect() ,v_MMaskColor ,1);
//                HighGui.imshow("dddddd" ,v_MMask);
//                HighGui.waitKey(0);
            }
        }
        
        
        List<CVPoint> v_CVPointList = Help.toList(v_CVPoints);
        Help.toDistinct(v_CVPointList ,"rectArea DESC");
        
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
            }
        }
        
        OpenCV.merge(v_CVPointList);

        
//        Mat v_Temp = i_M2.clone();
//        OpenCV.drawBlocks(v_Temp ,v_BlockWCount ,v_BlockHCount ,v_BlockSize ,v_BlockLineColor);
//        Scalar v_Color = new Scalar(0 ,0 ,255);
//        for (CVPoint v_CVPoint : v_CVPointList)
//        {
//            Imgproc.rectangle(v_Temp ,v_CVPoint.ceil(v_BlockSize).toRect() ,v_Color ,2);
//        }
//        HighGui.imshow("dddddd" ,v_Temp);
//        HighGui.waitKey(0);
        
        return v_CVPointList;
    }
}
