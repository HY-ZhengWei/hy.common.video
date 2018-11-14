package org.hy.common.video;

import org.junit.Test;





public class JU_VideoSplit
{
    
    @Test
    public void split()
    {
        ConvertVideo.splits("D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\outputPath\\WZYB-1080-720.mp4" 
                           ,"D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\outputPath" 
                           ,50 
                           ,5);
    }
    
}
