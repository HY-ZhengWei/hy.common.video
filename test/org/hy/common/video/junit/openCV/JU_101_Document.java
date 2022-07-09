package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：文档照片的处理
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-30
 * @version     v1.0
 */
public class JU_101_Document
{
    private static final Logger $Logger = new Logger(JU_101_Document.class ,true);
    
    
    /** 每次处理后的Mat历史列表 */
    private Map<String ,Mat> matHistorys;
    
    
    
    public JU_101_Document()
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
    public void document()
    {
        Mat v_MSource = Imgcodecs.imread(JU_101_Document.class.getResource("JU_101_Document.jpg").getFile().substring(1));
        Mat v_MStart  = null;
        Mat v_MEnd    = null;
        
        int    v_Height = 1000;  // 缩放后的高度
        double v_Scale  = Help.division(v_Height ,v_MSource.height());
        int    v_Width  = (int)(v_MSource.width() * v_Scale);
        
        v_MStart = v_MSource;
        v_MEnd   = newMat("缩放原图");
        Imgproc.resize(v_MStart ,v_MEnd ,new Size(v_Width ,v_Height));
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("灰度图");
        Imgproc.cvtColor(v_MStart ,v_MEnd ,Imgproc.COLOR_BGR2GRAY);
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("高斯噪声");
        Imgproc.GaussianBlur(v_MStart ,v_MEnd ,new Size(5 ,5) ,0 ,0);  // 高斯噪声。卷积核5越小越清晰
        
        v_MStart = v_MEnd;
        v_MEnd   = newMat("边缘检测");
        Imgproc.Canny(v_MStart ,v_MEnd ,60 ,200);
        HighGui.imshow("缩放原图" ,getMatHistory("缩放原图"));
        HighGui.imshow("边缘检测" ,getMatHistory("边缘检测"));
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        
        
        List<MatOfPoint> v_Contours = new ArrayList<MatOfPoint>();  // 轮廓结果集
        Imgproc.findContours(v_MEnd ,v_Contours ,new Mat() ,Imgproc.RETR_LIST ,Imgproc.CHAIN_APPROX_SIMPLE);
        
        int v_WidthMin  = 0;
        int v_WidthMax  = 0;
        int v_HeightMin = 0;
        int v_HeightMax = 0;
        for (int x=0; x<v_Contours.size(); x++)
        {
            MatOfPoint   v_P   = v_Contours.get(x);
            MatOfPoint2f v_P2f = new MatOfPoint2f(v_P);
            
            double v_Peri = Imgproc.arcLength(v_P2f ,true);   // 计算轮廓近似值
            MatOfPoint2f v_P2fRet = new MatOfPoint2f();
            Imgproc.approxPolyDP(v_P2f ,v_P2fRet ,0.02 * v_Peri ,true);
            
            
            v_WidthMax  = Math.min(v_WidthMin  ,v_P.width());
            v_WidthMax  = Math.max(v_WidthMax  ,v_P.width());
            v_HeightMin = Math.min(v_HeightMin ,v_P.height());
            v_HeightMax = Math.max(v_HeightMax ,v_P.height());
        }
        
        Point  v_PLeftTop     = new Point(v_WidthMin ,v_HeightMin);
        Point  v_PRightTop    = new Point(v_WidthMax ,v_HeightMin);
        Point  v_PLeftBotton  = new Point(v_WidthMin ,v_HeightMax);
        Point  v_PRightBotton = new Point(v_WidthMax ,v_HeightMax);
        Scalar v_Color        = new Scalar(255 ,0 ,0);
        
        Imgproc.line(v_MEnd ,v_PLeftTop     ,v_PRightTop    ,v_Color ,2);
        Imgproc.line(v_MEnd ,v_PRightTop    ,v_PRightBotton ,v_Color ,2);
        Imgproc.line(v_MEnd ,v_PRightBotton ,v_PLeftBotton  ,v_Color ,2);
        Imgproc.line(v_MEnd ,v_PLeftBotton  ,v_PLeftTop     ,v_Color ,2);
        
        HighGui.imshow("绘制轮廓" ,v_MEnd);
        HighGui.waitKey(0);
        $Logger.info(v_MSource);
    }
    
}