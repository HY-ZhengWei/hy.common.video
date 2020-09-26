package org.hy.common.video.junit;

import java.io.File;

import org.hy.common.video.VideoHelp;
import org.hy.common.video.VideoInfo;
import org.junit.Test;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;





public class JU_VideoInfo
{
    
    @Test
    public void test_GetVideoInfo()
    {
        File v_VideoFile = new File("D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\org\\hy\\common\\video\\1.mp4");
        
        try
        {
            Encoder        v_Encoder    = new Encoder();
            MultimediaInfo v_Multimedia = v_Encoder.getInfo(v_VideoFile);
            
            if ( v_Multimedia != null )
            {
                if ( v_Multimedia.getVideo() != null )
                {
                    if ( v_Multimedia.getVideo().getSize() != null )
                    {
                        System.out.println(v_Multimedia.getVideo().getSize().getWidth());
                        System.out.println(v_Multimedia.getVideo().getSize().getHeight());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    @Test
    public void test_VideoHelp_GetInfo()
    {
        VideoHelp.$FFMpegHome = "D:\\WorkSpace_SearchDesktop\\hy.common.video\\ffmpeg-4.1-win64-static";
        
        VideoInfo v_Info = VideoHelp.getVideoInfo("D:\\WorkSpace_SearchDesktop\\hy.common.video\\test\\org\\hy\\common\\video\\1.mp4");
        
        if ( v_Info != null )
        {
            System.out.println(v_Info.getWidth());
            System.out.println(v_Info.getHeight());
        }
    }
    
}
