package com.study.user.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@ApiModel(description = "微信登陆信息")
public class StudyWxUserResponse implements Serializable {

    /**
     * 建议序列化的class都给一个序列化的ID，这样可以保证序列化的成功，版本的兼容性。
     */
    private static final long serialVersionUID = 100000L;

    //#region 原表数据字段属性
    //#region 变量定义
    /**
     * 接口凭证
     */
    @ApiModelProperty(value = "接口凭证", name = "accessToken")
    private String accessToken;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp createTime;
    /**
     * 性别【radio:0-未知性别,1-男,2-女,5-女变男,6-男变女,9-其他】
     */
    @ApiModelProperty(value = "性别【radio:0-未知性别,1-男,2-女,5-女变男,6-男变女,9-其他】 ", name = "gender")
    private String gender;
    /**
     * 登录id
     */
    @ApiModelProperty(value = "登录id", name = "loginId")
    private String loginId;
    /**
     * 大会员返回凭证
     */
    @ApiModelProperty(value = "大会员返回凭证", name = "memberToken")
    private String memberToken;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", name = "mobile")
    private String mobile;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", name = "modifyTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp modifyTime;
    /**
     * 第三方昵称
     */
    @ApiModelProperty(value = "第三方昵称", name = "nickName")
    private String nickName;
    /**
     * 授权用户唯一标识
     */
    @ApiModelProperty(value = "授权用户唯一标识", name = "openid")
    private String openid;
    /**
     * 第三方用户头像
     */
    @ApiModelProperty(value = "第三方用户头像", name = "photo")
    private String photo;
    /**
     * 是否推送大会员【select:10-否,20-已推,30-推送失败】
     */
    @ApiModelProperty(value = "是否推送大会员【select:10-否,20-已推,30-推送失败】", name = "pushBigMember")
    private String pushBigMember;
    /**
     * 绑定公众号后的关联ID
     */
    @ApiModelProperty(value = "绑定公众号后的关联ID", name = "unionid")
    private String unionid;
    /**
     * 内部用户Id
     */
    @ApiModelProperty(value = "内部用户Id", name = "userBaseId")
    private String studyUserId;
    /**
     * ID
     */
    @ApiModelProperty(value = "ID", name = "wxuserId")
    private String wxuserId;
    /**
     * 状态【select:0-正常,1-禁用,2-移除】
     */
    @ApiModelProperty(value = "状态【select:0-正常,1-禁用,2-移除】", name = "wxuserStatus")
    private String wxuserStatus;


    @ApiModelProperty(value = "生日", name = "birthday")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;


    @ApiModelProperty(value = "二维码图片id")
    private String qrCode;

    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段", name = "ex")
    private String ex;

    @ApiModelProperty(value = "推送大会员失败备注", name = "pushMemberRemark")
    private String pushMemberRemark;
}
