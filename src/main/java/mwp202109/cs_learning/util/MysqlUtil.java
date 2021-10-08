package mwp202109.cs_learning.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import mwp202109.cs_learning.dao.domain.Req.MysqlPageBaseReq;
import org.springframework.util.ObjectUtils;

public class MysqlUtil {
    public static <T> QueryWrapper<T> getQueryWrapper(MysqlPageBaseReq pageBaseReq){
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        //字段值筛选
        if (!ObjectUtils.isEmpty(pageBaseReq.getEqualsReqs())) {
            pageBaseReq.getEqualsReqs().forEach(equalsReq -> {
                if (equalsReq.getKey() != null && equalsReq.getValue() != null) {
                    queryWrapper.eq(equalsReq.getKey(), equalsReq.getValue());
                }
            });
        }
        //时间范围筛选
        if(!ObjectUtils.isEmpty(pageBaseReq.getScopesReqs())){
            pageBaseReq.getScopesReqs().forEach(scopesReq -> {
                if(scopesReq.getKey() != null && scopesReq.getStartTime() != null && scopesReq.getEndTime() != null){
                    queryWrapper.ge(scopesReq.getKey(),scopesReq.getStartTime());
                    queryWrapper.le(scopesReq.getKey(),scopesReq.getEndTime());
                }
            });
        }
        return queryWrapper;
    }
}
