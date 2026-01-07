package it.allitude.legacyserviceweb.models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileFilter;
import com.ibm.as400.access.IFSFileReader;

import it.allitude.legacyserviceweb.DTOs.IFSListFileResponseDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesIFSUtil {

    private final AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ISeriesIFSUtil.class);
    private final ConnectionService _connectionService;

    public ISeriesIFSUtil(ConnectionService anISeriesConnectionService, AppConfig appConfig, AmbientiBanca ambientiBanca, ISeriesObjectUtil objectUtil) {
        this._connectionService = anISeriesConnectionService;
        this.appConfig = appConfig;
    }

    public IFSListFileResponseDTO listFiles(String aDirectory, String aPattern) throws Exception {
        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile directory = new IFSFile(as400, aDirectory);
        if (!directory.exists()) {
            throw new FileNotFoundException("The directoy " + directory + " does not exist");
        }
        String absDirPath = org.springframework.util.StringUtils.cleanPath(aDirectory);
        IFSListFileResponseDTO res = new IFSListFileResponseDTO(absDirPath);
        IFSFile[] directoryFiles = directory.listFiles(new MyDirectoryFilter(), aPattern);
        if (directoryFiles == null) {
            log.error("The directory does not exist");
            return res;
        } else if (directoryFiles.length == 0) {
            log.info("The directory is empty");
            return res;
        }
        for (IFSFile f : directoryFiles) {
            String fname = f.getName();
            long changeDate = f.lastModified();
            java.util.Date cdate = new java.util.Date(changeDate);

            String type = "f";
            if (f.isDirectory()) {
                type = "d";
            }
            long size = f.length();
            res.addItem(fname, type, size, simpleDateFormat.format(cdate));
        }
        return res;
    }

    public ArrayList<String> getIFSFileContent(String aFileName) throws Exception {
        String line;
        ArrayList<String> res = new ArrayList<>();
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile file = new IFSFile(as400, aFileName);
        try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
            while ((line = reader.readLine()) != null) {
                res.add(line);
            }
        }
        return res;
    }

    public ArrayList<Byte> getIFSFileContentZipped(String aFileName) throws Exception {
        return zipFile(aFileName);
    }

    public ArrayList<Byte> zipFile(String aFileName) throws Exception {
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile sourceFile = new IFSFile(as400, aFileName);
        String compressedFileName = sourceFile.getAbsolutePath() + ".zip";
        CommandCall cmd = new CommandCall(as400);
        String cmdString = "jar  -cMf " + compressedFileName + " -C " + sourceFile.getParent() + " " + sourceFile.getName();
        boolean isOk = cmd.run("QSH CMD('" + cmdString + "')");
        AS400Message[] messageList = cmd.getMessageList();
        if (!isOk) {
            if (messageList.length > 0) {
                throw new Exception("zipFile error: " + messageList[0].getText());
            }
        }

        IFSFile f = new IFSFile(as400, compressedFileName);
        ArrayList<Byte> result;
        try (IFSFileReader res = new IFSFileReader(f)) {
            int fLen = (int) f.length();        
            int data;
            int i = 0;
            result = new ArrayList<>(fLen);
            while ((data = res.read()) != -1) {
                byte b = (byte)data;                
                result.add(new Byte(b));
                i++;

            }
        }
        return result;        
    }

    public ArrayList<String> getIFSFilesContent(ArrayList<String> aFileNames) throws Exception {
        String line;
        ArrayList<String> res = new ArrayList<>();
        AS400 as400 = _connectionService.getAS400Connection();
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            try (BufferedReader reader = new BufferedReader(new IFSFileReader(file))) {
                while ((line = reader.readLine()) != null) {
                    {
                        res.add("[" + fName + "] - " + line);
                    }
                }
            }
        }
        return res;
    }

    public boolean deleteIFSFile(String aFileName) throws Exception {
        AS400 as400 = _connectionService.getAS400Connection();
        IFSFile file = new IFSFile(as400, aFileName);
        return file.delete();
    }

    public boolean deleteIFSFiles(ArrayList<String> aFileNames) throws Exception {
        boolean allDeleted = true;
        AS400 as400 = _connectionService.getAS400Connection();
        for (String fName : aFileNames) {
            IFSFile file = new IFSFile(as400, fName);
            try {
                file.delete();
            } catch (Exception e) {
                log.error("Error deleting file: " + fName, e);
                allDeleted = false;
            }
        }
        return allDeleted;
    }
}

class MyDirectoryFilter implements IFSFileFilter {

    public boolean accept(IFSFile file) {
        try {
            // Keep this entry.  Returning true tells the IFSList object  to return this file in the list of entries returned to the  .list() method.
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
