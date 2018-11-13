package io.dymatics.cogny.domain.persist;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.dymatics.cogny.domain.model.Page;
import io.dymatics.cogny.domain.model.Partner;
import io.dymatics.cogny.domain.model.User;

@Mapper
public interface UserRepository {

    void insert(User user);

    User findByNo(Long userNo);
    
    @Select("SELECT user_no FROM user WHERE email=#{email} AND user_status='MEMBER'")
    List<User> findByEmail(String email);
    
    User findByUid(String Uid);

    int countByPage(Page page);

    List<Partner> findByPage(Page page);

    void update(User user);

    void delete(Long userNo);
}
