package com.yu;

import ai.onnxruntime.OrtException;
import com.yu.yolo.ModelFactory;
import com.yu.yolo.Yolo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * web项目启动类
 */
@SpringBootApplication
public class SpringApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
        boolean f = false;
        try {
            for (String s : args) {
                String[] split = s.split("=");
                if (Objects.equals(split[0], "-view")) {
                    f = Boolean.parseBoolean(split[1]);
                }
            }
        }catch (Exception e){
            System.out.println("参数错误");
        }
        System.out.println(f);
        if (f) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    VideoApp videoApp = new VideoApp();
                    videoApp.run();

                }
            }, 6000);
        }

    }



    @Bean
    public ModelFactory getModelFactory() {
        return new ModelFactory();
    }

    @Bean
    @Autowired
    public Yolo getYolo(ModelFactory modelFactory) throws IOException, OrtException {
        return modelFactory.getModel("model.properties");
    }
}
