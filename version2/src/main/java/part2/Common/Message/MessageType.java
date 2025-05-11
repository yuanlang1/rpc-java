package part2.Common.Message;

import lombok.AllArgsConstructor;

/**
 * @author yl
 * @date 2025-05-06 11:17
 */
@AllArgsConstructor
public enum MessageType {
    // 枚举常量 代表消息请求
    REQUEST(0),
    // 枚举常量 代表消息响应
    RESPONSE(1);
    // 每个枚举值对应的编码
    private int code;

    public int getCode() {
        return code;
    }

}
