package org.hy.common.video.junit.openCV;

import org.hy.common.Help;
import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：图像处理
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-27
 * @version     v1.0
 */
public class JU_002_ReadImage
{
    private static final Logger $Logger = new Logger(JU_002_ReadImage.class ,true);
    
    
    
    public JU_002_ReadImage()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void readImage()
    {
        // 读取图片
        /*
         * IMREAD_UNCHANGED = -1 ：不进行转化，比如保存为了16位的图片，读取出来仍然为16位。
         * IMREAD_GRAYSCALE = 0 ：进行转化为灰度图，比如保存为了16位的图片，读取出来为8位，类型为CV_8UC1。
         * IMREAD_COLOR = 1 ：进行转化为三通道图像。
         * IMREAD_ANYDEPTH = 2 ：如果图像深度为16位则读出为16位，32位则读出为32位，其余的转化为8位。
         * IMREAD_ANYCOLOR = 4 ：图像以任何可能的颜色格式读取
         * IMREAD_LOAD_GDAL = 8 ：使用GDAL驱动读取文件，GDAL(Geospatial Data Abstraction
         * Library)是一个在X/MIT许可协议下的开源栅格空间数据转换库。它利用抽象数据模型来表达所支持的各种文件格式。它还有一系列命令行工具来进行数据转换和处理。
         */
        Mat v_Mat = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        HighGui.imshow("标题" ,v_Mat);           // 在屏幕上显示图像
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat);
    }
    
    
    
    @Test
    public void readImage_threshold()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图显示" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_RGB2GRAY);   // 灰度图
        
        HighGui.imshow("灰度图显示" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.threshold(v_MTarget ,v_MTarget ,125 ,255 ,Imgproc.THRESH_BINARY);    // 二值化：大于125的值变成255，否则变成0
        /*
            Imgproc.THRESH_BINARY：当像素值超过闽值thresh时取 maxval，否则取0；
            Imgproc.THRESH BINARY INV:Imgproc.THRESH BINARY的反转；
            Imgproc.THRESH_TRUNC：大于阀值时设为阀值，否则不变；
            Imgproc.IHRESHIOZERO：大于阈估时不变，否则设为0；
            Imgproc.THRESH_TOZERO_INV:Imgproc.THRESH_TOZERO的反转；
         */
        
        HighGui.imshow("二值化显示" ,v_MTarget);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_bitwise_not()
    {
        Mat v_Mat01 = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_Mat02 = new Mat();
        
        Core.bitwise_not(v_Mat01 ,v_Mat02);      // 反相：底片的效果
        
        HighGui.imshow("反相" ,v_Mat02);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat02);
    }
    
    
    
    @Test
    public void readImage_pyrDown()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.pyrDown(v_MSource ,v_MTarget);   // 向下采样
        HighGui.imshow("向下采样1" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.pyrDown(v_MTarget ,v_MTarget);   // 向下采样
        HighGui.imshow("向下采样2" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.pyrDown(v_MTarget ,v_MTarget);   // 向下采样
        HighGui.imshow("向下采样3" ,v_MTarget);
        HighGui.waitKey(0);
        
        
        Imgproc.pyrUp(v_MTarget ,v_MTarget);     // 向上采样
        HighGui.imshow("向上采样1" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.pyrUp(v_MTarget ,v_MTarget);     // 向上采样
        HighGui.imshow("向上采样2" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.pyrUp(v_MTarget ,v_MTarget);     // 向上采样
        HighGui.imshow("向上采样3" ,v_MTarget);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_拉普拉斯金字塔()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.pyrDown(v_MSource ,v_MTarget);   // 向下采样
        HighGui.imshow("向下采样" ,v_MTarget);
        HighGui.waitKey(0);
        
        Imgproc.pyrUp(v_MTarget ,v_MTarget);     // 向上采样
        HighGui.imshow("向上采样" ,v_MTarget);
        HighGui.waitKey(0);
        
        Mat v_SubMat = new Mat(v_MSource.size() ,CvType.CV_64F);
        Core.subtract(v_MTarget ,v_MSource ,v_SubMat);
        HighGui.imshow("拉普拉斯金字塔" ,v_SubMat);      // 采样图形轮廓
        HighGui.waitKey(0);
        
        Imgproc.dilate(v_SubMat ,v_SubMat ,new Mat());   // 膨胀
        HighGui.imshow("拉普拉斯金字塔:膨胀" ,v_SubMat);
        HighGui.waitKey(0);
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_medianBlur()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.medianBlur(v_MSource ,v_MTarget ,5);   // 椒盐噪声
        
        HighGui.imshow("椒盐噪声" ,v_MTarget);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_GaussianBlur()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.GaussianBlur(v_MSource ,v_MTarget ,new Size(13 ,13) ,10 ,10);   // 高斯噪声。卷积核13越小越清晰
        
        HighGui.imshow("高斯噪声" ,v_MTarget);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_blur()
    {
        Mat v_MSource = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        HighGui.imshow("原图" ,v_MSource);
        HighGui.waitKey(0);
        
        Imgproc.blur(v_MSource ,v_MTarget ,new Size(5 ,5));   // 均值滤波
        
        HighGui.imshow("均值滤波" ,v_MTarget);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_MTarget);
    }
    
    
    
    @Test
    public void readImage_getRotationMatrix2D()
    {
        Mat   v_Mat01  = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat   v_Mat02  = v_Mat01.clone();
        Point v_Center = new Point(v_Mat01.width() / 2.0D ,v_Mat01.height() / 2.0D);
        Mat   v_AffineTrans = Imgproc.getRotationMatrix2D(v_Center ,30 ,0.5F);            // 按v_Center点旋转30度，缩放0.5
        
        Imgproc.warpAffine(v_Mat01 ,v_Mat02 ,v_AffineTrans ,v_Mat02.size() ,Imgproc.INTER_NEAREST);  // 应用变换
        
        HighGui.imshow("旋转 & 缩放" ,v_Mat02);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat02);
    }
    
    
    
    @Test
    public void readImage_getAffineTransform()
    {
        Mat      v_Mat01   = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1));
        Mat      v_Mat02   = new Mat();
        Point [] v_PSource = new Point[3];
        Point [] v_PTarget = new Point[3];
        
        v_PSource[0] = new Point(0               ,0);
        v_PSource[1] = new Point(v_Mat01.width() ,0);
        v_PSource[2] = new Point(0               ,v_Mat01.height());
        
        v_PTarget[0] = v_PSource[0];
        v_PTarget[1] = v_PSource[1];
        v_PTarget[2] = new Point(v_Mat01.width() / 2.0D ,v_Mat01.height());
        
        Mat v_Affine = Imgproc.getAffineTransform(new MatOfPoint2f(v_PSource) ,new MatOfPoint2f(v_PTarget));  // 仿射变换
        
        Imgproc.warpAffine(v_Mat01 ,v_Mat02 ,v_Affine ,v_Mat02.size());                                       // 应用变换
        
        HighGui.imshow("仿射变换" ,v_Mat02);
        HighGui.waitKey(0);                      // 等待X毫秒；0表示：任意键退出
        
        $Logger.info(v_Mat02);
    }
    
    
    
    @Test
    public void writeImage()
    {
        // 读取图片，并按灰度图读取
        Mat v_Mat = Imgcodecs.imread(JU_002_ReadImage.class.getResource("JU_002_ReadImage.jpg").getFile().substring(1) ,Imgcodecs.IMREAD_GRAYSCALE);
        String v_SaveFile = Help.getClassHomePath()  + "new.jpg";  // 注意：文件名称，不支持中文
        Imgcodecs.imwrite(v_SaveFile ,v_Mat);
        
        $Logger.info("灰度图保存路径：" + v_SaveFile);
        $Logger.info(v_Mat);
    }
    
}
