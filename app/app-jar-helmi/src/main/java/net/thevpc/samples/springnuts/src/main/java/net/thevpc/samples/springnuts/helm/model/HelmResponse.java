package net.thevpc.samples.springnuts.helm.model;

public class HelmResponse {
    private String status;
    private String message;
    private Object data;
    
    public HelmResponse() {}
    
    public HelmResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
