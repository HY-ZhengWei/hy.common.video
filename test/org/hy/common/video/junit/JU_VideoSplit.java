package org.hy.common.video.junit;

import org.hy.common.video.VideoHelp;
import org.junit.Test;





public class JU_VideoSplit
{
    
    @Test
    public void split()
    {
        VideoHelp.$FFMpegHome = "C:\\WorkSpace\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.splits("E:\\WSS\\文档库\\WZYB-720x480.mp4"
                        ,"E:\\"
                        ,50
                        ,5);
    }
    
}
