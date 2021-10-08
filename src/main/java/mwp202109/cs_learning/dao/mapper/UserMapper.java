package mwp202109.cs_learning.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import mwp202109.cs_learning.dao.domain.DO.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<UserDO>{
    /**
     * <p>
     * 查询 : 根据state状态查询用户列表，分页显示
     * 注意!!: 如果入参是有多个,需要加注解指定参数名才能在xml中取值
     * </p>
     *
     * @param page 分页对象,xml中可以从里面进行取值,传递参数 Page 即自动分页,必须放在第一位(你可以继承Page实现自己的分页对象)
     * @return 分页对象
     */
    @Select("select id,username,password,chinese_name,create_time,update_time from user order by id")
    IPage<UserDO> getAllUser(Page page);

    @Insert({"<script>",
            "insert into user(username,chinese_name,password) ",
            "values",
            "<foreach collection='list' item='temp' index='index' separator=','>",
            "(#{temp.username},#{temp.chineseName},#{temp.password})",
            "</foreach>",
            "</script>"})
    int insertBatchUsers(@Param(value = "list") List<UserDO> list);
}
