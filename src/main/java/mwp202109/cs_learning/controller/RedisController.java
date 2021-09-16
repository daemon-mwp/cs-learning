package mwp202109.cs_learning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import mwp202109.cs_learning.cmmmon.ResponseCode;
import mwp202109.cs_learning.cmmmon.RestResponse;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "redis相关练习接口")
@RequestMapping(("/redis"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class RedisController {
    private final RedissonClient redissonClient;

    @ApiOperation(value = "添加字符串键值")
    @PostMapping("/addStringKeyValue")
    public RestResponse<Object> addStringKeyValue(@RequestParam @NotNull String key, @RequestParam @NotNull String value){
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value,5, TimeUnit.MINUTES);
        return RestResponse.build(ResponseCode.SUCCESS);
    }
}
