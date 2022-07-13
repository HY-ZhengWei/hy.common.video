package org.hy.common.video.junit.openCV;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;





/**
 * 测试单元：背景差分法
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-13
 * @version     v1.0
 */
public class JU_018_BackgroundSubtractor
{
    private static final Logger $Logger = new Logger(JU_018_BackgroundSubtractor.class ,true);
    
    
    
    public JU_018_BackgroundSubtractor()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void backgroundSubtractor()
    {
        BackgroundSubtractor v_BGSubtractor = Video.createBackgroundSubtractorMOG2();
        
        VideoCapture v_Capture = new VideoCapture(JU_018_BackgroundSubtractor.class.getResource("JU_107_Move.flv").getFile().substring(1));
        Mat          v_Frame   = new Mat();
        Mat          v_FGMask  = new Mat();     // 屏蔽背景，提取出来的前景
        
        try
        {
            while ( true )
            {
                boolean v_IsRead = v_Capture.read(v_Frame);
                
                if ( !v_IsRead || v_Frame.empty() )
                {
                    break;
                }
                
                v_BGSubtractor.apply(v_Frame ,v_FGMask);
                HighGui.imshow("当前帧" ,v_Frame);
                HighGui.imshow("前景图" ,v_FGMask);
                int v_Key = HighGui.waitKey(0);
                if ( v_Key == 27 ) { break; }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);;
        }
        
        v_Capture.release();
    }
    
}
