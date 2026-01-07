package it.allitude.legacyserviceweb.models;

public class SpoolFileListItem {
    String spoolfileName;
    int spoolNumber;
    String status;
    String creation_ts;
    String userData;
    int size;
    int pages;
    String jobName;
    String jobUser;
    String jobNumber;
    String outputQueueName;
    String outputQueueLibrary;

    public String getOutputQueueLibrary() {
        return outputQueueLibrary;
    }
    public void setOutputQueueLibrary(String outputQueueLibrary) {
        this.outputQueueLibrary = outputQueueLibrary;
    }
    public String getSpoolfileName() {
        return spoolfileName;
    }
    public void setSpoolfileName(String name) {
        this.spoolfileName = name;
    }
    public int getSpoolNumber() {
        return spoolNumber;
    }
    public void setSpoolNumber(int number) {
        this.spoolNumber = number;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCreation_ts() {
        return creation_ts;
    }
    public void setCreation_ts(String creation_ts) {
        this.creation_ts = creation_ts;
    }
    public String getUserData() {
        return userData;
    }
    public void setUserData(String userData) {
        this.userData = userData;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getPages() {
        return pages;
    }
    public void setPages(int pages) {
        this.pages = pages;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobUser() {
        return jobUser;
    }
    public void setJobUser(String jobUser) {
        this.jobUser = jobUser;
    }
    public String getJobNumber() {
        return jobNumber;
    }
    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }
    public String getOutputQueueName() {
        return outputQueueName;
    }
    public void setOutputQueueName(String outputQueue) {
        this.outputQueueName = outputQueue;
    }
}
