package com.yu;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;


@Slf4j
//@SpringBootTest
public class VideoViewTest {


    @Test
    public void videoTest() {
        nu.pattern.OpenCV.loadLocally();

        // 打开视频文件
        VideoCapture videoCapture = new VideoCapture("D:\\0\\Captures\\1.mp4");

        // 检查视频文件是否打开成功
        if (!videoCapture.isOpened()) {
            System.out.println("无法打开视频文件");
            return;
        }
        // 获取视频的宽度和高度
        int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        // 创建新的视频文件
        VideoWriter videoWriter = new VideoWriter("D:\\0\\Captures\\0.mp4", VideoWriter.fourcc('X', '2', '6', '4'), 30, new Size(width, height));

//        // 创建分类器用于人脸检测
//        CascadeClassifier faceCascade = new CascadeClassifier("path/to/haarcascade_frontalface_default.xml");

        // 读取视频帧并进行分割
        Mat frame = new Mat();
        while (videoCapture.read(frame)) {
            // 将帧转换为灰度图像
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            // 对灰度图像进行直方图均衡化
            Imgproc.equalizeHist(grayFrame, grayFrame);

            // 使用分类器进行人脸检测
//            MatOfRect faces = new MatOfRect();
//            faceCascade.detectMultiScale(grayFrame, faces);

            // 在帧上绘制人脸检测结果
//            for (Rect rect : faces.toArray()) {
//                Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
//            }

            // 将帧写入新的视频文件
            videoWriter.write(frame);
        }

        // 释放资源
        videoCapture.release();
        videoWriter.release();
//        Imgproc.destroyAllWindows();
    }
}