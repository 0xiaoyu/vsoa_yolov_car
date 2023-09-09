package com.yu.cli;

import ai.onnxruntime.OrtException;
import com.google.gson.Gson;
import com.yu.yolo.Detection;
import com.yu.utils.ImageUtil;
import com.yu.yolo.ModelFactory;
import com.yu.yolo.Yolo;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * 视觉识别脚手架
 * 启动后命令行输入图片路径，回车后输出识别结果
 * @author za'y
 */
public class CLI_App {

    private Yolo inferenceSession;
    private Gson gson;

    public CLI_App() {

        ModelFactory modelFactory = new ModelFactory();

        try {
            this.inferenceSession = modelFactory.getModel("./model.properties");
            this.gson = new Gson();
        } catch (OrtException | IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        CLI_App app = new CLI_App();

        while (true) {

            System.out.print("输入图像路径（输入'q/Q'退出）: ");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = null;

            try {
                input = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            if ("q".equals(input) | "Q".equals(input)) {
                System.out.println("Exit");
                System.exit(0);
            }

            File f = new File(input);
            if (!f.exists()) {
                System.out.println("File does not exists: " + input);
                continue;
            }
            if (f.isDirectory()) {
                System.out.println(input + " is a directory");
                continue;
            }

            Mat img = Imgcodecs.imread(input, Imgcodecs.IMREAD_COLOR);
            if (img.dataAddr() == 0) {
                System.out.println("Could not open image: " + input);
                continue;
            }
            try {
                // 运行检测
                List<Detection> detectionList = app.inferenceSession.run(img);
                // 绘制检测结果
                ImageUtil.drawPredictions(img, detectionList);
                // 输出检测结果
                System.out.println(app.gson.toJson(detectionList));
                // 输出图片宽高
                System.out.println(img.width());
                System.out.println(img.height());
                // 将绘制图像保存到指定的D盘中
                Imgcodecs.imwrite("D:\\predictions.jpg", img);
            } catch (OrtException ortException) {
                ortException.printStackTrace();
            }

        }

    }

}
