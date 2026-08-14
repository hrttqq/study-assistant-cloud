package com.study.common.core.model;

import com.study.common.core.enumConstants.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作返回实体
 *
 * @author 枫澜 @CreateDate：2018/11/20 16:21:41
 */
@ApiModel(description = "返回实体")
public class ResultMode<T> implements Serializable {

    private static final long serialVersionUID = 2168115660376016205L;

    /**
     * 单个实体返回
     */
    private T data;

    /**
     * 执行返回的实体,可以为空.
     */
    private List<T> model;


    /**
     * 本次查询总记录数
     */
    private int total = 0;


    /**
     * 是否成功
     */
    private boolean succeed = true;
    /**
     * 本次查询异常信息
     */
    private String errMsg = "";

    /**
     * 本次查询异常编码
     */
    private String errCode = "";

    /**
     * 构造函数
     */
    public ResultMode() {
        data = null;
        model = new ArrayList<>();
    }

    /**
     * 通用返回成功
     */
    public ResultMode<T> resultModeOk(T model){
        ResultMode<T> resultMode = new ResultMode<>();
        resultMode.setSucceed(true);
        resultMode.setErrCode(ErrorCodeEnum.CODE_200.getCode());
        resultMode.setErrMsg(ErrorCodeEnum.CODE_200.getDesc());
        resultMode.addModel(model);
        return resultMode;
    }

    /**
     * 通用返回失败
     */
    public ResultMode<T> resultModeError(String errMsg){
        ResultMode<T> resultMode = new ResultMode<>();
        resultMode.setSucceed(false);
        resultMode.setErrCode(ErrorCodeEnum.CODE_500.getCode());
        resultMode.setErrMsg(errMsg);
        return resultMode;
    }
    /**
     * 检验参数失败时 设置失败提示信息
     */
    public void checkParameterError(T errMsg) {
        model.add(errMsg);
        succeed = false;
    }

    /**
     * 通用返回参数校验败
     */
    public ResultMode(String errMsg) {
        succeed = false;
        this.errMsg = errMsg;
        this.errCode = ErrorCodeEnum.PARAM_CHECK.getCode();
    }


    /**
     * 构造函数，返回False的方法
     *
     * @param errCode 异常编码
     * @param errMsg  异常信息
     */
    public ResultMode(String errCode, String errMsg) {
        succeed = false;
        model = new ArrayList<>();
        total = 0;
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    /**
     * 设置错误消息
     *
     * @param errCode 异常编码
     * @param errMsg  异常信息
     * @param model   执行返回的实体,可以为空
     */
    public ResultMode(String errCode, String errMsg, List<T> model) {
        succeed = false;
        this.model = model;
        total = 0;
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    /**
     * 构造函数，返回False的方法
     *
     * @param errorCode {@link ErrorCodeEnum}
     */
    public ResultMode(ErrorCodeEnum errorCode) {
        succeed = false;
        model = new ArrayList<>();
        total = 0;
        errMsg = errorCode.getDesc();
        errCode = errorCode.getCode();
        if (ErrorCodeEnum.CODE_200.getCode().equals(errCode)) {
            succeed = true;
        }


    }

    /**
     * 构造函数，返回True的方法
     *
     * @param model 执行返回的实体,可以为空
     */
    public ResultMode(List<T> model) {
        succeed = true;
        this.model = model;
        total = model.size();
    }

    /**
     * 构造函数，返回True的方法
     *
     * @param model 执行返回的实体,可以为空
     * @param total 本次查询总记录数
     */
    public ResultMode(List<T> model, int total) {
        succeed = true;
        this.model = model;
        this.total = total;
        this.errCode = ErrorCodeEnum.CODE_200.getCode();
        this.errMsg = ErrorCodeEnum.CODE_200.getDesc();
    }

    /**
     * 构造函数，返回True的方法
     *
     * @param model 执行返回的实体,可以为空.
     */
    public ResultMode(T model) {
        succeed = true;
        this.model = new ArrayList<>();
        this.getModel().add(model);
        total = 1;
    }

    /**
     * 追加一个结果实体数据
     *
     * @param model 返回实体数据
     */
    public void addModel(T model) {
        this.model.add(model);
        total = this.model.size();
    }

    /**
     * 通过错误枚举值设置错误信息
     */
    public void setErrorCodeEnum(ErrorCodeEnum errorCode) {
        succeed = false;
        this.errCode = errorCode.getCode();
        this.errMsg = errorCode.getDesc();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getModel() {
        return model;
    }

    public void setModel(List<T> model) {
        this.model = model;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean getSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Override
    public String toString() {
        return "ResultMode{" +
                "data=" + data +
                ", model=" + model +
                ", total=" + total +
                ", succeed=" + succeed +
                ", errMsg='" + errMsg + '\'' +
                ", errCode='" + errCode + '\'' +
                '}';
    }
}
