package com.yl.server.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yl
 * @date 2025-05-11 11:19
 */
// 服务端关注读空闲事件，写空闲事件
@Slf4j
public class HeartbeatHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            //处理事件
            if (evt instanceof IdleStateEvent) {
                // 获得状态
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                IdleState idleState = idleStateEvent.state();

                // 触发的是读空闲时间，说明已经超过n秒没有收到客户端心跳包
                if (idleState == IdleState.READER_IDLE) {
                    log.info("超过10s没有收到客户心跳包，channel：{}", ctx.channel());
                    // 关闭channel
                    ctx.close();
                } else if (idleState == IdleState.WRITER_IDLE) {
                    log.info("超过20s没有写数据，channel：{}", ctx.channel());
                    // 关闭channel
                    ctx.close();
                }
            }
        } catch (Exception e) {
            log.error("处理事件发送异常" + e);
        }
    }
}
