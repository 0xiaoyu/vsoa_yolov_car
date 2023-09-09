package com.yu.yolo;

import ai.onnxruntime.OrtException;
import com.yu.utils.ConfigReader;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 模型工厂
 * 用于获取模型
 */
public class ModelFactory {


    /**
     * 获取模型
     *
     * @param propertiesFilePath 配置文件路径
     * @return 模型
     * @throws IOException
     * @throws OrtException
     */
    public Yolo getModel(String propertiesFilePath) throws IOException, OrtException {

        ConfigReader configReader = new ConfigReader();
        Properties properties = configReader.readProperties(propertiesFilePath);
        String modelName = properties.getProperty("modelName");
        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + properties.getProperty("modelPath"));
        File file2 = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "coco.names");
        String modelPath = String.valueOf(file);
        String labelPath = String.valueOf(file2);
        float confThreshold = Float.parseFloat(properties.getProperty("confThreshold"));
        float nmsThreshold = Float.parseFloat(properties.getProperty("nmsThreshold"));
        int gpuDeviceId = Integer.parseInt(properties.getProperty("gpuDeviceId"));
        /*File file = ResourceUtils.getFile("classpath:yolov5s.onnx");
        String modelPath = String.valueOf((file));
        File file2 = ResourceUtils.getFile("classpath:coco.names");
        String labelPath = String.valueOf((file2));
        String modelName = properties.getProperty("modelName");
//        String modelPath = Objects.requireNonNull(getClass().getClassLoader().getResource(properties.getProperty("modelPath"))).getFile();
//        String labelPath = Objects.requireNonNull(getClass().getClassLoader().getResource(properties.getProperty("labelPath"))).getFile();
        float confThreshold = Float.parseFloat(properties.getProperty("confThreshold"));
        float nmsThreshold = Float.parseFloat(properties.getProperty("nmsThreshold"));
        int gpuDeviceId = Integer.parseInt(properties.getProperty("gpuDeviceId"));*/

        if (modelName.equalsIgnoreCase("yolov5")) {
            return new YoloV5(modelPath, labelPath, confThreshold, nmsThreshold, gpuDeviceId);
        } else if (modelName.equalsIgnoreCase("yolov8")) {
            return new YoloV8(modelPath, labelPath, confThreshold, nmsThreshold, gpuDeviceId);
        } else {
            throw new RuntimeException("Invalid model name");
        }

    }
}
