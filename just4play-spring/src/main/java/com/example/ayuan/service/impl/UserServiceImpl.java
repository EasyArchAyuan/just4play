package com.example.ayuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ayuan.domain.User;
import com.example.ayuan.mapper.UserMapper;
import com.example.ayuan.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author apple
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-05-24 12:56:24
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




