package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.video.CVPoint;
import org.hy.common.video.OpenCV;
import org.junit.Test;

public class JU_999_OpenCV
{
    
    @Test
    public void test_merge()
    {
        List<CVPoint> v_Rectangles = new ArrayList<CVPoint>();
        
        v_Rectangles.add(new CVPoint(10 ,10 ,20 ,20));
        v_Rectangles.add(new CVPoint(16 ,16 ,26 ,26));
        v_Rectangles.add(new CVPoint(24 ,24 ,34 ,34));
        
        OpenCV.merge(v_Rectangles);
        
        Help.print(v_Rectangles);
    }
    
}
