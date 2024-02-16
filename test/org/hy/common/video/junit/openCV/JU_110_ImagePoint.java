package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.TablePartition;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





public class JU_110_ImagePoint
{
    
    private static final Logger $Logger = new Logger(JU_110_ImagePoint.class ,true);
    
    
    
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    public void imagePoint()
    {
        
    }
    
    
    
    @Test
    public void test_ImagePoint()
    {
        Scalar v_BGColor = new Scalar(255 ,255 ,255);   // 背景色
        Scalar v_XColor  = new Scalar(0   ,0   ,255);   // X轴线的颜色
        Scalar v_YColor  = new Scalar(0   ,255 ,0);     // Y轴线的颜色
        Mat    v_MSource = Imgcodecs.imread("C:\\WorkSpace\\hy.common.video\\test\\org\\hy\\common\\video\\junit\\openCV\\JU_110_ImagePoint02.png");
        Mat    v_MTarget = new Mat();
        
        Imgproc.cvtColor(v_MSource  ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);          // 注意：BGR，不是RGB
        Imgproc.threshold(v_MTarget ,v_MTarget ,127 ,255 ,Imgproc.THRESH_BINARY); // 二值化：大于125的值变成255，否则变成0
        
        // 获取轮廓
        List<MatOfPoint> v_Contours  = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Mat              v_Hierarchy = new Mat();
        Imgproc.findContours(v_MTarget ,v_Contours ,v_Hierarchy ,Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        // 绘制轮廓
        Mat v_ContoursTarget = new Mat(v_MTarget.height() ,v_MTarget.width() ,CvType.CV_8SC3 ,v_BGColor);
        for (int x=0; x<v_Contours.size(); x++)
        {
            Imgproc.drawContours(v_ContoursTarget ,v_Contours ,x ,v_XColor ,1);
        }
        
        HighGui.imshow("轮廓识别：findContours" ,v_ContoursTarget);
        HighGui.waitKey(0);
        
        
        TablePartition<Integer ,Integer> v_AxisXMap = new TablePartition<Integer ,Integer>();
        int v_Width  = v_MTarget.width();
        int v_Height = v_MTarget.height();
        for (int v_X=0; v_X<v_Width; v_X++)
        {
            for (int v_Y=0; v_Y<v_Height; v_Y++)
            {
                double [] v_ColorValue = v_MTarget.get(v_X ,v_Y);
                
                if ( !Help.isNull(v_ColorValue) && v_ColorValue[0] == 0 )
                {
                    v_AxisXMap.putRow(v_X ,v_Y);
                }
            }
        }
        
        for (Integer v_X : v_AxisXMap.keySet())
        {
            List<Integer> v_AxisY = v_AxisXMap.get(v_X);
            v_AxisY = Help.toSort(v_AxisY);
            
            int v_OldY      = -1;
            int v_LineStart = -1;
            int v_LineSize  = 0;
            int v_Y         = -1;
            for (int i=0; i<v_AxisY.size(); i++)
            {
                v_Y = v_AxisY.get(i);
                if ( v_OldY == -1 )
                {
                    v_OldY      = v_Y;
                    v_LineStart = v_Y;
                }
                else if ( v_Y - v_OldY > 5 )
                {
                    if ( v_LineSize >= 5  )
                    {
                        Point v_PStart = new Point();
                        Point v_PEnd   = new Point();
                        
                        v_PStart.x = v_X;
                        v_PStart.y = v_LineStart;
                        
                        v_PEnd.x = v_X;
                        v_PEnd.y = v_Y;
                        
                        Imgproc.line(v_MSource ,v_PStart ,v_PEnd ,v_YColor);
                        HighGui.imshow("画Y轴线" ,v_MSource);
                        HighGui.waitKey(0);
                        $Logger.info(".." + v_LineSize);
                    }
                    
                    v_LineStart = v_Y;
                    v_LineSize  = 0;
                }
                else
                {
                    v_LineSize++;
                    v_OldY = v_Y;
                }
            }
            
            if ( v_LineSize >= 10  )
            {
                Point v_PStart = new Point();
                Point v_PEnd   = new Point();
                
                v_PStart.x = v_X;
                v_PStart.y = v_LineStart;
                
                v_PEnd.x = v_X;
                v_PEnd.y = v_Y;
                
                Imgproc.line(v_MSource ,v_PStart ,v_PEnd ,v_YColor);
                HighGui.imshow("画Y轴线" ,v_MSource);
                HighGui.waitKey(0);
                $Logger.info(".." + v_LineSize);
            }
        }
        
        HighGui.imshow("最终效果" ,v_MSource);
        HighGui.waitKey(0);
    }
    
}
