package org.hy.common.video.junit.openCV.app;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.hy.common.xml.XJava;

public class OpenCVMain
{
    
    public static void main(String [] i_Args) throws Exception
    {
        XJava.putObject("SYS_LANGUAGE" ,"cn");
        XJava.parserAnnotation("org.hy.common.video.junit.openCV.app");
        OpenCVFrame v_OpenCVFrame = (OpenCVFrame)XJava.getObject("OpenCVFrame");
        
        Dimension v_Dimension = Toolkit.getDefaultToolkit().getScreenSize();
        v_Dimension.height = v_Dimension.height - 40;
                
        v_OpenCVFrame.setSize(v_Dimension);
        v_OpenCVFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v_OpenCVFrame.setTitle("OpenCV UI");
        v_OpenCVFrame.setVisible(true);
        v_OpenCVFrame.validate();
        v_OpenCVFrame.init();
    }
    
}
