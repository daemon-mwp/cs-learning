package mwp202109.cs_learning.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import mwp202109.cs_learning.cmmmon.ResponseCode;
import mwp202109.cs_learning.cmmmon.RestResponse;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import mwp202109.cs_learning.dao.mapper.EsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@Api(tags = "elasticsearch相关接口")
@RequestMapping(("/elasticsearch"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class ElasticsearchController {
    private final EsMapper esMapper;

    @ApiOperation(value = "插入单个用户")
    @PostMapping("/insertUser")
    public RestResponse<Object> insertUser(@RequestBody @NotNull UserDO user) throws IOException {
        esMapper.insertUser(user);
        return RestResponse.build(ResponseCode.SUCCESS_200);
    }

    @ApiOperation(value = "批量插入用户")
    @PostMapping("/bulkInsertUser")
    public RestResponse<Object> bulkInsertUser(@RequestBody @NotNull List<UserDO> users) throws IOException {
        esMapper.bulkInsertUser(users);
        return RestResponse.build(ResponseCode.SUCCESS_200);
    }

    @ApiOperation(value = "批量删除用户")
    @PostMapping("/bulkDeleteUser")
    public RestResponse<Object> bulkDeleteUser(@RequestBody @NotNull List<String> ids) throws IOException {
        esMapper.bulkDeleteUser(ids);
        return RestResponse.build(ResponseCode.SUCCESS_200);
    }

    @ApiOperation(value = "批量删除用户")
    @PostMapping("/bulkUpdateUser")
    public RestResponse<Object> bulkUpdateUser(@RequestBody @NotNull List<UserDO> users) throws IOException {
        esMapper.bulkUpdateUser(users);
        return RestResponse.build(ResponseCode.SUCCESS_200);
    }
}
