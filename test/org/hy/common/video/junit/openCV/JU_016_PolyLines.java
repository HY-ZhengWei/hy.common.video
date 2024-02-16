package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.video.OpenCV;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：多边形
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-11
 * @version     v1.0
 */
@Xjava(value=XType.XML)
public class JU_016_PolyLines
{
    private static final Logger               $Logger          = new Logger(JU_016_PolyLines.class ,true);
    
    private static       boolean              $IsInit          = false;
    
    /** 红色警示区 */
    private static       Map<Integer ,Scalar>  $RedAreaMap     = new HashMap<Integer ,Scalar>();
                                               
    /** 红色警示区的标线 */
    private static       List<MatOfPoint>      $RedAreaPoints  = new ArrayList<MatOfPoint>();
    
    
    
    public JU_016_PolyLines() throws Exception
    {
        if ( !$IsInit )
        {
            $IsInit = true;
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            XJava.parserAnnotation(this.getClass().getName());
            
            Param v_RedArea = XJava.getParam("RedArea");
            if ( Help.isNull(v_RedArea.getValue()) )
            {
                return;
            }
            
            // 初始化已确定的红色区域
            String [] v_RedAreas     = StringHelp.replaceAll(v_RedArea.getValue() ,new String[] {"\r" ,"\n" ,"\t" ," "} ,StringHelp.$ReplaceNil).split(",");
            Scalar    v_RedLineColor = new Scalar(0   ,0   ,255);
            for (String v_Value : v_RedAreas)
            {
                $RedAreaMap.put(Integer.parseInt(v_Value) ,v_RedLineColor);
            }
            
            Param      v_RedAreaPoint  = XJava.getParam("RedAreaPoint");
            String []  v_RedAreaPoints = StringHelp.replaceAll(v_RedAreaPoint.getValue() ,new String[] {"\r" ,"\n" ,"\t" ," "} ,StringHelp.$ReplaceNil).split(",");
            Point  []  v_PointList     = new Point[v_RedAreaPoints.length / 2];
            for (int x=0 ,y=0; x<v_RedAreaPoints.length; x=x+2 ,y++)
            {
                v_PointList[y] = new Point(Integer.parseInt(v_RedAreaPoints[x]) ,Integer.parseInt(v_RedAreaPoints[x+1]));
            }
            $RedAreaPoints.add(new MatOfPoint(v_PointList));
        }
    }
    
     
    
