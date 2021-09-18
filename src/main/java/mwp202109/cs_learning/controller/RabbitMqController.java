package mwp202109.cs_learning.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mwp202109.cs_learning.cmmmon.ResponseCode;
import mwp202109.cs_learning.cmmmon.RestResponse;
import mwp202109.cs_learning.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@Api(tags = "rabbitmq相关练习接口")
@RequestMapping(("/rabbitmq"))
@Slf4j
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class RabbitMqController {
    private final MessagingService messagingService;

    @PostMapping("/register")
        public RestResponse<Object> doRegister(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("name") String name) throws Exception {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        data.put("name", name);
        data.put("success", true);
        try {
            messagingService.sendRegistrationMessage(data);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return RestResponse.fail("mq register send failed");
        }
        return RestResponse.success("mq register send success");
    }

    @PostMapping("/signin")
    public RestResponse<Object> doSignin(@RequestParam("email") String email, @RequestParam("password") String password) {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        data.put("success", true);
        try {
            messagingService.sendLoginMessage(data);
        } catch (RuntimeException e) {
            data.put("success", false);
            messagingService.sendLoginMessage(data);
            return RestResponse.fail("mq signin send failed");
        }
        return RestResponse.success("mq signin send success");
    }
}
