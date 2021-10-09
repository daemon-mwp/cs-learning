package mwp202109.cs_learning.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mwp202109.cs_learning.cmmmon.ResponseCode;
import mwp202109.cs_learning.cmmmon.RestResponse;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "redis相关接口")
@RequestMapping(("/redis"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class RedisController {
    private final RedissonClient redissonClient;

    /**
     * @param key   key
     * @param value value
     * @return 成功返回
     */
    @ApiOperation(value = "添加字符串键值")
    @PostMapping("/addStringKeyValue")
    public RestResponse<Object> addStringKeyValue(@RequestParam @NotNull String key, @RequestParam @NotNull String value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, 5, TimeUnit.MINUTES);
        return RestResponse.build(ResponseCode.SUCCESS_200);
    }

    /**
     * @param key  锁名
     * @param time 等待时间和失效时间，单位秒
     * @return 操作结果
     * @throws InterruptedException InterruptedException
     */
    @ApiOperation(value = "获取锁")
    @PostMapping("/getLock")
    public RestResponse<Object> getLock(@RequestParam @NotNull String key, @RequestParam @NotNull int time) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        boolean res = lock.tryLock(time, time, TimeUnit.SECONDS);
        return RestResponse.build(ResponseCode.SUCCESS_200.getCode(), res ? "获取锁成功！" : "获取锁失败！");
    }

    /**
     * @param key  队列名称
     * @param data 数据
     * @return 操作结果
     * @throws InterruptedException InterruptedException
     */
    @ApiOperation(value = "生产队列消息")
    @PostMapping("/pushQueue")
    public RestResponse<Object> pushQueue(@RequestParam @NotNull String key, @RequestParam @NotNull String data) throws InterruptedException {
        RBlockingQueue<Object> queue = redissonClient.getBlockingQueue(key);
        return RestResponse.build(ResponseCode.SUCCESS_200.getCode(), queue.add(data) ? "生产成功！" : "生产失败！");
    }

    @ApiOperation(value = "消费队列消息")
    @PostMapping("/pollQueue")
    public RestResponse<Object> pollQueue(@RequestParam @NotNull String key) {
        RBlockingQueue<Object> queue = redissonClient.getBlockingQueue(key);
        return RestResponse.success(queue.poll());
    }

    @ApiOperation(value = "创建新闻排行榜")
    @PostMapping("/createNewsList")
    public RestResponse<Object> createNewsList() {
        RScoredSortedSet<Object> newsList = redissonClient.getScoredSortedSet("NewsList");
        Map<Object, Double> newsMap = new HashMap<>();
        newsMap.put("news1", 1.0);
        newsMap.put("news3", 3.0);
        newsMap.put("news2", 2.0);
        newsMap.put("news5", 5.0);
        newsMap.put("news4", 4.0);
        return RestResponse.build(ResponseCode.SUCCESS_200.getCode(), newsList.addAll(newsMap) == newsMap.size() ? "创建成功！" : "创建失败！");
    }

    @ApiOperation(value = "获取热度前三的新闻")
    @PostMapping("/getNews")
    public RestResponse<Object> getNews() {
        RScoredSortedSet<Object> newsList = redissonClient.getScoredSortedSet("NewsList");
        Collection<ScoredEntry<Object>> scoredEntries = newsList.entryRangeReversed(0, 2);
        newsList.expire(5,TimeUnit.MINUTES);
        return RestResponse.success(scoredEntries);
    }
}
