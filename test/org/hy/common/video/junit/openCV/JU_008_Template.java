package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：模块匹配
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-29
 * @version     v1.0
 */
public class JU_008_Template
{
    private static final Logger $Logger = new Logger(JU_008_Template.class ,true);
    
    
    
    public JU_008_Template()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void matchTemplate()
    {
        Mat v_MSource   = Imgcodecs.imread(JU_008_Template.class.getResource("JU_008_Template_Big.jpg"  ).getFile().substring(1));
        Mat v_MTemplate = Imgcodecs.imread(JU_008_Template.class.getResource("JU_008_Template_Small.jpg").getFile().substring(1));
        Mat v_MTarget   = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        HighGui.imshow("要匹配的模板" ,v_MTemplate);
        HighGui.waitKey(0);
        
        Imgproc.matchTemplate(v_MSource ,v_MTemplate ,v_MTarget ,Imgproc.TM_CCOEFF);   // 匹配结果
        
        Core.MinMaxLocResult v_MinMax   = Core.minMaxLoc(v_MTarget);                   // 获取最小与最大边界
        Point                v_BeginLoc = v_MinMax.maxLoc;                             // TM_CCOEFF模式取最大值
        Point                v_EndLoc   = new Point(v_BeginLoc.x + v_MTemplate.cols() ,v_BeginLoc.y + v_MTemplate.rows());
        Scalar               v_Color    = new Scalar(0 ,0 ,255);                       // 边框颜色
        
        
        Imgproc.rectangle(v_MSource ,v_BeginLoc ,v_EndLoc ,v_Color ,2);
        HighGui.imshow("原图边出匹配的结果" ,v_MSource);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
}