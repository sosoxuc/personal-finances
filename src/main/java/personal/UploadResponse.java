package personal;

import java.io.Serializable;

public class UploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean success;

    public String fileName;

    public String originalName;

    public String error;

    public UploadResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public UploadResponse(boolean success) {
        this(success, "");
    }
}
