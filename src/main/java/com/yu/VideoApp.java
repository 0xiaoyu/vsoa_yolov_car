package com.yu;

import com.yu.service.CarService;
import com.yu.service.impl.CarServiceImpl;
import com.yu.view.VideoView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Controller;

/**
 * java图像识别启动类
 *
 */

@Controller
public class VideoApp {


    public void run() {
        CarService car = new CarServiceImpl();
        car.init();
        VideoView video = new VideoView();
        // 打开视频文件
        VideoCapture cap = video.init();
        Mat image = new Mat();
        int count = 0;
        while (cap.read(image)) {
            if (count++ % 4 == 0) {
                video.read(image);
            }
        }
        // 释放资源
        cap.release();
    }

}
