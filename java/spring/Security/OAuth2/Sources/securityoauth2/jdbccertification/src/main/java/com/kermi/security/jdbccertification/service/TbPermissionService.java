package com.kermi.security.jdbccertification.service;

import com.kermi.security.jdbccertification.domain.TbPermission;

import java.util.List;

public interface TbPermissionService {
    default List<TbPermission> selectByUserId(Long userId) {
        return null;
    }
}
