package com.yu.yolo;

import ai.onnxruntime.*;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Yolo抽象类
 * 用于加载模型和标签
 * 以及推理
 *
 */
public abstract class Yolo {

    public static final int INPUT_SIZE = 640;
    public static final int NUM_INPUT_ELEMENTS = 3 * 640 * 640;
    public static final long[] INPUT_SHAPE = {1, 3, 640, 640};
    public float confThreshold;
    public float nmsThreshold;
    public OnnxJavaType inputType;
    protected final OrtEnvironment env;
    protected final OrtSession session;
    protected final String inputName;
    public ArrayList<String> labelNames;

    OnnxTensor inputTensor;

    /**
     * Constructor
     * @param modelPath 模型的位置
     * @param labelPath 标签的位置
     * @param confThreshold 置信度阈值
     * @param nmsThreshold nms阈值
     * @param gpuDeviceId gpu设备id
     * @throws OrtException
     * @throws IOException
     */
    public Yolo(String modelPath, String labelPath, float confThreshold, float nmsThreshold, int gpuDeviceId) throws OrtException, IOException {
        // 加载opencv
        nu.pattern.OpenCV.loadLocally();

        // 获取OrtEnvironment
        this.env = OrtEnvironment.getEnvironment();
        var sessionOptions = new OrtSession.SessionOptions();
        sessionOptions.addCPU(false);
        sessionOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);

        if (gpuDeviceId >= 0) sessionOptions.addCUDA(gpuDeviceId);
        this.session = this.env.createSession(modelPath, sessionOptions);

        Map<String, NodeInfo> inputMetaMap = this.session.getInputInfo();
        this.inputName = this.session.getInputNames().iterator().next();
        NodeInfo inputMeta = inputMetaMap.get(this.inputName);
        this.inputType = ((TensorInfo) inputMeta.getInfo()).type;

        this.confThreshold = confThreshold;
        this.nmsThreshold = nmsThreshold;

        BufferedReader br = new BufferedReader(new FileReader(labelPath));
        String line;
        this.labelNames = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            this.labelNames.add(line);
        }

    }

    /**
     * 进行推理
     * @param img 需要推理的图片
     * @return 检测结果
     * @throws OrtException 识别异常
     */
    public abstract List<Detection> run(Mat img) throws OrtException;

    private float computeIOU(float[] box1, float[] box2) {

        float area1 = (box1[2] - box1[0]) * (box1[3] - box1[1]);
        float area2 = (box2[2] - box2[0]) * (box2[3] - box2[1]);

        float left = Math.max(box1[0], box2[0]);
        float top = Math.max(box1[1], box2[1]);
        float right = Math.min(box1[2], box2[2]);
        float bottom = Math.min(box1[3], box2[3]);

        float interArea = Math.max(right - left, 0) * Math.max(bottom - top, 0);
        float unionArea = area1 + area2 - interArea;
        return Math.max(interArea / unionArea, 1e-8f);

    }

    /**
     * 非极大值抑制
     * 目的是从一组边界框中选择出重叠度较小且可信度最高的边界框，以便进行进一步的处理或展示
     * 首先，代码创建一个空列表bestBboxes，用于存储输出的边界框。
     * 然后，边界框列表bboxes按照可信度进行排序，从低到高。
     * 接下来，通过一个循环执行标准的非最大抑制算法。
     * 在每次循环中，从排序后的边界框列表的末尾取出可信度最高的边界框bestBbox（
     * 也就是通过pop操作取出列表的最后一个元素），并将其加入到输出列表bestBboxes中。
     * 然后，使用流处理（stream）对剩余的边界框进行筛选
     * 使用filter函数过滤掉与bestBbox重叠度（通过computeIOU函数计算）大于等于iouThreshold的边界框
     * 这样做的目的是确保输出列表中的边界框与当前处理的最佳边界框不重叠。
     * 最后，循环结束后，输出列表bestBboxes中存储了经过非最大抑制筛选后的边界框集合，即具有最高可信度且重叠较小的边界框。
     * @param bboxes 边界框列表
     * @param iouThreshold 重叠度阈值
     * @return 非极大值抑制后的边界框列表
     */
    protected List<float[]> nonMaxSuppression(List<float[]> bboxes, float iouThreshold) {

        // 输出箱
        List<float[]> bestBboxes = new ArrayList<>();
        // 可信度排序
        bboxes.sort(Comparator.comparing(a -> a[4]));

        // 标准纳米
        while (!bboxes.isEmpty()) {
            float[] bestBbox = bboxes.remove(bboxes.size() - 1);  // 目前信心最高的盒子pop
            bestBboxes.add(bestBbox);
            bboxes = bboxes.stream().filter(a -> computeIOU(a, bestBbox) < iouThreshold).collect(Collectors.toList());
        }

        return bestBboxes;
    }

    /**
     * 将xywh格式的边界框转换为xyxy格式
     * @param bbox 边界框
     */
    protected void xywh2xyxy(float[] bbox) {
        float x = bbox[0];
        float y = bbox[1];
        float w = bbox[2];
        float h = bbox[3];

        bbox[0] = x - w * 0.5f;
        bbox[1] = y - h * 0.5f;
        bbox[2] = x + w * 0.5f;
        bbox[3] = y + h * 0.5f;
    }

    protected void scaleCoords(float[] bbox, float orgW, float orgH, float padW, float padH, float gain) {
        // xmin, ymin, xmax, ymax -> (xmin_org, ymin_org, xmax_org, ymax_org)
        bbox[0] = Math.max(0, Math.min(orgW - 1, (bbox[0] - padW) / gain));
        bbox[1] = Math.max(0, Math.min(orgH - 1, (bbox[1] - padH) / gain));
        bbox[2] = Math.max(0, Math.min(orgW - 1, (bbox[2] - padW) / gain));
        bbox[3] = Math.max(0, Math.min(orgH - 1, (bbox[3] - padH) / gain));
    }

    static int argmax(float[] a) {
        float re = -Float.MAX_VALUE;
        int arg = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] >= re) {
                re = a[i];
                arg = i;
            }
        }
        return arg;
    }

}
