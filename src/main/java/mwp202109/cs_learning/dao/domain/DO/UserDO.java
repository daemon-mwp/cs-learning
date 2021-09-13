package mwp202109.cs_learning.dao.domain.DO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDO {
    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "中文名")
    private String chineseName;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
