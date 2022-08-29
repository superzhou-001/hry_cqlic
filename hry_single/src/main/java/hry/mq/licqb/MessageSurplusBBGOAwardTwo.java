package hry.mq.licqb;

import com.rabbitmq.client.Channel;
import hry.licqb.remote.RemoteAwardService;
import hry.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import java.util.Date;

/**
 * @author zhouming
 * @date 2021/5/16 23:40
 * @Version 1.0
 */
public class MessageSurplusBBGOAwardTwo implements ChannelAwareMessageListener {
    private Logger log = LoggerFactory.getLogger(MessageSurplusBBGOAwardTwo.class);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String jsonMap = new String(message.getBody());
        RemoteAwardService remoteAwardService = SpringUtil.getBean("remoteAwardService");
        remoteAwardService.mqSurplusBBGOExchangeTwo(jsonMap);
        log.info(new Date() + "冻结BBGO静态奖励撤回成功进入mq");
    }
}
