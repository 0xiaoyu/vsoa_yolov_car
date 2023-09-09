package com.yu.utils;

import com.acoinfo.vsoa.CBCall;
import com.acoinfo.vsoa.Client;
import com.acoinfo.vsoa.Error;
import com.acoinfo.vsoa.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.acoinfo.vsoa.Request.VSOA_METHOD_SET;

/**
 * 车辆工具类
 * 用于与车辆通信
 * 枚举了所有的车辆状态和控制车辆的方法
 * @author za'y
 */
@Component
public class CarUtils {

    public static final String SPEED = "speed"; // 速度 0-240
    public static final String POWER = "power"; // 电量 0-100
    public static final String MILEAGE = "mileage"; // 里程
    public static final String GEAR = "gear"; // 档位 0-6
    public static final String TURNLIGHT = "turnlight"; // 转向灯  1左 2右 3双闪 0关
    public static final String LIGHT = "light"; // 灯光
    public static final String WIPER = "wiper";
    // 门 1左前 2右前 3左后 4右后 5后备箱 6车锁
    public static final String DOOR = "door";
    public static final String BRAKING = "braking"; // 刹车 1刹车 0松开
    public static final String SEATBELT = "seatbelt"; // 安全带 1系上 0松开
    public static final String ABS = "abs"; // ABS 1报警 0正常
    public static final String AIRBAG = "airbag"; // 安全气囊 1报警 0正常
    public static final String TIRE = "tire"; // 轮胎 1左前 2右前 3左后 4右后 5备胎
    public static final String BRAKINGWARING = "brakingwaring"; //刹车 1刹车 0松开
    public static final String SEATBELTWARING = "seatbeltwaring"; // 安全带 1系上 0松开
    public static final String ABSWARING = "abswaring"; // ABS 1报警 0正常
    public static final String LEFTFRONTTIRE = "leftfronttire"; //左前轮胎胎压 0-2.5
    public static final String RIGHTFRONTTIRE = "rightfronttire"; //右前轮胎胎压 0-2.5
    public static final String LEFTREARTIRE = "leftreartire"; //左后轮胎胎压 0-2.5
    public static final String RIGHTREARTIRE = "rightreartire"; //右后轮胎胎压 0-2.5


    private static Client client;

    @Autowired
    private void setClient(Client client) {
        CarUtils.client = client;
    }

    private static final String format = "{\"%s\":%d}";

    private static String getFormat(String name, int value) {
        return format.formatted(name, value);
    }

    private static Payload getPayload(String name, int value) {
        return new Payload(getFormat(name, value), null);
    }

    /**
     * 调节car
     *
     * @param name  属性名称
     * @param value 属性值
     * @return 是否成功
     */

    public static boolean client_set(String name, int value) {
        return client.call("/" + name, VSOA_METHOD_SET, getPayload(name, value), new CBCall() {
            @Override
            public void callback(Error error, Payload payload, int tid) {
                if (error != null) {
                    System.out.println("RPC call error:" + error.message);
                } else {
                    System.out.println("RPC call reply:" + payload.param);
                }
            }
        }, 2000);
    }

}
