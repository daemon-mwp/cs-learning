package mwp202109.cs_learning.dao.domain.Req;

import lombok.Data;
import mwp202109.cs_learning.dao.domain.DO.UserDO;

import java.util.List;

@Data
public class MysqlAddUsersReq {
    private List<UserDO> list;
}
