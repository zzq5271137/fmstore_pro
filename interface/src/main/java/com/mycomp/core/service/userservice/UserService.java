package com.mycomp.core.service.userservice;

import com.mycomp.core.exceptions.WrongSmscodeException;
import com.mycomp.core.pojo.user.User;

public interface UserService {

    void sendCode(String phone);

    void addUser(String smscode, User user) throws WrongSmscodeException;

}
