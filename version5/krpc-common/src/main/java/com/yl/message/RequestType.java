package com.yl.message;

import lombok.AllArgsConstructor;

/**
 * @author yl
 * @date 2025-05-11 12:10
 */
@AllArgsConstructor
public enum RequestType {
    NORMAL(0),
    HEARTBEAT(1);

    private int code;

    public int getCode() {
        return code;
    }

}
