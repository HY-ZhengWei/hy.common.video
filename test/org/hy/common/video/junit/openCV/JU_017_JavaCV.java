package org.hy.common.video.junit.openCV;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;




public class JU_017_JavaCV
{
    
    @Test
    public void helloWorld()
    {
        Mat v_M1 = new Mat();
        Mat v_M2 = new Mat();
        opencv_imgproc.cvtColor(v_M1 ,v_M2 ,opencv_imgproc.CV_BGR2GRAY);
    }
    
}
