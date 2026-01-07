package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class WRKACTJOBRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	String userName;
    String jobName;
    String subSystemName;
    String jobNumber;
	boolean sortByJobName;
    boolean sortByJobStatus;
	public boolean isSortByJobName() {
		return sortByJobName;
	}
	public void setSortByJobName(boolean sortByJobName) {
		this.sortByJobName = sortByJobName;
	}
	public boolean isSortByJobStatus() {
		return sortByJobStatus;
	}
	public void setSortByJobStatus(boolean sortByJobStatus) {
		this.sortByJobStatus = sortByJobStatus;
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
	public String getSubSystemName() {
		return subSystemName;
	}
	public void setSubSystemName(String subSystemName) {
		this.subSystemName = subSystemName;
	}
	public String getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	public WRKACTJOBRequestDTO(String userName, String jobName, String subSystemName, String jobNumber) {
		this.userName = userName;
		this.jobName = jobName;
		this.subSystemName = subSystemName;
		this.jobNumber = jobNumber;
	}

}