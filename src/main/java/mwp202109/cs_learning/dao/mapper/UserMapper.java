package mwp202109.cs_learning.dao.mapper;

import mwp202109.cs_learning.dao.domain.DO.UserDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    @Select("select id,username,password,chinese_name,create_time,update_time from user")
    List<UserDO> getAllUser();
}
