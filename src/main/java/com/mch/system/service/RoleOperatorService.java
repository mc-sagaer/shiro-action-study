package com.mch.system.service;

import com.mch.system.mapper.RoleOperatorMapper;
import com.mch.system.model.RoleOperator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RoleOperatorService {

    @Resource
    private RoleOperatorMapper roleOperatorMapper;

    public int insert(RoleOperator roleOperator) {
        return roleOperatorMapper.insert(roleOperator);
    }

}
