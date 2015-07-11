package personal;

import java.io.Serializable;

public class UploadResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private String fileName;

    private String originalName;

    private String error;

    public UploadResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public UploadResponse(boolean success) {
        this(success, "");
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
