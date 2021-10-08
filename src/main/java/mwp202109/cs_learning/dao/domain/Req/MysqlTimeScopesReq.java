package mwp202109.cs_learning.dao.domain.Req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MysqlTimeScopesReq {
    @ApiModelProperty(value = "筛选字段")
    private String key;
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
