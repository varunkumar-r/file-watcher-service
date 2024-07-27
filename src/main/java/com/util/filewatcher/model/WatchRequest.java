package com.util.filewatcher.model;

public class WatchRequest {

    private String sourceDir;
    private String targetDir;
    private boolean logOnly;

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public boolean isLogOnly() {
        return logOnly;
    }

    public void setLogOnly(boolean logOnly) {
        this.logOnly = logOnly;
    }

    @Override
    public String toString() {
        return "WatchRequest{" +
                "sourceDir='" + sourceDir + '\'' +
                ", targetDir='" + targetDir + '\'' +
                ", logOnly=" + logOnly +
                '}';
    }
}
