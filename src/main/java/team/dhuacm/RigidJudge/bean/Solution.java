package team.dhuacm.RigidJudge.bean;

import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;

/**
 * Created by wujy on 15-1-7.
 */
public class Solution {
    private int id;
    private String code;
    private Language language;
    private Result result;
    private String compileInfo;
    private int time;
    private int memory;

    public String getCompileInfo() {
        return compileInfo;
    }

    public void setCompileInfo(String compileInfo) {
        this.compileInfo = compileInfo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

}
