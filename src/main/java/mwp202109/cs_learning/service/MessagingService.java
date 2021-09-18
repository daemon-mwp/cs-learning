package mwp202109.cs_learning.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessagingService {
    @Resource
    RabbitTemplate rabbitTemplate;

    public void sendRegistrationMessage(JSONObject data) {
        rabbitTemplate.convertAndSend("registration", "", data);
    }

    public void sendLoginMessage(JSONObject data) {
        String routingKey = data.getBoolean("success") ? "" : "login_failed";
        rabbitTemplate.convertAndSend("login", routingKey, data);
    }
}
