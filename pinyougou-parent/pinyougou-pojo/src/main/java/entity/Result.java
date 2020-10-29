package entity;

import java.io.Serializable;

/**
 * <h3>pinyougou</h3>
 * <p>返回结果</p>
 *
 * @author : 沈疴
 * @date : 2020-09-22 17:31
 **/
public class Result implements Serializable {
    private String message;
    private boolean success;

    public Result( boolean success,String message) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
