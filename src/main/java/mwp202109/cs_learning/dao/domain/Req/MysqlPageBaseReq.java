package mwp202109.cs_learning.dao.domain.Req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MysqlPageBaseReq {
    @NotNull
    @ApiModelProperty(value = "页数")
    private Integer pageNo;
    @NotNull
    @ApiModelProperty(value = "页大小")
    private Integer pageSize;
    @ApiModelProperty(value = "筛选条件")
    private List<MysqlEqualsReq> equalsReqs;
    @ApiModelProperty(value = "筛选条件")
    private List<MysqlTimeScopesReq> scopesReqs;
}
