package com.study.wx.service.service;

import com.study.common.core.enumConstants.DicEnum;
import com.study.common.core.model.ResultMode;
import com.study.common.core.utils.RedisUtil;
import com.study.user.api.response.StudyUserResponse;
import com.study.user.api.response.StudyWxUserResponse;
import com.study.wx.api.feign.StudyAuthApiService;
import com.study.wx.api.feign.StudyExamApiService;
import com.study.wx.api.feign.StudyUserApiService;
import com.study.wx.api.vo.WxUserLoginResponseVO;
import com.study.wx.service.domain.WeChatAccessTokenResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WxLoginServiceImplTest {

    private final WxLoginServiceImpl service = new WxLoginServiceImpl();
    private final StudyUserApiService userApi = mock(StudyUserApiService.class);
    private final StudyAuthApiService authApi = mock(StudyAuthApiService.class);
    private final StudyExamApiService examApi = mock(StudyExamApiService.class);
    private final WeChatAccessTokenResponseVO session = new WeChatAccessTokenResponseVO();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "studyUserApiService", userApi);
        ReflectionTestUtils.setField(service, "studyAuthApiService", authApi);
        ReflectionTestUtils.setField(service, "studyExamApiService", examApi);
        session.setOpenid("test-open-id");

        StudyWxUserResponse wxUser = new StudyWxUserResponse();
        wxUser.setWxuserId("wx-user-id");
        wxUser.setStudyUserId("10000001");
        wxUser.setWxuserStatus(DicEnum.WxUserStatusEnum.DEFAULT.getCode());
        when(userApi.getOpenId("test-open-id", null)).thenReturn(wxUser);

        StudyUserResponse user = new StudyUserResponse();
        user.setStudyUserId("10000001");
        when(userApi.getModelByLoginId("10000001")).thenReturn(user);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void returnsInitializationStatusInLoginResponse(boolean initialized) {
        ResultMode<Map<String, Boolean>> examResult = new ResultMode<>();
        examResult.setData(Map.of("initialized", initialized));
        when(examApi.initialized("10000001")).thenReturn(examResult);

        try (MockedStatic<RedisUtil> redis = mockStatic(RedisUtil.class)) {
            redis.when(() -> RedisUtil.get(anyString())).thenReturn("Bearer test-token");

            ResultMode<WxUserLoginResponseVO> result = service.loginAfter(session, null);

            assertTrue(result.getSucceed());
            assertNotNull(result.getData());
            assertEquals(initialized, result.getData().getInitialized());
            assertEquals("10000001", result.getData().getStudyUserId());
            assertEquals("Bearer test-token", result.getData().getToken());
            verify(examApi).initialized("10000001");
        }
    }

    @ParameterizedTest
    @MethodSource("invalidExamResponses")
    void failsLoginWhenInitializationResponseIsInvalid(ResultMode<Map<String, Boolean>> examResult) {
        when(examApi.initialized("10000001")).thenReturn(examResult);

        ResultMode<WxUserLoginResponseVO> result = service.loginAfter(session, null);

        assertFalse(result.getSucceed());
        assertNull(result.getData());
        verifyNoInteractions(authApi);
    }

    private static Stream<ResultMode<Map<String, Boolean>>> invalidExamResponses() {
        ResultMode<Map<String, Boolean>> failed = new ResultMode<>();
        failed.setSucceed(false);
        failed.setData(Map.of("initialized", true));
        ResultMode<Map<String, Boolean>> missingData = new ResultMode<>();
        ResultMode<Map<String, Boolean>> missingStatus = new ResultMode<>();
        missingStatus.setData(Collections.emptyMap());
        ResultMode<Map<String, Boolean>> nullStatus = new ResultMode<>();
        nullStatus.setData(Collections.singletonMap("initialized", null));
        return Stream.of(null, failed, missingData, missingStatus, nullStatus);
    }

    @Test
    void failsLoginWhenExamServiceIsUnavailable() {
        when(examApi.initialized("10000001")).thenThrow(new IllegalStateException("unavailable"));

        ResultMode<WxUserLoginResponseVO> result = service.loginAfter(session, null);

        assertFalse(result.getSucceed());
        assertNull(result.getData());
        verifyNoInteractions(authApi);
    }

    @Test
    void doesNotQueryExamServiceWhenSystemUserDoesNotExist() {
        when(userApi.getModelByLoginId("10000001")).thenReturn(null);

        ResultMode<WxUserLoginResponseVO> result = service.loginAfter(session, null);

        assertFalse(result.getSucceed());
        assertNull(result.getData());
        verifyNoInteractions(examApi, authApi);
    }
}
