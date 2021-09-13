package mwp202109.cs_learning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import mwp202109.cs_learning.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "mysql相关练习接口")
@RequestMapping(("/mysql"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class MysqlController {
    private final UserMapper userMapper;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getUser")
    public IPage<UserDO> getUser(@RequestParam @NotNull Long pageNo, @RequestParam @NotNull Long pageSize){
        return userMapper.getAllUser(new Page(pageNo, pageSize));
    }

}
