package com.pyg.utils;

import java.io.Serializable;

/**
 * Created by on 2018/8/6.
 */
public class PygResult implements Serializable{

    public PygResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    private boolean success;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
