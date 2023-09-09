package com.yu.yolo;

/**
 * 检测结果
 * @param label 标签
 * @param bbox bbox[0] and [1] 代表左上角x和y bbox[2] and [3] 代表右下角x和y
 * @param confidence 置信度
 */
public record Detection(String label, float[] bbox, float confidence) {

}
