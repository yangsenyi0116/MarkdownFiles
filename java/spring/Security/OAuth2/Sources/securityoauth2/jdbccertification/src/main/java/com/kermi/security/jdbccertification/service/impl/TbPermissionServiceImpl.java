package com.kermi.security.jdbccertification.service.impl;

import com.kermi.security.jdbccertification.domain.TbPermission;
import com.kermi.security.jdbccertification.mapper.TbPermissionMapper;
import com.kermi.security.jdbccertification.service.TbPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TbPermissionServiceImpl implements TbPermissionService {

    @Resource
    private TbPermissionMapper tbPermissionMapper;

    @Override
    public List<TbPermission> selectByUserId(Long userId) {
        return tbPermissionMapper.selectByUserId(userId);
    }
}
