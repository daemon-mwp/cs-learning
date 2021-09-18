package mwp202109.cs_learning.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueueMessageListener {

    static final String QUEUE_MAIL = "q_mail";
    static final String QUEUE_SMS = "q_sms";
    static final String QUEUE_APP = "q_app";

    @RabbitListener(queues = QUEUE_MAIL)
    public void onRegistrationMessageFromMailQueue(JSONObject message) throws Exception {
        log.info("queue {} received registration message: {}", QUEUE_MAIL, message);
    }

    @RabbitListener(queues = QUEUE_SMS)
    public void onRegistrationMessageFromSmsQueue(JSONObject message) throws Exception {
        log.info("queue {} received registration message: {}", QUEUE_SMS, message);
    }

    @RabbitListener(queues = QUEUE_MAIL)
    public void onLoginMessageFromMailQueue(JSONObject message) throws Exception {
        log.info("queue {} received message: {}", QUEUE_MAIL, message);
    }

    @RabbitListener(queues = QUEUE_SMS)
    public void onLoginMessageFromSmsQueue(JSONObject message) throws Exception {
        log.info("queue {} received message: {}", QUEUE_SMS, message);
    }

    @RabbitListener(queues = QUEUE_APP)
    public void onLoginMessageFromAppQueue(JSONObject message) throws Exception {
        log.info("queue {} received message: {}", QUEUE_APP, message);
    }
}
