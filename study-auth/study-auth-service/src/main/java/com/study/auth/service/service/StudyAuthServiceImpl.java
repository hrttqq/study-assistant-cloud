package com.study.auth.service.service;

import com.alibaba.fastjson.JSON;
import com.study.auth.api.service.StudyAuthService;
import com.study.auth.service.mapper.StudyPermissionMapper;
import com.study.auth.service.mapper.StudyRoleMapper;
import com.study.auth.service.mapper.StudyRolePermissionMapper;
import com.study.auth.service.mapper.StudyUserRoleMapper;
import com.study.common.core.model.TokenInfo;
import com.study.common.core.utils.JwtUtil;
import com.study.common.core.utils.UtilityClass;
import com.study.user.api.response.StudyUserResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class StudyAuthServiceImpl implements StudyAuthService {


    @Resource
    private StudyPermissionMapper studyPermissionMapper;
    @Resource
    private StudyRoleMapper studyRoleMapper;
    @Resource
    private StudyUserRoleMapper studyUserRoleMapper;
    @Resource
    private StudyRolePermissionMapper studyRolePermissionMapper;


    @Override
    public String getJwtToken(StudyUserResponse studyUser, String clientType) {
        TokenInfo tokenInfo = new TokenInfo();
        // 根据用户id获取角色组集合
        List<String> permissionIdList = StudyUserRoleMapper.getPermissionIdListByAccountId(userInfo.getUserBaseId());
        tokenInfo.setUserBaseId(studyUser.getStudyUserId());
        tokenInfo.setCompanyId(userInfo.getOrganizationId());
        tokenInfo.setLoginId(userInfo.getLoginId());
        tokenInfo.setLoginName(userInfo.getLoginName());
        tokenInfo.setUsername(studyUser.getUsername());
        tokenInfo.setLoginVersionCode(userInfo.getExLoginVersionCode());
        tokenInfo.setPermissionIdList(permissionIdList);
        if (StringUtils.isNotBlank(token)) {
            tokenInfo.setUuid(token);
        } else {
            tokenInfo.setUuid(UtilityClass.uuid());
        }

        String tokenInfoString = JSON.toJSONString(tokenInfo);
        return JwtUtil.getInstance().generateToken(tokenInfoString);
    }
}
