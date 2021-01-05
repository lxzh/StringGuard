package com.geetest.stringguard.plugin;

import java.util.Arrays;

public class Config {
    private boolean useKey;
    private String key;
    private boolean enable;
    private boolean debug;
    private String[] includePackages;
    private String[] excludePackages;
    private String[] includeClasses;
    private String[] excludeClasses;
    private String implementation;

    public Config(boolean useKey,
                  String key,
                  boolean enable,
                  boolean debug,
                  String[] includePackages,
                  String[] excludePackages,
                  String[] includeClasses,
                  String[] excludeClasses,
                  String implementation) {
        this.useKey = useKey;
        this.key = key;
        this.enable = enable;
        this.debug = debug;
        this.includePackages = includePackages;
        this.excludePackages = excludePackages;
        this.includeClasses = includeClasses;
        this.excludeClasses = excludeClasses;
        this.implementation = implementation;
    }

    public boolean isUseKey() {
        return useKey;
    }

    public void setUseKey(boolean useKey) {
        this.useKey = useKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String[] getIncludePackages() {
        return includePackages;
    }

    public void setIncludePackages(String[] includePackages) {
        this.includePackages = includePackages;
    }

    public String[] getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(String[] excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String[] getIncludeClasses() {
        return includeClasses;
    }

    public void setIncludeClasses(String[] includeClasses) {
        this.includeClasses = includeClasses;
    }

    public String[] getExcludeClasses() {
        return excludeClasses;
    }

    public void setExcludeClasses(String[] excludeClasses) {
        this.excludeClasses = excludeClasses;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    @Override
    public String toString() {
        return "Config{" +
                "useKey=" + useKey +
                ", key='" + key + '\'' +
                ", enable=" + enable +
                ", debug=" + debug +
                ", includePackages=" + Arrays.toString(includePackages) +
                ", excludePackages=" + Arrays.toString(excludePackages) +
                ", includeClasses=" + Arrays.toString(includeClasses) +
                ", excludeClasses=" + Arrays.toString(excludeClasses) +
                ", implementation='" + implementation + '\'' +
                '}';
    }
}
