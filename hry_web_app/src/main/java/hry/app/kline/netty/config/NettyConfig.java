package hry.app.kline.netty.config;


import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyConfig {
    //存储每一个客户端接入进来的对象
    public static ChannelGroup tradingGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
