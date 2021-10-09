package mwp202109.cs_learning.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import mwp202109.cs_learning.Aspect.DB;
import mwp202109.cs_learning.cmmmon.ResponseCode;
import mwp202109.cs_learning.cmmmon.RestResponse;
import mwp202109.cs_learning.config.mysql.DataSourceType;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import mwp202109.cs_learning.dao.domain.Req.MysqlAddUsersReq;
import mwp202109.cs_learning.dao.domain.Req.MysqlPageBaseReq;
import mwp202109.cs_learning.dao.mapper.UserMapper;
import mwp202109.cs_learning.util.MysqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "mysql相关接口")
@RequestMapping(("/mysql"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class MysqlController {
    private final UserMapper userMapper;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getUser")
    @DB(DataSourceType.DB1)
    public IPage<UserDO> getUser(@RequestParam @NotNull Long pageNo, @RequestParam @NotNull Long pageSize) {
        return userMapper.getAllUser(new Page(pageNo, pageSize));
    }

    @ApiOperation(value = "批量添加用户")
    @DB(DataSourceType.DB1)
    @PostMapping("/addUsers")
    public <T> RestResponse<T> addUsers(@RequestBody MysqlAddUsersReq mysqlAddUsersReq) {
        if (userMapper.insertBatchUsers(mysqlAddUsersReq.getList()) == mysqlAddUsersReq.getList().size()) {
            return RestResponse.success();
        } else {
            return RestResponse.fail();
        }
    }

    @ApiOperation(value = "批量删除用户")
    @DB(DataSourceType.DB1)
    @PostMapping("/delUsers")
    public RestResponse<String> delUsers(@RequestBody @Validated MysqlPageBaseReq pageBaseReq) {
        QueryWrapper<UserDO> queryWrapper = MysqlUtil.getQueryWrapper(pageBaseReq);
        int count = userMapper.delete(queryWrapper);
        return RestResponse.build(ResponseCode.SUCCESS_200.getCode(), "共删除" + count + "条数据");
    }

    @ApiOperation(value = "批量更新用户")
    @DB(DataSourceType.DB1)
    @PostMapping("/updateUsers")
    public RestResponse<String> updateUsers() {
        //UPDATE user SET password=? WHERE (username = ? OR (id = ? OR id = ?))
        int count = userMapper.update(null, new UpdateWrapper<UserDO>().eq("username", "2").or(i -> i.eq("id", 8).or().eq("id", 10)).set("password", "update"));
        return RestResponse.build(ResponseCode.SUCCESS_200.getCode(), "共修改" + count + "条数据");
    }

    @ApiOperation(value = "用户信息分页查询接口")
    @DB(DataSourceType.DB1)
    @PostMapping("/getUserByPage")
    public RestResponse<Page<UserDO>> queryMonitorEvent(@RequestBody @Validated MysqlPageBaseReq pageBaseReq) {
        Page<UserDO> page = new Page<>(pageBaseReq.getPageNo(), pageBaseReq.getPageSize());
        QueryWrapper<UserDO> queryWrapper = MysqlUtil.getQueryWrapper(pageBaseReq);
        queryWrapper.orderByDesc("create_time");
        //指定查询字段
        queryWrapper.select("id", "username");
        return RestResponse.success(userMapper.selectPage(page, queryWrapper));
    }


}
