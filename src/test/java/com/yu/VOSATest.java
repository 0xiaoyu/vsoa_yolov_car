package com.yu;

import com.acoinfo.vsoa.Error;
import com.acoinfo.vsoa.*;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static com.acoinfo.vsoa.Request.VSOA_METHOD_SET;

public class VOSATest {
    private static String PASSWORD = "123456";
    private static String POS_ADDRESS = "127.0.0.1";
    private static int POS_PORT = 3100;
    private static String setN = "{\"%s\":%d}";

    public static Client client;
    public static String info = """
            speed 速度 0-240
            power 电量 0-100
            mileage 里程
            gear 档位 0-6
            light 灯光 0-1
            door 车门，brake 刹车，abs.
            """;
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("输入修改的值：");
        System.out.println(info);
        String s = sc.next();
        System.out.println("输入要修改的");
        int value = sc.nextInt();
        System.out.println("成功修改" + s + "为" + value);
        if (true)
            return;
        client = new Client(new ClientOption(PASSWORD)){
            @Override
            public void onError(Error error) {
                System.out.println("Client error:" + error.message);
            }

            /*
             * Message receiver
             */
            @Override
            public void onMessage(String url, Payload payload, boolean quick) {
                if (payload.param.length() > 100) {
                    System.out.println("[CLIENT] received event: " + url +
                            " payload len: " + payload.param.length());
                } else {
                    System.out.println("[CLIENT] received event: " + url +
                            " payload: " + payload.param);
                }
            }

            @Override
            public void onConnected(String info) {
                System.out.println("Connected with server:" + info);
            }
        };

        InetSocketAddress address = new InetSocketAddress(POS_ADDRESS, POS_PORT);
        System.out.println(address);
        if (!client.connect(address, null, Constant.VSOA_DEF_CONN_TIMEOUT)) {
            System.out.println("Connected with server failed" + address);
            return;
        }

        Payload payload = new Payload(setN.formatted(s,value), null);
        client.call("/car", VSOA_METHOD_SET, payload, new CBCall() {
            @Override
            public void callback(Error error, Payload payload, int tunid) {
                if (error != null) {
                    System.out.println("RPC call error:" + error.message);
                } else {
                    System.out.println("RPC call reply:" + payload.param);
                }
            }
        }, 2000);

    }
    @Test
    public void setSpeedPayloadTest() {
        System.out.println("输入修改的值：");
        System.out.println(info);
        String s = sc.next();
        System.out.println("输入要修改的");
        int value = sc.nextInt();
        System.out.println("成功修改" + s + "为" + value);
    }
}
