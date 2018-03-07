/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

public class ErrorInFileException extends Exception {

    public static final long serialVersionUID = 0;

    public ErrorInFileException(String msg) {
        super(msg);
    }

    public ErrorInFileException() {
        super();
    }

}
