package com.example.ayuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ayuan.domain.User;

import java.io.Serializable;

/**
* @author apple
* @description 针对表【user】的数据库操作Service
* @createDate 2023-05-24 12:56:24
*/
public interface UserService extends IService<User> {

    @Override
    default User getById(Serializable id) {
        return IService.super.getById(id);
    }
}
