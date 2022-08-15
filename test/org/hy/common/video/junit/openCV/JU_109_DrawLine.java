package org.hy.common.video.junit.openCV;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.hy.common.StringHelp;
import org.hy.common.video.OpenCV;
import org.hy.common.xml.log.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;





/**
 * 测试单元：绘制危险区
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-15
 * @version     v1.0
 */
public class JU_109_DrawLine extends JFrame implements WindowListener
{

    private static final long serialVersionUID = 6188485059429529607L;
    
    private static final Logger $Logger = new Logger(JU_109_DrawLine.class ,true);
    
    private ZPanel              imagePanel;
    
    private ZPanelMouseListener mouseListener;
    
    
    
    public static void main(String [] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new JU_109_DrawLine();
    }
    
    
    
    public JU_109_DrawLine()
    {
        String v_ImageUrl  = JU_109_DrawLine.class.getResource("JU_016_PolyLines_T1N151.jpg").getFile().substring(1);
        this.imagePanel    = new ZPanel();
        this.mouseListener = new ZPanelMouseListener(this.imagePanel ,v_ImageUrl ,"D:\\VideoDatas\\JU_109_DrawLine.jpg");
        this.imagePanel.addMouseListener(this.mouseListener);
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(this.imagePanel ,BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(this);
        this.setSize(1280, 800);
        this.setVisible(true);
    }
    
    
    
    
    
    
    class ZPanelMouseListener implements MouseListener
    {
        private ZPanel      imagePanel;
                            
        private String      imageUrl;
                            
        private String      saveUrl;
        
        private List<Point> points;
        
        

        public ZPanelMouseListener(ZPanel i_ImagePanel ,String i_ImageUrl ,String i_SaveUrl)
        {
            this.points     = new ArrayList<Point>();
            this.imageUrl   = i_ImageUrl;
            this.saveUrl    = i_SaveUrl;
            this.imagePanel = i_ImagePanel;
            this.imagePanel.setImagePath(this.imageUrl);
        }
        
        
        public List<Point> getPoints()
        {
            return this.points;
        }
        
        
        @Override
        public void mouseClicked(MouseEvent e)
        {
            this.points.add(new Point(e.getX() ,e.getY()));
            
            Mat v_MTarget = Imgcodecs.imread(this.imageUrl);
            
            List<MatOfPoint> v_Points = new ArrayList<MatOfPoint>();
            
            v_Points.add(new MatOfPoint(this.points.toArray(new Point [] {})));
            Imgproc.polylines(v_MTarget ,v_Points ,true ,OpenCV.$Color_Red ,2);
            
            Imgcodecs.imwrite(this.saveUrl ,v_MTarget);
            this.imagePanel.setImagePath(this.saveUrl);
            this.imagePanel.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }
    }
    
    
    
    class ZPanel extends javax.swing.JPanel
    {

        private static final long serialVersionUID = 7893551691286678277L;

        private Image             image;

        private int               imgWidth;

        private int               imgHeight;

        
        
        public int getImgWidth()
        {
            return imgWidth;
        }



        public void setImgWidth(int imgWidth)
        {
            this.imgWidth = imgWidth;
        }



        public int getImgHeight()
        {
            return imgHeight;
        }



        public void setImgHeight(int imgHeight)
        {
            this.imgHeight = imgHeight;
        }



        public ZPanel()
        {
        }



        public void setImagePath(String imgPath)
        {
            // 该方法不推荐使用，该方法是懒加载，图像并不加载到内存，当拿图像的宽和高时会返回-1；
            // image = Toolkit.getDefaultToolkit().getImage(imgPath);
            try
            {
                // 该方法会将图像加载到内存，从而拿到图像的详细信息。
                image = ImageIO.read(new FileInputStream(imgPath));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            setImgWidth(image.getWidth(this));
            setImgHeight(image.getHeight(this));
        }



        @Override
        public void paintComponent(Graphics g1)
        {
            int x = 0;
            int y = 0;
            Graphics g = g1;
            if ( null == image )
            {
                return;
            }
            g.drawImage(image ,x ,y ,image.getWidth(this) ,image.getHeight(this) ,this);
            g = null;
        }
    }



    @Override
    public void windowOpened(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void windowClosing(WindowEvent e)
    {
        List<Point> v_Points = this.mouseListener.getPoints();
        for (Point v_Point : v_Points)
        {
            $Logger.info("," + StringHelp.rpad((int)v_Point.x ,5 ," ") + "," + (int)v_Point.y);
        }
    }



    @Override
    public void windowClosed(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void windowIconified(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void windowDeiconified(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void windowActivated(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void windowDeactivated(WindowEvent e)
    {
        // TODO Auto-generated method stub
        
    }
}
