package com.kermi.security.jdbccertification.service.impl;

import com.kermi.security.jdbccertification.mapper.TbRoleMapper;
import com.kermi.security.jdbccertification.service.TbRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TbRoleServiceImpl implements TbRoleService{

    @Resource
    private TbRoleMapper tbRoleMapper;

}
