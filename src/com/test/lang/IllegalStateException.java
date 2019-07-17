package com.test.lang;

import java.io.Serializable;

/**
 * @Created by JYB
 * @Date 2019/7/9 15:28
 * @Description TODO
 */
public class IllegalStateException extends RuntimeException {
    public IllegalStateException(){
        super();
    }

    public IllegalStateException(String s){super(s);}

    public IllegalStateException(String message,Throwable cause){super(message,cause);}

    public IllegalStateException(Throwable cause){super(cause);}

    static final long serialVersionUID = -1848914673093119416L;
}
