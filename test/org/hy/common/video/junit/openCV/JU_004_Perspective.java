package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：透视转换
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-27
 * @version     v1.0
 */
public class JU_004_Perspective
{
    private static final Logger $Logger = new Logger(JU_004_Perspective.class ,true);
    
    
    
    public JU_004_Perspective()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void perspective()
    {
        // 读取图片
        Mat      v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_004_Perspective.png").getFile().substring(1));
        Mat      v_MTarget = new Mat();
        Point [] v_PSource = new Point[4];
        Point [] v_PTarget = new Point[4];
        
        v_PSource[0] = new Point(40  ,40);    // 顺时针旋转数点的位置。顺序与点2保持一样即可，都是逆时针也行
        v_PSource[1] = new Point(930 ,260);
        v_PSource[2] = new Point(930 ,710);
        v_PSource[3] = new Point(40  ,930);
        
        v_PTarget[0] = new Point(0   ,0);    // 顺时针旋转数点的位置
        v_PTarget[1] = new Point(900 ,0);
        v_PTarget[2] = new Point(900 ,900);
        v_PTarget[3] = new Point(0   ,900);
        
        Mat v_Perspective = Imgproc.getPerspectiveTransform(new MatOfPoint2f(v_PSource) ,new MatOfPoint2f(v_PTarget));  // 获取透视转换矩阵
        Imgproc.warpPerspective(v_MSource ,v_MTarget ,v_Perspective ,new Size(900 ,900));
        
        HighGui.imshow("标题" ,v_MTarget);       // 在屏幕上显示图像
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_MSource);
    }
    
}
