package com.study.wx.service.domain;


/**
 * @ClassName WechatAppletUserInfoResponse
 * @Description TODO
 * @Auther guanQing
 * @Date 2019/3/23 下午1:05
 * @Version 1.0
 **/

public class WechatAppletUserInfoResponseVO {
    private String openId;//普通用户的标识，对当前开发者帐号唯一
    private String nickName;//普通用户昵称
    private Integer gender;//性别，小程序独有,1为男性，2为女性
    private String province;//普通用户个人资料填写的省份
    private String city;//普通用户个人资料填写的城市
    private String country;//国家，如中国为CN
    private String avatarUrl;//用户头像，小程序独有，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
    private String unionId;//用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
    private String mobile; //手机号 TODO 是否返回

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
