package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

public class JobDetailInfoResponseDTO implements Serializable {

    String userName;
    String jobName;
    String jobNumber;
    String jobDate;
    String subSystem;
    String function;
    String statusExtended;
    String cpuUsed; // in millisecondi
    String userDescription;
    String loggingText;
    int loggingLevel;
    String loggingCLPrograms;
    String jobSwitches;

    ArrayList<String> libraryList;
    ArrayList<JobDetailInfoResponseDTOOpenFileItem> openFiles;
    ArrayList<JobDetailInfoResponseDTOCallStackItem> callStack;

    public JobDetailInfoResponseDTO(JobDetailInfoRequestDTO in) {
        this.userName = in.userName;
        this.jobName = in.jobName;
        this.jobNumber = in.jobNumber;
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

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getStatusExtended() {
        return statusExtended;
    }

    public void setStatusExtended(String statusExtended) {
        this.statusExtended = statusExtended;
    }

    public ArrayList<String> getLibraryList() {
        return libraryList;
    }

    public void setLibraryList(ArrayList<String> libraryList) {
        this.libraryList = libraryList;
    }

    public ArrayList<JobDetailInfoResponseDTOOpenFileItem> getOpenFiles() {
        return openFiles;
    }

    public void setOpenFiles(ArrayList<JobDetailInfoResponseDTOOpenFileItem> openFiles) {
        this.openFiles = openFiles;
    }

    public ArrayList<JobDetailInfoResponseDTOCallStackItem> getCallStack() {
        return callStack;
    }

    public void setCallStack(ArrayList<JobDetailInfoResponseDTOCallStackItem> callStack) {
        this.callStack = callStack;
    }

    public String getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(String cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    String tempStorageUsed; // Memoria temporanea utilizzata in MB

    public String getTempStorageUsed() {
        return tempStorageUsed;
    }

    public void setTempStorageUsed(String tempStorageUsed) {
        this.tempStorageUsed = tempStorageUsed;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getLoggingText() {
        return loggingText;
    }

    public void setLoggingText(String loggingText) {
        this.loggingText = loggingText;
    }

    public int getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(int loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public String getLoggingCLPrograms() {
        return loggingCLPrograms;
    }

    public void setLoggingCLPrograms(String loggingCLPrograms) {
        this.loggingCLPrograms = loggingCLPrograms;
    }

    public String getJobSwitches() {
        return jobSwitches;
    }

    public void setJobSwitches(String jobSwitches) {
        this.jobSwitches = jobSwitches;
    }

}
