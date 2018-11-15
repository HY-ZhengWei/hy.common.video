package org.hy.common.video;

import org.junit.Test;





public class JU_VideoSplit
{
    
    @Test
    public void split()
    {
        VideoHelp.$FFMpegHome = "D:\\WorkSpace_SearchDesktop\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoHelp.splits("D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\outputPath\\WZYB-1080-720.mp4" 
                        ,"D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\outputPath" 
                        ,50 
                        ,5);
    }
    
}
