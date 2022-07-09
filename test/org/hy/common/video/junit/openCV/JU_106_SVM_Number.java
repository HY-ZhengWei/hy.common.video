package org.hy.common.video.junit.openCV;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.video.OpenCV;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;





/**
 * 测试单元：数字识别
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-07
 * @version     v1.0
 */
public class JU_106_SVM_Number
{
    
    public JU_106_SVM_Number()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    /**
     * 准备训练数据：将大图分割成小图
     */
    @Test
    public void bigImageToSmallImage()
    {
        int    v_Number    = 0;
        int    v_FileIndex = 0;
        Mat    v_MSource   = Imgcodecs.imread(JU_106_SVM_Number.class.getResource("JU_106_SVM_Number.png").getFile().substring(1));
        Mat    v_MGray     = new Mat();
        String v_SavePath  = "C:\\WorkSpace\\hy.common.video\\test\\org\\hy\\common\\video\\junit\\openCV\\SVM_03" + Help.getSysPathSeparator();
        
        Imgproc.cvtColor(v_MSource, v_MGray, Imgproc.COLOR_BGR2GRAY);
        int v_BlockSize = 20;
        int v_RowCount  = v_MGray.rows() / v_BlockSize;         // 原图为1000*2000
        int v_ColCount  = v_MGray.cols() / v_BlockSize;         // 裁剪为5000个20*20的小图块

        for (int v_RowIndex = 0; v_RowIndex < v_RowCount; v_RowIndex++)
        {
            int v_OffsetY = v_RowIndex * v_BlockSize;           // 行上的偏移量
            
            if( v_RowIndex % 5 == 0 )                           // 每5行是一组数字
            {
                if ( v_RowIndex != 0 )
                {
                    v_Number++;
                    v_FileIndex = 0;
                }
                
                File v_SaveDirFile = new File(v_SavePath + StringHelp.lpad(v_Number ,2 ,"0"));
                v_SaveDirFile.mkdirs();
            }
            
            for (int v_ColIndex = 0; v_ColIndex < v_ColCount; v_ColIndex++)
            {
                int v_OffsetX = v_ColIndex * v_BlockSize;        // 列上的偏移量
                
                Mat    v_MSmall   = OpenCV.cutImage(v_MGray ,new Rect(v_OffsetX ,v_OffsetY ,v_BlockSize ,v_BlockSize));  // 截取20*20的小块
                String v_SaveFile = StringHelp.lpad(v_Number ,2 ,"0") + Help.getSysPathSeparator() + StringHelp.lpad(++v_FileIndex ,3 ,"0") + ".png";
                
                Imgcodecs.imwrite(v_SavePath + v_SaveFile ,v_MSmall);
                
                v_MSmall.release();
            }
        }
        
        v_MSource.release();
        v_MGray.release();
    }
    
    
    
    /**
     * 开始训练
     */
    @Test
    public void svmTrain()
    {
        String        v_SVMPath     = "C:\\WorkSpace\\hy.common.video\\test\\org\\hy\\common\\video\\junit\\openCV\\SVM_03" + Help.getSysPathSeparator();
        List<Mat>     v_TrainDatas  = new ArrayList<Mat>();
        List<Integer> v_TrainLabels = new ArrayList<Integer>();
        
        makeTrainDatas(v_SVMPath + "00" ,0 ,v_TrainDatas ,v_TrainLabels);
        makeTrainDatas(v_SVMPath + "01" ,1 ,v_TrainDatas ,v_TrainLabels);
        
        
        Mat v_MTrainDatas  = new Mat();
        Mat v_MTrainLabels = new Mat(v_TrainLabels.size(), 1, CvType.CV_32SC1);
        
        Core.vconcat(v_TrainDatas, v_MTrainDatas);
        for (int i = 0; i < v_TrainLabels.size(); i++)
        {
            int [] v_TrainLabel = { v_TrainLabels.get(i) };
            v_MTrainLabels.put(i, 0, v_TrainLabel);
        }
        
        
        SVM v_SVM = OpenCV.svm(v_MTrainDatas ,v_MTrainLabels);
        v_SVM.save(v_SVMPath + "HY.xml");
    }
    
    
    
    /**
     * 生成训练数据
     * 
     * @param i_DirFullName
     * @param i_TrainLabel
     * @param io_TrainDatas
     * @param io_TrainLabels
     */
    private void makeTrainDatas(String i_DirFullName ,int i_TrainLabel ,List<Mat> io_TrainDatas ,List<Integer> io_TrainLabels)
    {
        File    v_SVMDir   = new File(i_DirFullName);
        File [] v_SVMFiles = v_SVMDir.listFiles();
        
        for (File v_SVMFile : v_SVMFiles)
        {
            Mat v_MSource = Imgcodecs.imread(v_SVMFile.getAbsolutePath());
            Mat v_MTarget = v_MSource.clone();
            
            Imgproc.cvtColor(v_MTarget ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);
            v_MTarget.convertTo(v_MTarget, CvType.CV_32FC1);
            
            /*
             * cn为新的通道数，如果cn = 0，表示通道数不会改变。
             * 参数rows为新的行数，如果rows = 0，表示行数不会改变。
             * 
             * 我们将参数定义为reshape(1, 1)的结果就是原图像对应的矩阵将被拉伸成一个一行的向量，作为特征向量。
             */
            Mat v_Reshape = v_MTarget.reshape(1, 1);
            
            io_TrainDatas.add(v_Reshape);
            io_TrainLabels.add(i_TrainLabel);
        }
    }
    
    
    
    @Test
    public void check()
    {
        SVM v_Svm     = SVM.load(JU_101_Document.class.getResource("SVM_03/HY.xml").getFile().substring(1));
        Mat v_MSource = Imgcodecs.imread(JU_101_Document.class.getResource("JU_101_Document.jpg").getFile().substring(1));
        Mat v_MTarget = new Mat();
        
        Imgproc.cvtColor(v_MSource ,v_MTarget ,Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(v_MTarget ,v_MTarget ,new Size(20 ,20));
        v_MTarget.convertTo(v_MTarget, CvType.CV_32FC1);
        Mat v_Reshape = v_MTarget.reshape(1, 1);
        
        float v_Ret = v_Svm.predict(v_Reshape);
        System.out.println(v_Ret);
    }
    
}
