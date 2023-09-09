package com.yu.utils;

public enum LightName {
    WU(1),//雾灯
    YUAN(2),//远光灯
    JIN(3),//近光灯
    SHIKUO(8);//示廓灯

    public final int value;

    LightName(int value){
        this.value = value;
    }
}
