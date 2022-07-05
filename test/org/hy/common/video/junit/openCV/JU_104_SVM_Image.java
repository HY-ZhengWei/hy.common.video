package org.hy.common.video.junit.openCV;

import java.util.ArrayList;
import java.util.List;

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
public class JU_104_SVM_Image
{
    private static final Logger $Logger = new Logger(JU_104_SVM_Image.class ,true);
    
    /** 训练集 */
    public static List<Mat>     $TrainDatas  = new ArrayList<Mat>();
    
    /** 训练标签 */
    public static List<Integer> $TrainLabels = new ArrayList<Integer>();
    
    /** 训练图片的宽度 */
    public static int           $TranWidth   = 240;
    
    /** 训练图片的高度 */
    public static int           $TranHeight  = 240;
    
    
    
    public JU_104_SVM_Image()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    
    
    @Test
    public void svm_train_data()
    {
        loadData(1 ,JU_104_SVM_Image.class.getResource("SVM_01/1-001.jpg").getFile().substring(1));
        loadData(2 ,JU_104_SVM_Image.class.getResource("SVM_01/2-001.jpg").getFile().substring(1));
        
        String v_SVMFile = "C:\\WorkSpace\\hy.common.video\\test\\org\\hy\\common\\video\\junit\\openCV\\SVM_01\\HY.xml";
        trainData(v_SVMFile);
        
        SVM v_SVM = loadSVM(v_SVMFile);
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/1-001.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/2-001.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/test-001.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/test-002.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/test-003.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/test-004.jpg").getFile().substring(1));
        svmTest(v_SVM ,JU_104_SVM_Image.class.getResource("SVM_01/test-005.jpg").getFile().substring(1));
    }
    
    
    
    public static void svmTest(SVM i_SVM ,String i_ImageName)
    {
        Mat v_MImage = Imgcodecs.imread(i_ImageName);
        svmTest(i_SVM ,v_MImage);
    }
    
    
    
    public static void svmTest(SVM i_SVM ,Mat i_MImage)
    {
        Mat v_MTarget = new Mat();
        
        Imgproc.cvtColor(i_MImage ,v_MTarget ,Imgproc.COLOR_RGB2GRAY);
        Imgproc.resize(v_MTarget ,v_MTarget ,new Size($TranWidth ,$TranHeight));
        v_MTarget.convertTo(v_MTarget, CvType.CV_32FC1);
        Mat v_Reshape = v_MTarget.reshape(0, 1);
        
        Mat v_Ret = new Mat();
        i_SVM.predict(v_Reshape, v_Ret, 0);
        
        $Logger.info(v_Ret.dump());
    }
    
    
    
    public static SVM loadSVM(String i_SaveSVM)
    {
        return SVM.load(i_SaveSVM);
    }
    
    
    
    public static void trainData(String i_SaveSVM)
    {
        Mat v_TrainDatas = new Mat();
        Core.vconcat($TrainDatas, v_TrainDatas);
        
        Mat v_TrainLabels = new Mat($TrainLabels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < $TrainLabels.size(); i++)
        {
            int [] v_TrainLabel = { $TrainLabels.get(i) };
            v_TrainLabels.put(i, 0, v_TrainLabel);
        }
        
        trainSVM(v_TrainDatas ,v_TrainLabels ,i_SaveSVM);
    }
    
    
    
    public static void loadData(int i_Label ,String i_TrainImageName)
    {
        Mat v_TrainImage = Imgcodecs.imread(i_TrainImageName);
        
        Imgproc.cvtColor(v_TrainImage ,v_TrainImage ,Imgproc.COLOR_RGB2GRAY);
        Imgproc.resize(v_TrainImage ,v_TrainImage ,new Size($TranWidth ,$TranHeight));
        
        v_TrainImage.convertTo(v_TrainImage ,CvType.CV_32FC1);
        Mat v_Reshape = v_TrainImage.reshape(0 ,1);
        
        $TrainDatas.add(v_Reshape);
        $TrainLabels.add(i_Label);
    }
    
    
    
    /**
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-07-05
     * @version     v1.0
     * 
     * @param i_TrainDatas   训练数据训练数据
     * @param i_TrainLables  训练标签
     * @param i_SaveSVM      保存训练结果的文件
     * @return
     */
    public static SVM trainSVM(Mat i_TrainDatas ,Mat i_TrainLables ,String i_SaveSVM)
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
        TermCriteria v_TermCriteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER ,1000 ,0.01);
        svm.setTermCriteria(v_TermCriteria);

        TrainData v_TrainData = TrainData.create(i_TrainDatas ,Ml.ROW_SAMPLE ,i_TrainLables);
        boolean v_Succeed = svm.train(v_TrainData.getSamples(), Ml.ROW_SAMPLE ,v_TrainData.getResponses());
        $Logger.info("训练是否成功：" + v_Succeed);
        
        svm.save(i_SaveSVM);
        return svm;
    }
    
}