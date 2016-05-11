package ahmadalinasir.com.noodleconnect;

/**
 * Created by ahmadalinasir on 7/21/15.
 *
 */

public class ErrorModel extends BaseModel {
    private int status;
    private String exception;
    private String request;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
