package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;





/**
 * 测试单元：两图相加
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-27
 * @version     v1.0
 */
public class JU_003_ImageAdd
{
    private static final Logger $Logger = new Logger(JU_003_ImageAdd.class ,true);
    
    
    
    public JU_003_ImageAdd()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void addWeighted()
    {
        // 读取图片
        Mat v_Mat01 = Imgcodecs.imread(JU_003_ImageAdd.class.getResource("JU_003_ImageAdd_A.png").getFile().substring(1));
        Mat v_Mat02 = Imgcodecs.imread(JU_003_ImageAdd.class.getResource("JU_003_ImageAdd_B.png").getFile().substring(1));
        Mat v_Mat03 = new Mat();
        
        Core.addWeighted(v_Mat01 ,0.5 ,v_Mat02 ,0.5 ,0 ,v_Mat03);   // 两AB两图加权拼成一幅图， AB两均取0.5的加权（透明度0.5）
        HighGui.imshow("标题" ,v_Mat03);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat03);
    }
    
    
    
    @Test
    public void add()
    {
        // 读取图片
        Mat v_Mat01 = Imgcodecs.imread(JU_003_ImageAdd.class.getResource("JU_003_ImageAdd_A.png").getFile().substring(1));
        Mat v_Mat02 = Imgcodecs.imread(JU_003_ImageAdd.class.getResource("JU_003_ImageAdd_B.png").getFile().substring(1));
        Mat v_Mat03 = new Mat();
        
        Core.add(v_Mat01 ,v_Mat02 ,v_Mat03);     // 两AB两图拼成一幅图。255+255=255，超出最大值，取最大取
        HighGui.imshow("标题" ,v_Mat03);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat03);
    }
    
}
