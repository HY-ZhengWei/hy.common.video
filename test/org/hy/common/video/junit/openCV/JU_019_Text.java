package org.hy.common.video.junit.openCV;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.hy.common.video.OpenCV;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;





/**
 * 测试单元：文字输出
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-19
 * @version     v1.0
 */
public class JU_019_Text
{
    
    private static final Logger $Logger = new Logger(JU_019_Text.class ,true);
    
    
    
    public JU_019_Text()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void text()
    {
        Mat v_Mat = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_016_PolyLines.jpg").getFile().substring(1));
        HighGui.imshow("原图" ,v_Mat);
        HighGui.waitKey(0);
        
        BufferedImage v_BImage = OpenCV.matToJava(v_Mat ,".jpg");
        
        Font font = new Font("微软雅黑", Font.PLAIN, 24);
        Graphics2D g = v_BImage.createGraphics();
        g.drawImage(v_BImage, 0, 0, v_BImage.getWidth() ,v_BImage.getHeight(), null);
        g.setFont(font);              //设置字体
        
        // 设置水印的坐标
        Color v_FontColor = new Color(0 ,255 ,0);
        g.setColor(v_FontColor);
        g.drawString("中文水印" ,100, 100);
        g.dispose();
        
        Mat v_MTarget = OpenCV.javaToMat(v_BImage ,BufferedImage.TYPE_3BYTE_BGR ,CvType.CV_8UC3);
        HighGui.imshow("中文水印" ,v_MTarget);
        HighGui.waitKey(0);
    }
    
    
    
    @Test
    public void test02()
    {
        Mat   v_Mat       = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_016_PolyLines.jpg").getFile().substring(1));
        Font  v_Font      = new Font("微软雅黑", Font.PLAIN, 24);
        Color v_FontColor = new Color(0 ,255 ,0);
        
        OpenCV.putText(v_Mat ,".jpg" ,100  ,100 ,"中文水印" ,v_Font ,v_FontColor);
        
        HighGui.imshow("中文水印" ,v_Mat);
        HighGui.waitKey(0);
    }
    
}
