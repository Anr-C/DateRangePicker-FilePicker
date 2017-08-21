package com.lckiss.rangedatepicker.lib.filepicker.entity;


import com.lckiss.rangedatepicker.lib.common.entity.JavaBean;

/**
 * 文件项信息
 */
public class FileItem extends JavaBean {
    private int icon;
    private String name;
    private String path = "/";
    private long size = 0;
    private boolean isDirectory = false;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

}
