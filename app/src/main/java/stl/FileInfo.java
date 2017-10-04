package stl;

import java.io.Serializable;

/**
 * Created by If Chan on 2017/10/4.
 */

public class FileInfo implements Serializable{
    private int id;
    private String url;
    private String fileNane;
    private int length;
    private int finished;

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileNane='" + fileNane + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }

    public FileInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileNane() {
        return fileNane;
    }

    public void setFileNane(String fileNane) {
        this.fileNane = fileNane;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }


    public FileInfo(int id, String url, String fileNane, int length, int finished) {
        this.id = id;
        this.url = url;
        this.fileNane = fileNane;
        this.length = length;
        this.finished = finished;
    }


}
