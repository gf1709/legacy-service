package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class JobInfoRequestDTO implements Serializable {
	String userName;
    String jobName;
    String jobNumber;
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
	public String getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	public JobInfoRequestDTO(String userName, String jobName, String jobNumber) {
		this.userName = userName;
		this.jobName = jobName;
		this.jobNumber = jobNumber;
	}


}