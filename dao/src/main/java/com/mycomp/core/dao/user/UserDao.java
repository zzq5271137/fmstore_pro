package com.mycomp.core.dao.user;

import com.mycomp.core.pojo.user.User;
import com.mycomp.core.pojo.user.UserQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    User selectByPrimaryKey(Long id);

    List<User> selectByExample(UserQuery example);

    int insert(User record);

    int insertSelective(User record);

    int updateByPrimaryKey(User record);

    int updateByPrimaryKeySelective(User record);

    int updateByExample(@Param("record") User record, @Param("example") UserQuery example);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserQuery example);

    int deleteByPrimaryKey(Long id);

    int deleteByExample(UserQuery example);

    int countByExample(UserQuery example);

}