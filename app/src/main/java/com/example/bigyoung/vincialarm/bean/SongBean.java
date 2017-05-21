package com.example.bigyoung.vincialarm.bean;

/**
 * Created by BigYoung on 2017/5/21.
 */

public class SongBean {
    private String filePath;//文件路径
    private String fileName;//文件名
    private boolean isSelected;//是否被选中

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
