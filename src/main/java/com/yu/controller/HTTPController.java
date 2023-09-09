package com.yu.controller;

import ai.onnxruntime.OrtException;
import com.yu.common.ErrorCode;
import com.yu.common.R;
import com.yu.exception.UploadFileException;
import com.yu.service.CarService;
import com.yu.yolo.Detection;
import com.yu.yolo.Yolo;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * http的controller
 */
@RestController
@Slf4j
public class HTTPController {
    static private final List<String> mimeTypes = Arrays.asList("image/png", "image/jpeg");

    private final Yolo inferenceSession;

    @Autowired
    private CarService carService;

    @Autowired
    public HTTPController(Yolo inferenceSession) {
        this.inferenceSession = inferenceSession;
    }
    /*private HTTPController() throws OrtException, IOException {
        ModelFactory modelFactory = new ModelFactory();
        this.inferenceSession = modelFactory.getModel("model.properties");
    }*/

    /**
     * 上传图片进行检测
     * @param uploadFile 上传的图片
     * @return 检测结果
     */
    @PostMapping(value = "/detection", consumes = {"multipart/form-data"}, produces = {"application/json"})
    public List<Detection> detection(MultipartFile uploadFile) throws OrtException, IOException {
        if (!mimeTypes.contains(uploadFile.getContentType())) throw new UploadFileException(ErrorCode.INVALID_MIME_TYPE);
        byte[] bytes = uploadFile.getBytes();
        Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        List<Detection> result = inferenceSession.run(img);
        log.info("POST 200");
        return result;
    }


    /**
     * 设置车辆信息
     * @param name 车属性
     * @param value 车值
     * @return 结果
     */
    @GetMapping("/car/{name}/{value}")
    public R setCarInfo(@PathVariable("name") String name,@PathVariable("value") String value){
        try {
            carService.common(name, Integer.parseInt(value));
            return R.ok();
        }catch (Exception e){
            return R.error();
        }
    }

    /**
     * 设置车辆信息
     * @param map 车辆设置
     * @return 结果
     */
    @PostMapping("/car")
    public R setCarMore(@RequestBody Map<String,Integer> map){
        try {
            map.forEach((k,v)-> carService.common(k,v));
            return R.ok();
        }catch (Exception e){
            return R.error();
        }
    }

}
