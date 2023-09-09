package com.yu.common;

public record R(int code, String msg, Object data) {


    public static R getInstance(int code, String msg, Object data) {
        return new R(code, msg, data);
    }
    public static R ok() {
        return new R(200, "ok", null);
    }

    public static R ok(Object data) {
        return new R(200, "ok", data);
    }

    public static R error() {
        return new R(500, "error", null);
    }

    public static R error(String msg) {
        return new R(500, msg, null);
    }

    public static R error(int code, String msg) {
        return new R(code, msg, null);
    }
}
