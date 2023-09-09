package com.yu.controller;

import ai.onnxruntime.OrtException;
import com.acoinfo.vsoa.*;
import com.yu.yolo.Detection;
import com.yu.yolo.Yolo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * vsoa的controller
 * @author za'y
 */
@Component
public class VSOAController {
    private  static String  SERVER_INFO   = "\"VSOA_car server\"";
    private  static String  PASSWORD      = "123456";
    private  static String  SERVER_ADDR   = "127.0.0.1";
    private  static int     SERVER_PORT   = 3100;

    static Server server;

    private final Yolo inferenceSession;

    /**
     * 构造函数
     * 初始化推理和vsoa服务
     * @param inferenceSession 推理会话
     */
    @Autowired
    public VSOAController(Yolo inferenceSession){
        this.inferenceSession = inferenceSession;
        try {
            // 初始化vsoa服务
            ServerOption opt = new ServerOption(SERVER_INFO, PASSWORD, false);
            // 服务地址
            InetSocketAddress address = new InetSocketAddress(SERVER_ADDR, SERVER_PORT);
            // 创建服务
            server = new Server(opt) {
                @Override
                public void onClient(CliHandle client, boolean link) {
                    if (!client.isConnected()) {
                        System.out.println("disconnected");
                    }

                    System.out.println("Client link " + link + " address: " + client.address().toString());
                }
            };
            // 启动服务
            if (!server.start(address, null)) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return ;
        }
        /*
          每次请求接收数据,回复数据
          携带参数payload为base64编码的图片数据
         */

        server.on("/car", new CBOnCall() {
            @Override
            public boolean Callback(String s, Server.CliHandle cliHandle, Request request, Payload payload) {
                try {
                    byte[] bytes = Base64.decodeBase64(payload.param);
                    Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
                    List<Detection> result = VSOAController.this.inferenceSession.run(img);
                    cliHandle.reply(Constant.SUCCESS, request.seqno,
                            new Payload(result.toString(),null));
                } catch (OrtException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });

    }

}
