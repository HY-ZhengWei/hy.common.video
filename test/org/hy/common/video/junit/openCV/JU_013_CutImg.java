package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;





/**
 * 测试单元：剪切图片\裁剪图片
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-03
 * @version     v1.0
 */
public class JU_013_CutImg
{
    private static final Logger $Logger = new Logger(JU_013_CutImg.class ,true);
    
    
    
    public JU_013_CutImg()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void cutImg()
    {
        Mat v_MSource = Imgcodecs.imread(JU_013_CutImg.class.getResource("JU_010_Harris.jpg").getFile().substring(1));
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        int  v_ImgWidth  = 1120 - 920;
        int  v_ImgHeight = 710  - 490;
        int  v_ImgStartX = 920;
        int  v_ImgStartY = 490;
        Rect v_Rect      = new Rect(v_ImgStartX ,v_ImgStartY ,v_ImgWidth ,v_ImgHeight);
        Mat  v_imgMat    = new Mat(v_ImgWidth, v_ImgHeight, CvType.CV_8UC3);
        Mat  v_imgMatROI = new Mat(v_MSource ,v_Rect);
        
        v_imgMatROI.copyTo(v_imgMat);  // 从ROI中剪切图片
        
        HighGui.imshow("剪切图片" ,v_imgMat);
        HighGui.waitKey(0);
        
        $Logger.info(v_imgMat);
    }
    
}