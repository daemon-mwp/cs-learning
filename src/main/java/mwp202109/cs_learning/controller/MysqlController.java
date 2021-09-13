package mwp202109.cs_learning.controller;

import lombok.AllArgsConstructor;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import mwp202109.cs_learning.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(("/mysql"))
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class MysqlController {
    private final UserMapper userMapper;

    @GetMapping("/getUser")
    public List<UserDO> getUser(){
        return userMapper.getAllUser();
    }
}
