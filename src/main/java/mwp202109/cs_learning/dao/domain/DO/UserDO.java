package mwp202109.cs_learning.dao.domain.DO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDO {
    private Integer id;
    private String username;
    private String chineseName;
    private String password;
    private LocalDate createTime;
    private LocalDate updateTime;
}
