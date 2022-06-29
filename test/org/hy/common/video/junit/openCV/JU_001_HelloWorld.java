package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;





/**
 * 测试单元：第一个启动成功的程序
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-27
 * @version     v1.0
 */
public class JU_001_HelloWorld
{
    private static final Logger $Logger = new Logger(JU_001_HelloWorld.class ,true);
    
    
    
    public JU_001_HelloWorld()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void helloWorld()
    {
        Mat v_Mat = Mat.eye(3 ,3 ,CvType.CV_8UC1);   // 3 * 3 的矩阵
        /*
        CV 8UC1：8位Unsigned（取值范围0-255）；Channel（通道）-1（灰应图）；
        CV8UC3：8位Unsigned；Channel-3（彩色图像）；
        CV_64FC3：64位浮点数；Channel=3（彩色图像）；
        */
        
        $Logger.info(v_Mat);
        $Logger.info("\n" + v_Mat.dump());                            // 内存细节
        $Logger.info("\n" + Mat.zeros(3 ,3 ,CvType.CV_8UC1).dump());  // 内存细节：全零
        $Logger.info("\n" + Mat.ones (3 ,3 ,CvType.CV_8UC1).dump());  // 内存细节：全1
        $Logger.info("\n" + Mat.eye  (3 ,3 ,CvType.CV_8SC4).dump());  // 内存细节：RGBA
    }
    
}
