package com.yu.service;

import org.springframework.stereotype.Service;

/**
 * 发送vosa的车相关的服务实现接口
 * @author za'y
 */
@Service
public interface CarService {

    // 通用实现
    boolean common(String key, int value);

    // 初始化
    boolean init();

    // 减速
    boolean sspeed();
    // 停车
    boolean Stop();

    // 控制速度
    boolean Speed(int speed);

    // 换挡
    boolean Gear(int gear);

    // 修改里程
    boolean Mileage(int mileage);

    // 修改电量
    boolean Power(int power);

    // 控制灯光
    boolean Light(int light);

    // 设置门状态
    boolean Door(int door);
}
