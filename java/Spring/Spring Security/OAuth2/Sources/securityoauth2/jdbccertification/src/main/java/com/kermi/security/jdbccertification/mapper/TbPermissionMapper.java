package com.kermi.security.jdbccertification.mapper;

import com.kermi.security.jdbccertification.domain.TbPermission;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TbPermissionMapper extends MyMapper<TbPermission> {
    List<TbPermission> selectByUserId(@Param("userId") Long userId);
}
