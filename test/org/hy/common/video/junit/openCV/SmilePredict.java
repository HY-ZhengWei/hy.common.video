package org.hy.common.video.junit.openCV;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;


public class SmilePredict {
    //窗体
    static JFrame frame;
    //显示图片
    static JLabel label;
    static boolean flag = true;

    // 第0个flag表示是否正在预测
    // 第1个flag表示预测结果是否为smile
    // 第2个flag表示是否保存当前图片
    static final boolean[] flags = {false, false, false};

    public static void main(String args[]) {
        //加载 opencv 配置文件
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //创建Opencv中的视频捕捉对象
        VideoCapture camera = new VideoCapture();
        //open函数中的0代表当前计算机中索引为0的摄像头
        camera.open(0);
        //isOpened函数用来判断摄像头调用是否成功
        if (!camera.isOpened()) {
            System.out.println("Camera Error");
        } else {
            Mat img = new Mat();//创建一个输出帧
            //记录以及保存的图片数量
            int count =0;
            //初始化窗口
            frame = createJFrame("windowName", 640, 480);
            //官方人脸检测模型
            CascadeClassifier faceDetector = new CascadeClassifier("C:\\OpenCV\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface_improved.xml");
            //定义人脸检测
            MatOfRect faceDetections = new MatOfRect();

            //循环读取摄像头内容
            while (flag) {
                camera.read(img);//read方法读取摄像头的当前帧
                faceDetector.detectMultiScale(img, faceDetections);
                //获取人脸区域
                Rect[] rects = faceDetections.toArray();
                //存在人脸
                if (faceDetections.toArray().length > 0) {
                    //获得人脸区域
                    for (Rect rect : rects) {
                        Mat mat = new Mat(rect.width, rect.width, CvType.CV_8UC3);
                        for (int i = rect.x; i < rect.x + rect.width; i++) {
                            for (int j = rect.y; j < rect.y + rect.height; j++) {
                                mat.put(j - rect.y, i - rect.x, img.get(j, i));
                            }
                        }

                        //标注人脸区域
                        //如果是smile则为绿色,否则为红色
                        if(flags[1])
                            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                    new Scalar(0, 255, 0), 2);
                        else
                            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                    new Scalar(0, 0, 255), 2);
                        //是否保存当前图片
                        if(flags[2]){
                            //smile or non smile
                            if(flags[1])
                                Imgcodecs.imwrite("./output/smile/" +count+++".jpg", img);
                            else
                                Imgcodecs.imwrite("./output/non_smile/" +count+++".jpg", img);
                            flags[2]=false;
                        }

                        //截取人脸区域用于预测
                        Imgproc.resize(mat, mat, new Size(64, 64), 0, 0, 0);


                        //如果当前没有在预测则新开线程进行预测
                        if (!flags[0]) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    flags[0] = true;
                                    //获取hog特征值
                                    float hog[] = SmileTrain.getHOG(mat);
                                    //预测结果
                                    if (predict(hog)) {
                                        flags[1] = true;
                                    } else {
                                        flags[1] = false;
                                    }
                                    flags[0] = false;
                                }
                            }).start();
                        }
                        //根据是否为smile显示不同文字
                        if (flags[1]) {
                            Imgproc.putText(img, "smile", new Point(rect.x, rect.y), Imgproc.FONT_HERSHEY_COMPLEX, 2, new Scalar(0, 255, 0));
                        } else {
                            Imgproc.putText(img, "no smile", new Point(rect.x, rect.y), Imgproc.FONT_HERSHEY_COMPLEX, 2, new Scalar(0, 0, 255));
                        }
                    }
                }
                //重新加载窗口图片
                Image loadedImage = toBufferedImage(img);
                label.setIcon(new ImageIcon(loadedImage));
                try {
                    Thread.sleep(100);//线程暂停100ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        System.exit(0);
    }

    //把Mat转为Image用于显示
    public static Image toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // 获取所有的像素点
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }


    //创建窗口
    public static JFrame createJFrame(String windowName, int width, int height) {
        JFrame frame = new JFrame(windowName);
        label = new JLabel();
        final JScrollPane imageScrollPane = new JScrollPane(label);
        //滚动条
        imageScrollPane.setPreferredSize(new Dimension(width, height));
        frame.add(imageScrollPane, BorderLayout.CENTER);
        //自适应大小
        frame.pack();
        //设置窗口位置
        frame.setLocationRelativeTo(null);
        //窗口可见
        frame.setVisible(true);
        //关闭事件
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//用户点击窗口关闭
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //设置键盘监听
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                //按下s保存
                if(e.getKeyCode()==83){
                    flags[2]=true;
                }
                //按下esc退出
                else if(e.getKeyCode()==27){
                    flag=false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        return frame;
    }

    public static boolean predict(float hog[]){
        SVM svm = SVM.load("./output/best.xml");
        Mat predictMat = new Mat();
        predictMat.create(1, hog.length, CvType.CV_32FC1);
        predictMat.put(0, 0, hog);
        float results = svm.predict(predictMat); //预测结果
        if(results==1)
            return true;
        return false;
    }

}
