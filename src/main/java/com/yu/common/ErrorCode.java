package com.yu.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 错误码
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_MIME_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "无效的mime类型“"),
    INVALID_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "无效文件");
    private final HttpStatus httpStatus;
    private final String message;

}
