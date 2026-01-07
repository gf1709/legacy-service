package it.allitude.legacyserviceweb.models;

public class WRKACTJOB_Filter {

    String userName;
    String jobName;
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

}
