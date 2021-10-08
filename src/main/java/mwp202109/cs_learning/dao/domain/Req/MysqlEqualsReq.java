package mwp202109.cs_learning.dao.domain.Req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MysqlEqualsReq {
    @ApiModelProperty(value = "筛选字段")
    private String key;
    @ApiModelProperty(value = "筛选值")
    private Object value;
}
