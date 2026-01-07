package it.allitude.legacyserviceweb.DTOs;

import java.util.ArrayList;

public class IFSListFileResponseDTO {


    class IFSListFileResponseItem
    {   
        String type;
        String name;
        long size;
        String changeDate;

        public IFSListFileResponseItem(String name, String type, long  size, String changeDate) {
            this.changeDate = changeDate;
            this.name = name;
            this.size = size;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public String getChangeDate() {
            return changeDate;
        }
    }

    String directory;
    ArrayList<IFSListFileResponseItem> files = new ArrayList<>();
    public String getDirectory() {
        return directory;
    }

    public ArrayList<IFSListFileResponseItem> getFiles() {
        files.sort(
                (a, b) -> {
                    return a.name.compareTo(b.name);
                });
        return files;
    }
    
    public IFSListFileResponseDTO(String directory) {
        this.directory = directory;
    }
    public void addItem(String name,String type,long size,String changeDate)
    {
        IFSListFileResponseItem ni = new IFSListFileResponseItem(name, type, size, changeDate);
        files.add(ni);
    }


    
}
