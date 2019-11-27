package com.zyan.miaosha.exception;

import com.zyan.miaosha.result.CodeMsg;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-23 下午4:00
 */
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
