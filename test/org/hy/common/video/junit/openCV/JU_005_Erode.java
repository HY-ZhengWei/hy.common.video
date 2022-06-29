package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：腐蚀 & 膨胀
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-27
 * @version     v1.0
 */
public class JU_005_Erode
{
    private static final Logger $Logger = new Logger(JU_005_Erode.class ,true);
    
    
    
    public JU_005_Erode()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void erode() throws InterruptedException
    {
        Mat v_MSource = Imgcodecs.imread(JU_005_Erode.class.getResource("JU_005_Erode.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.erode(v_MSource ,v_MTarget ,new Mat());    // 腐蚀
        
        HighGui.imshow("腐蚀" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void dilate() throws InterruptedException
    {
        Mat v_MSource = Imgcodecs.imread(JU_005_Erode.class.getResource("JU_005_Erode.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.dilate(v_MSource ,v_MTarget ,new Mat());   // 膨胀
        
        HighGui.imshow("膨胀" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void morphologyEx_Open() throws InterruptedException
    {
        Mat v_MSource = Imgcodecs.imread(JU_005_Erode.class.getResource("JU_005_Erode.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        // -1 ,-1 表示默认值，从中心点为原点
        Imgproc.morphologyEx(v_MSource ,v_MTarget ,Imgproc.MORPH_OPEN ,new Mat() ,new Point(-1 ,-1) ,7);   // 形态学：开运算：先腐蚀，后膨胀。迭代7次
        
        HighGui.imshow("形态学：开运算" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void morphologyEx_GRADIENT() throws InterruptedException
    {
        Mat v_MSource = Imgcodecs.imread(JU_005_Erode.class.getResource("JU_005_Erode.png").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        // -1 ,-1 表示默认值，从中心点为原点
        Imgproc.morphologyEx(v_MSource ,v_MTarget ,Imgproc.MORPH_GRADIENT ,new Mat() ,new Point(-1 ,-1) ,3);   // 形态学：梯度：轮廓线
        
        HighGui.imshow("形态学：梯度" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
}
