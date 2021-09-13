package mwp202109.cs_learning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import mwp202109.cs_learning.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(("/mysql"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class MysqlController {
    private final UserMapper userMapper;

    @GetMapping("/getUser")
    public Object getUser(@RequestParam @NotNull Long pageNo, @RequestParam @NotNull Long pageSize){
        return userMapper.getAllUser(new Page(pageNo, pageSize));
    }

}
