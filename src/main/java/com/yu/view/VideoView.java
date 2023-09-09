package com.yu.view;

import ai.onnxruntime.OrtException;
import com.yu.service.CarService;
import com.yu.service.impl.CarServiceImpl;
import com.yu.utils.DistanceUtil;
import com.yu.utils.ImageUtil;
import com.yu.utils.VoiceUtil;
import com.yu.yolo.Detection;
import com.yu.yolo.ModelFactory;
import com.yu.yolo.Yolo;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 视频界面
 * 选择视频进行推理展示
 * 列出了距离和提示以及车辆信息
 * @author yu
 *
 */
public class VideoView {


    private final CarService carService = new CarServiceImpl();

    private Yolo inferenceSession;

    public JFrame jframe = new JFrame("cat");
    private final int FRAME_WIDTH = 1530;
    private final int FRAME_HEIGHT = 800;
    private final int VIDEO_WIDTH = 600;
    private final int VIDEO_HEIGHT = 500;
    private final int startY = 50;

    private MatOfByte matofByte1;
    private MatOfByte matofByte2;
    private final JLabel video1 = new JLabel();
    private final JLabel video2 = new JLabel();
    private VideoCapture cap;
    private JFileChooser chooser = new JFileChooser();
    private JTextArea article = new JTextArea();
    private JTextArea action = new JTextArea();

    String catName = """
            traffic sign
            train
            traffic light
            """;



    private final VoiceUtil cat = new VoiceUtil("注意前车");
    private final VoiceUtil tflight = new VoiceUtil("注意前车");
    private final VoiceUtil sign = new VoiceUtil("注意标志");
    private final VoiceUtil peo = new VoiceUtil("注意行人");
    ExecutorService e = Executors.newFixedThreadPool(5);
    public VideoView() {
        ModelFactory modelFactory = new ModelFactory();
        try {
            this.inferenceSession = modelFactory.getModel("model.properties");
            nu.pattern.OpenCV.loadLocally();
            matofByte1 = new MatOfByte();
            matofByte2 = new MatOfByte();
            cap = new VideoCapture(0);
        } catch (OrtException | IOException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }


    public VideoCapture init() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & mp4", "mp4", "gif");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(jframe);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            cap = new VideoCapture(chooser.getSelectedFile().getPath());
            chooser.setVisible(false);
        }
        cap.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
        cap.set(Videoio.CAP_PROP_FRAME_HEIGHT, 800);

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSL(video1, 0, startY, VIDEO_WIDTH, VIDEO_HEIGHT);
        setSL(video2, VIDEO_WIDTH, startY, VIDEO_WIDTH, VIDEO_HEIGHT);
        Container c = jframe.getContentPane();
        //设置组件添加顺序
        c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        //设置组件流式布局以及组件间隔
        c.setLayout(null);
        //向面板添加组件
        c.add(video1);
        c.add(video2);
        jframe.setVisible(true);
        jframe.setResizable(true);
        jframe.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        setSL(article, 2 * VIDEO_WIDTH, startY, 300, 500);
        article.setFont(new Font("宋体", Font.BOLD, 25));
        jframe.add(article);

        setSL(action, 2 * VIDEO_WIDTH, startY + 500, 300, 250);
        action.setFont(new Font("宋体", Font.BOLD, 25));
        jframe.add(action);


//        speed.addInputMethodListener();
        if (!cap.isOpened()) {
            System.exit(1);
        }
        return cap;
    }

    public void read(Mat frame) {
        ImageUtil.resizeWithPadding(frame, frame, VIDEO_WIDTH, VIDEO_HEIGHT);
        Imgcodecs.imencode(".jpg", frame, matofByte1);
        ImageIcon image = new ImageIcon(matofByte1.toArray());
        video1.setIcon(image);
        video1.repaint();
        // run detection
        try {
            List<Detection> detectionList = this.inferenceSession.run(frame)
                    .stream().filter(o -> o.confidence() > 0.5).toList();
            if (detectionList.size() == 0) {
                action.setText("");
            }
            StringBuilder sb = new StringBuilder();
            detectionList.forEach(
                    o -> {
                        double distance = DistanceUtil.getDistance(o);
                        if (distance < 15) {
                            if (catName.contains(o.label())){
                                action.setText("注意路标");
                                e.execute(sign);
                            }else if("tf_red".equals(o.label())){
                                action.setText("注意红灯");
                                // 停车
                                carService.Stop();
                                e.execute(tflight);
                            }else if("person".equals(o.label())) {
                                action.setText("注意行人");
                                // 停车
                                carService.Stop();
                                e.execute(peo);
                            }
                            else {
                                action.setText("注意车距，减速....");
                                // 减速40
                                carService.sspeed();
                                e.execute(cat);
                            }
                        }else{
                            carService.Speed(100);
                            action.setText("");
                        }

                        sb.append("%7s".formatted(o.label())).append(":")
                                .append("%.2fm".formatted(distance))
                                .append("\n");
                    });
            article.setText(String.valueOf(sb));
            ImageUtil.drawPredictions(frame, detectionList);
        } catch (OrtException ortException) {
            ortException.printStackTrace();
        }
        Imgcodecs.imencode(".jpg", frame, matofByte2);
        ImageIcon imageIcon = new ImageIcon(matofByte2.toArray());
        video2.setIcon(imageIcon);
        video2.repaint();
    }


    public void setSL(JComponent jComponent, int x, int y, int width, int height) {
        this.setSL(jComponent, x, y, width, height, true);
    }

    public void setSL(JComponent jComponent, int x, int y, int width, int height, boolean visible) {
        jComponent.setLocation(x, y);
        jComponent.setSize(width, height);
        jComponent.setVisible(visible);
    }
}
