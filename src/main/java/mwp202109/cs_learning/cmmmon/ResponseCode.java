package mwp202109.cs_learning.cmmmon;


public enum ResponseCode {
    /**
     * 公共返回码
     */
    FAIL(500, "内部错误"),
    SUCCESS(200, "请求成功");


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
