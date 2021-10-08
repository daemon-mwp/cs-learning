package mwp202109.cs_learning.dao.domain.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class UserDO {
    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "中文名")
    private String chineseName;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "创建时间")
    @TableField(exist = false)
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    @TableField(exist = false)
    private LocalDateTime updateTime;
}
