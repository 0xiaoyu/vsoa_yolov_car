package com.yu.exception;

import com.yu.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传文件异常
 * 用于处理上传文件时的异常
 */
@Getter
@AllArgsConstructor
public class UploadFileException extends RuntimeException {
    private final ErrorCode errorCode;
}
