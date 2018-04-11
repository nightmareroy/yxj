package com.wanniu.core.common;

/**
 * 通用提示异常
 * @author Yangzz
 *
 */
public class NoteException extends java.lang.Exception implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public String reason;

    public NoteException() {

    }

    public NoteException(String reason) {
        this.reason = reason;
    }
}
