package org.hy.common.video.junit.openCV;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hy.common.xml.log.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;





/**
 * 测试单元：SVM训练模型
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-05
 * @version     v1.0
 */
public class JU_103_SVM
{
    private static final Logger $Logger = new Logger(JU_103_SVM.class ,true);
    
    
    
    /** 每次处理后的Mat历史列表 */
    private Map<String ,Mat> matHistorys;
    
    
    
    public JU_103_SVM()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    public synchronized Mat newMat(String i_MatName)
    {
        Mat v_New = new Mat();
        
        if ( this.matHistorys == null )
        {
            this.matHistorys = new LinkedHashMap<String ,Mat>();
        }
        
        this.matHistorys.put(i_MatName ,v_New);
        return v_New;
    }
    
    
    
    public Mat getMatHistory(String i_MatName)
    {
        return this.matHistorys.get(i_MatName);
    }
    
    
    
    @Test
    public void svm()
    {
        // 训练数据 体重,身高
        float[] train_data = { 186, 80, 185, 81, 165, 60, 168, 61, 160, 50, 161, 48 };
        // 测试数据 身高,体重
        float[] test = { 184, 79, 160, 62, 159, 50 };
        // 训练数据 0=男 ,1=女
        int[] label = { 0, 0, 0, 0, 1, 1 };
        
        Mat train_mat = new Mat(6, 2, CvType.CV_32FC1);
        train_mat.put(0, 0, train_data);

        Mat train_lable = new Mat(6, 1, CvType.CV_32SC1);
        train_lable.put(0, 0, label);

        Mat test_lable = new Mat(3, 2, CvType.CV_32FC1);
        test_lable.put(0, 0, test);

        SVM v_SvmCreate = SVM(train_mat,train_lable,test_lable);
        
        
        
        // 测试训练的效果
        SVM svm = SVM.load("D:\\OpenCV\\HY.xml");
        Mat imread = Imgcodecs.imread(JU_101_Document.class.getResource("JU_101_Document.jpg").getFile().substring(1));
        Mat dst = new Mat();
        Imgproc.resize(imread,dst,new Size(640 ,800));
        
        Imgproc.cvtColor(dst ,dst ,Imgproc.COLOR_BGR2GRAY);
        
        imread.convertTo(dst, CvType.CV_32FC1);
        Mat reshape = imread.reshape(0, 1);
        
        Mat responseMat = new Mat();
        // v_SvmCreate.predict(reshape, responseMat, 0);
        svm.predict(reshape, responseMat, 0);
        
        System.out.println(responseMat.dump());
        System.out.println(svm.predict(reshape));
    }
    
    
    
    /**
     * @param : tarin 训练数据
     * @param : lable 训练标签
     * @param : test  测试数据
     */
    public static SVM SVM(Mat tarin ,Mat lable ,Mat test)
    {
        SVM svm = SVM.create();
        svm.setC(1);
        svm.setP(0);
        svm.setNu(0);
        svm.setCoef0(0);
        svm.setGamma(1);
        svm.setDegree(0);
        
        
        /***
         * 核类型
         * CvSVM::LINEAR - 没有任何向映像至高维空间，线性区分（或回归）在原始特征空间中被完成，这是最快的选择。 d(x,y) = x•y == (x,y)
         * CvSVM::POLY - 多项式核: d(x,y)= (gamma*(x•y)+coef0)degree
         * CvSVM::RBF - 径向基，对于大多数情况都是一个较好的选择：d(x,y)= exp(-gamma*|x-y|2)
         * CvSVM::SIGMOID - sigmoid函数被用作核函数:d(x,y) = tanh(gamma*(x•y)+coef0)
         * ***/
        svm.setType(SVM.C_SVC);
        
        /***
         * 核类型
         * CvSVM::LINEAR - 没有任何向映像至高维空间，线性区分（或回归）在原始特征空间中被完成，这是最快的选择。 d(x,y) = x•y == (x,y)
         * CvSVM::POLY - 多项式核: d(x,y)= (gamma*(x•y)+coef0)degree
         * CvSVM::RBF - 径向基，对于大多数情况都是一个较好的选择：d(x,y)= exp(-gamma*|x-y|2)
         * CvSVM::SIGMOID - sigmoid函数被用作核函数:d(x,y) = tanh(gamma*(x•y)+coef0)
         * ***/
        svm.setKernel(SVM.LINEAR);
        
        // /训练参数迭代终止条件，训练类型，最大迭代次数 ，结果精确性精度
        TermCriteria criteria=new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER ,1000 ,0);
        svm.setTermCriteria(criteria);

        TrainData trainData = TrainData.create(tarin ,Ml.ROW_SAMPLE ,lable);
        svm.train(trainData.getSamples(), Ml.ROW_SAMPLE ,trainData.getResponses());
        svm.save("D:\\OpenCV\\HY.xml");

        Mat response = new Mat();
        svm.predict(test, response, 0);

        System.out.println(response.dump());

        for (int i = 0; i < response.height(); i++)
        {
            if (response.get(i, 0)[0] == 0)
            {
                System.out.println("男");
            }
            if (response.get(i, 0)[0] == 1)
            {
                System.out.println("女");
            }
        }
        
        return svm;
    }
    
}