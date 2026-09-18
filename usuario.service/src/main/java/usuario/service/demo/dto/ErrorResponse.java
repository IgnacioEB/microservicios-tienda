package usuario.service.demo.dto;

public class ErrorResponse {
    private String message, error;
    private Integer status;

    public ErrorResponse(String message, String error, Integer status) {
        this.message = message;
        this.error = error;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
