package org.hy.common.video.junit.openCV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;

public class SmileTrain {

    //每张图片的标签,1笑 0不笑
    static int flags[] = new int[4002];
    //训练标签
    static int resultFlag[] = new int[4002];
    //分组 10组每组367张
    static int result[][] = new int[10][367];
    //随机分组文字
    static File randomFiles[][] = new File[10][367];
    //每张图片的HOG值
    static float inputArr[][] = new float[4002][];
    //每组每张图片的HOG值
    static float hogs[][][] = new float[10][367][];


    public static void main(String args[]) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //加载 opencv 配置文件
        //getFace("./genki4k/files/");
        handleFace();
    }

    /**
     * 在给予的图片中找到人脸并保存
     */
    public static void getFace(String filePath) {
        System.out.println("读取人脸......");
        //人脸检测模型
        CascadeClassifier faceDetector = new CascadeClassifier("C:\\OpenCV\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml");
        //定义人脸检测
        MatOfRect faceDetections = new MatOfRect();
        //获取图像所在的文件夹
        File file = new File(filePath);
        //遍历文件夹中的所有图片
        for (File f : file.listFiles()) {
            //读取图片
            Mat image = Imgcodecs.imread(f.getAbsolutePath());
            //对图像进行人脸检测
            faceDetector.detectMultiScale(image, faceDetections);
            //获取人脸区域
            Rect[] rects = faceDetections.toArray();

            //存在人脸
            if (faceDetections.toArray().length > 0) {
                //获得人脸区域
                for (Rect rect : rects) {
                    Mat mat = new Mat(rect.width, rect.width, CvType.CV_8UC3);
                    for (int i = rect.x; i < rect.x + rect.width; i++) {
                        for (int j = rect.y; j < rect.y + rect.height; j++) {
                            mat.put(j - rect.y, i - rect.x, image.get(j, i));
                        }
                    }
                    //截取人脸区域
                    Imgproc.resize(mat, mat, new Size(64, 64), 0, 0, 0);
                    //保存图像
                    Imgcodecs.imwrite("./genki4k/files1/" + f.getName(), mat);
                }
            }
        }
        System.out.println("读取人脸完毕");
    }

    /**
     * 把图片进行随机分组,并且设置图片的标签
     *
     * @param filePath
     */
    public static void getRandomFile(String filePath) {
        System.out.println("图片随机分组......");
        File file = new File(filePath);
        File listFile[] = file.listFiles();
        Random random = new Random();
        //初始化标签
        //前2162为笑脸
        for (int i = 0; i < 3670; i++) {
            String no = listFile[i].getName().substring(4, 8);
            if (Integer.parseInt(no) <= 2162)
                flags[Integer.parseInt(no)] = 1;
            else
                flags[(Integer.parseInt(no))] = 0;
        }
        //随机排列,连同标签一起排列
        for (int i = 0; i < 3670; i++) {
            int r = random.nextInt(i + 1);
            File tmp = listFile[i];
            listFile[i] = listFile[r];
            listFile[r] = tmp;
            int t = flags[i];
            flags[i] = flags[r];
            flags[r] = t;
        }
        //分组
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 367; j++) {
                randomFiles[i][j] = listFile[i * 367 + j];
            }
        }
        System.out.println("图片随机分组完毕");
    }

    /**
     * 十折方式处理初始数据
     */
    public static void handleFace() {
        //最好得分
        double best = 0;
        //最好得分模型
        int index = 0;
        System.out.println("处理人脸......");
        int k;
        for (int t = 0; t < 10; t++) {
            k = 0;
            getRandomFile("./genki4k/files1/");
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 367; j++) {
                    //读取图片
                    Mat originalImage = Imgcodecs.imread(randomFiles[i][j].getAbsolutePath());
                    //获取HOG值
                    inputArr[k] = getHOG(originalImage);
                    hogs[i][j] = inputArr[k];
                    //获得标签
                    String no = randomFiles[i][j].getName().substring(4, 8);
                    resultFlag[k] = flags[Integer.parseInt(no)];
                    result[i][j] = flags[Integer.parseInt(no)];
                    k++;
                }
            }
            //训练该组数据
            train(t);
            double res = test(t);
            if (res > best) {
                best = res;
                index = t;
            }
        }

        //把最好的模型单独复制一份
        File src = new File("./output/result" + index + ".xml");
        File dst = new File("./output/best.xml");
        try {
            FileInputStream inputStream = new FileInputStream(src);
            FileOutputStream outputStream = new FileOutputStream(dst);
            // 得到源文件通道
            FileChannel inputChannel = inputStream.getChannel();
            // 得到目标文件通道
            FileChannel outputChannel = outputStream.getChannel();
            //将源文件数据通达连通到目标文件通道进行传输
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("处理人脸完毕");
    }


    public static float[] getHOG(Mat src) {
        //灰度以及梯度处理
        src = getGradient(src);
        //图像缩放
        Imgproc.resize(src, src, new Size(64, 128), 0, 0, Imgproc.INTER_AREA);
        HOGDescriptor hogDescriptor = new HOGDescriptor();
        MatOfFloat descriptorsValues = new MatOfFloat();
        MatOfPoint locations = new MatOfPoint();
        //计算 Hog 特征值
        hogDescriptor.compute(src, descriptorsValues, new Size(0, 0), new Size(0, 0), locations);
        //特征值维数
        int size = (int) (descriptorsValues.total() * descriptorsValues.channels());
        float[] temp = new float[size];
        descriptorsValues.get(0, 0, temp);
        //特征数组
        float[] resArrays = descriptorsValues.toArray();
        return resArrays;
    }


    public static Mat getGradient(Mat src) {
        Mat grayMat = new Mat();
        // 灰度
        Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_BGR2GRAY, 0);
        //梯度计算
        Mat x = new Mat(); //X 方向梯度值矩阵
        Mat y = new Mat(); //Y 方向梯度值矩阵
        //计算 X,Y 方向绝对梯度
        Imgproc.Sobel(grayMat, x, -1, 1, 0, 3, 1, 0);
        Core.convertScaleAbs(x, x);
        Imgproc.Sobel(grayMat, y, -1, 0, 1, 3, 1, 0);
        Core.convertScaleAbs(y, y);
        //XY 综合梯度
        Core.addWeighted(x, 0.5, y, 0.5, 0, grayMat);

        return grayMat;
    }

    public static void train(int t) {
        System.out.println("开始训练第" + (t + 1) + "组");
        //定义 SVM 对象
        SVM svm = SVM.create();
        //训练数据 Hog 特征值
        Mat dataMat = new Mat();
        //标签数据
        Mat labelMat = new Mat();
        //SVM类型
        svm.setType(SVM.C_SVC);
        //线性
        svm.setKernel(SVM.LINEAR);
        //迭代终止条件
        svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 20000, 1e-6));

        //数据大小
        int size = inputArr[0].length;

        //创建数据和标签对象,训练九组
        dataMat.create(3303, size, CvType.CV_32FC1);
        labelMat.create(3303, 1, CvType.CV_32SC1);

        //加载数据以及标签
        for (int i = 0; i < 3303; i++) {
            for (int j = 0; j < size; j++) {
                dataMat.put(i, j, inputArr[i][j]);
            }
            labelMat.put(i, 0, resultFlag[i]);
        }
        System.out.println("训练......");
        svm.train(dataMat, Ml.ROW_SAMPLE, labelMat);
        System.out.println("训练完毕");

        System.out.println("保存训练模型......");
        svm.save("./output/result" + t + ".xml"); //保存 SVM 模型
        dataMat.release(); //释放变量
        labelMat.release(); //释放变量
        System.out.println("保存训练模型完毕");
    }

    public static double test(int i) {
        System.out.println("开始测试");
        System.out.println("开始测试第" + (i + 1) + "组模型......");
        //创建 SVM 模型对象
        SVM svm = SVM.load("./output/result" + i + ".xml");
        // TP 为正样本，并且检测结果为正样本
        // FP 为正样本，并且检测结果为负样本
        // TN 为负样本，并且检测结果为负样本
        // FN 为负样本，并且检测结果为正样本
        double TP = 0;
        double FP = 0;
        double TN = 0;
        double FN = 0;

        for (int k = 0; k < 367; k++) {
            Mat testMat = new Mat();
            int size=hogs[i][k].length;
            testMat.create(1,size , CvType.CV_32FC1);
            for (int p = 0; p < size; p++) {
                testMat.put(0, p, hogs[i][k][p]);
            }
            float predict = svm.predict(testMat); //预测结果
            if (result[i][k] == 1 && predict == 1) {
                TP++; //TP 自增 1
            } else if (result[i][k] == 1 && predict == 0) {
                FP++; //FP 自增 1
            } else if (result[i][k] == 0 && predict == 0) {
                TN++; //TN 自增 1
            } else if (result[i][k] == 0 && predict == 1) {
                FN++; //FN 自增 1
            }
        }
        System.out.println("第" + (i + 1) + "组模型训练完毕");
        //计算正确率
        System.out.println("第" + (i + 1) + "组模型训练结果");
        System.out.println("TP: " + TP + "\t FP: " + FP + "\t TN: " + TN + "\t FN: " + FN);
        System.out.println("准确率:" + 2 * TP / (2 * TP + FP + FN));
        System.out.println();
        System.out.println("测试完毕");
        return 2 * TP / (2 * TP + FP + FN);
    }
}

