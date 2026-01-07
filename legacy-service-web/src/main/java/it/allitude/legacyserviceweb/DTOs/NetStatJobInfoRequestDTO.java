package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class NetStatJobInfoRequestDTO implements Serializable {

    private static final long serialVersionUID = 5926468583004150707L;

    int port;
    String userName;
    String jobName;

    public int getPort() {
        return port;
    }

    public void setPort(int value) {
        port = value;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

}
