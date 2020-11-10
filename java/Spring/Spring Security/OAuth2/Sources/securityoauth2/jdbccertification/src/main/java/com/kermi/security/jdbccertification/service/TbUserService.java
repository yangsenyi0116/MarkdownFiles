package com.kermi.security.jdbccertification.service;

import com.kermi.security.jdbccertification.domain.TbUser;

public interface TbUserService {
    default TbUser getByUsername(String username) {
        return null;
    }
}