    @Test
    public void showImage()
    {
        Mat v_MTarget = Imgcodecs.imread(JU_016_PolyLines.class.getResource("JU_016_PolyLines_T1N151.jpg").getFile().substring(1));
        
        int      v_Width          = v_MTarget.cols();        // 列数是宽度
        int      v_Height         = v_MTarget.rows();        // 行数是高度
        int      v_BlockSize      = 16;                      // 块大小
        int      v_BlockWCount    = v_Width  / v_BlockSize;  // 宽度上分割的块数
        int      v_BlockHCount    = v_Height / v_BlockSize;  // 高度上分割的块数
        Scalar   v_BlockLineColor = new Scalar(255 ,255 ,255);
        OpenCV.drawBlocks(v_MTarget ,v_BlockWCount ,v_BlockHCount ,v_BlockSize ,v_BlockLineColor);
        
        for (int v_HIndex=0; v_HIndex<v_BlockHCount; v_HIndex++)
        {
            for (int v_WIndex=0; v_WIndex<v_BlockWCount; v_WIndex++)
            {
                int    v_BlockNo = v_HIndex * v_BlockWCount + v_WIndex;
                Scalar v_Color   = $RedAreaMap.get(v_BlockNo);
                
                if ( v_Color != null )
                {
                    Imgproc.rectangle(v_MTarget ,new Rect(v_WIndex * v_BlockSize ,v_HIndex * v_BlockSize ,v_BlockSize ,v_BlockSize) ,v_Color ,2);
                }
            }
        }
        
        HighGui.imshow("红色区域" ,v_MTarget);
        HighGui.waitKey(0);
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void polyLines()
    {
        Mat v_MTarget = Imgcodecs.imread(JU_016_PolyLines.class.getResource("JU_016_PolyLines_T1N151.jpg").getFile().substring(1));
        
        Imgproc.polylines(v_MTarget ,$RedAreaPoints ,true ,OpenCV.$Color_Red ,1);
        HighGui.imshow("红色区域" ,v_MTarget);
        HighGui.waitKey(0);
        
        int      v_Width          = v_MTarget.cols();        // 列数是宽度
        int      v_Height         = v_MTarget.rows();        // 行数是高度
        int      v_BlockSize      = 16;                      // 块大小
        int      v_BlockWCount    = v_Width  / v_BlockSize;  // 宽度上分割的块数
        int      v_BlockHCount    = v_Height / v_BlockSize;  // 高度上分割的块数
        Scalar   v_BlockLineColor = new Scalar(255 ,255 ,255);
        Scalar   v_RedLineColor   = new Scalar(0   ,0   ,255);
        Scalar   v_GreenLineColor = new Scalar(0   ,255 ,0);
        OpenCV.drawBlocks(v_MTarget ,v_BlockWCount ,v_BlockHCount ,v_BlockSize ,v_BlockLineColor);
        
        // 先绘制已标记的块
        for (int v_HIndex=0; v_HIndex<v_BlockHCount; v_HIndex++)
        {
            for (int v_WIndex=0; v_WIndex<v_BlockWCount; v_WIndex++)
            {
                int    v_BlockNo   = v_HIndex * v_BlockWCount + v_WIndex;
                Scalar v_FlagColor = $RedAreaMap.get(v_BlockNo);
                
                if ( v_FlagColor != null )
                {
                    Imgproc.rectangle(v_MTarget ,new Rect(v_WIndex * v_BlockSize ,v_HIndex * v_BlockSize ,v_BlockSize ,v_BlockSize) ,v_FlagColor ,2);
                }
            }
        }
        
        // 再人为控制选择哪些块要被标记
        int v_WStart = 0;
        for (int v_HIndex=0; v_HIndex<v_BlockHCount; v_HIndex++)
        {
            for (int v_WIndex=v_WStart; v_WIndex<v_BlockWCount; v_WIndex++)
            {
                Mat v_MTemp = v_MTarget.clone();
                Imgproc.rectangle(v_MTemp ,new Rect(v_WIndex * v_BlockSize ,v_HIndex * v_BlockSize ,v_BlockSize ,v_BlockSize) ,v_GreenLineColor ,2);
                HighGui.imshow("上下左右 1 or 2" ,v_MTemp);
                int v_Key = HighGui.waitKey(0);
                
                if ( v_Key == 49 )        // 1 确认
                {
                    int v_BlockNo = v_HIndex * v_BlockWCount + v_WIndex;
                    $RedAreaMap.put(v_BlockNo ,v_RedLineColor);
                    Imgproc.rectangle(v_MTarget ,new Rect(v_WIndex * v_BlockSize ,v_HIndex * v_BlockSize ,v_BlockSize ,v_BlockSize) ,v_RedLineColor ,1);
                }
                else if ( v_Key == 50 )   // 2 取消
                {
                    int v_BlockNo = v_HIndex * v_BlockWCount + v_WIndex;
                    $RedAreaMap.remove(v_BlockNo);
                    Imgproc.rectangle(v_MTarget ,new Rect(v_WIndex * v_BlockSize ,v_HIndex * v_BlockSize ,v_BlockSize ,v_BlockSize) ,v_BlockLineColor ,1);
                }
                else if ( v_Key == 38 )   // 上
                {
                    v_HIndex--;
                    v_HIndex--;
                    v_WStart = v_WIndex;
                    break;
                }
                else if ( v_Key == 40 )   // 下
                {
                    v_WStart = v_WIndex;
                    break;
                }
                else if ( v_Key == 37 )   // 左
                {
                    v_HIndex--;
                    v_WStart = v_WIndex - 1;
                    break;
                }
                else if ( v_Key == 39 )   // 右
                {
                    continue;
                }
            }
        }
        
        Help.print(Help.toSort(Help.toListKeys($RedAreaMap)));
        
        HighGui.imshow("多边形" ,v_MTarget);
        HighGui.waitKey(0);
        $Logger.info(v_MTarget);
    }
    
}