package com.geetest.stringguard.plugin;

import java.util.Arrays;

public class Config {
    private boolean enable;
    /**
     * 混淆模式：
     *  0：value/key 加解密模式
     *  1：value     无key加解密模式
     *  2：          隐藏数组模式
     */
    private int guardMode;
    private String key;
    private boolean debug;
    private String[] includePackages;
    private String[] excludePackages;
    private String[] includeClasses;
    private String[] excludeClasses;
    private String implementation;

    public Config(boolean enable,
                  int guardMode,
                  String key,
                  boolean debug,
                  String[] includePackages,
                  String[] excludePackages,
                  String[] includeClasses,
                  String[] excludeClasses,
                  String implementation) {
        this.enable = enable;
        this.guardMode = guardMode;
        this.key = key;
        this.debug = debug;
        this.includePackages = includePackages;
        this.excludePackages = excludePackages;
        this.includeClasses = includeClasses;
        this.excludeClasses = excludeClasses;
        this.implementation = implementation;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getGuardMode() {
        return guardMode;
    }

    public void setGuardMode(int guardMode) {
        this.guardMode = guardMode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
                "enable=" + enable +
                ", guardMode=" + guardMode +
                ", key='" + key + '\'' +
                ", debug=" + debug +
                ", includePackages=" + Arrays.toString(includePackages) +
                ", excludePackages=" + Arrays.toString(excludePackages) +
                ", includeClasses=" + Arrays.toString(includeClasses) +
                ", excludeClasses=" + Arrays.toString(excludeClasses) +
                ", implementation='" + implementation + '\'' +
                '}';
    }
}
