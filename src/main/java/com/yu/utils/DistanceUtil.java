package com.yu.utils;

import com.yu.yolo.Detection;

import java.util.Map;

/**
 * 距离计算工具类
 * 用单目测距来计算物品到车辆的距离
 * 枚举了常见的物体的高度
 * 通过物体的高度和图像中物体的像素高度来计算物体到摄像头的距离
 * @author za'y
 */
public class DistanceUtil {

    // 镜头焦距
    private static final double FOC = 1196.0;
    // 高度,注意单位是英寸 1英寸=2.54厘米
    public static Map<String,Double> map = Map.of(
            "person",64.94,
            "car",57.08,
            "bus",102.3,
            "bike",26.248,
            "truck",78.7402,
            "rider",64.94,
            "traffic light",10.0
    );

    public static double getDistance(Detection detection) {
        String label = detection.label();
        Double height = map.getOrDefault(label,59.0);
        float[] bbox = detection.bbox();
        return  2.24 * (height * FOC) / (bbox[3] - bbox[1] - 2) / 100;
    }
}
