package mwp202109.cs_learning.cmmmon;


public enum ResponseCode {
    /**
     * 执行成功
     */
    SUCCESS_200(200, "执行成功"),

    /**
     * 服务器异步处理
     */
    SUCCESS_202(202, "服务器收到响应，进行异步处理"),

// ================== 400 ====================
    /**
     * 请求执行失败
     */
    FAIL_400(400, "请求失败"),

    /**
     * 未授权错误导致执行失败
     */
    FAIL_401_NO_AUTH(401, "未授权错误"),

    /**
     * 资源不可用导致执行失败
     */
    FAIL_403_FORBIDDEN(403, "资源不可用"),

    /**
     * 找不到请求接口导致执行失败
     */
    FAIL_404_NOT_FOUND(404, "找不到请求接口"),

    /**
     * 请求超时导致执行失败
     */
    FAIL_408_TIMEOUT(408, "请求超时"),

    /**
     * 填写的表单或者输入参数校验错误
     */
    FAIL_450_BAD_PARAM(450, "输入参数校验错误"),

    /**
     * 用户名或密码错误
     */
    FAIL_451_USERNAME_PASSWORD(451, "用户名或密码错误"),

    /**
     * token失效
     */
    FAIL_TOKEN_INVALID(452, "token失效"),

// ================== 500 ====================
    /**
     * 服务器内部错误
     */
    FAIL_500(500, "服务器内部错误，无法完成请求"),

    /**
     * 服务不可用(应用维护中)
     */
    FAIL_503_MAINTAINING(503, "服务正在维护中"),;


    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
