package org.hy.common.video.junit.openCV.app;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;





/**
 * 图形界面的主窗口
 * 
 * @author ZhengWei(HY)
 * @create 2022-07-03
 */
@Xjava(XType.XML)
public class OpenCVFrame extends JFrame
{
    
    private static final long serialVersionUID = -1642575792442915300L;

    
    
    
    public OpenCVFrame()
    {
    }
    
    
    
    /**
     * 初始化主窗口信息
     */
    public void init()
    {
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((Component)XJava.getObject("xpMain") ,BorderLayout.CENTER);
    }
    
}
