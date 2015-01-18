package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;

/**
 * Created by wujy on 15-1-7.
 */
public class Solution {
    private int id;
    private Problem problem;
    private String code;
    private Language language;

    private Result result;
    private String compileInfo;
    private int time;
    private int memory;
    private String input;
    private String stdAns;
    private String output;

    public Solution() {

    }

    public Solution(int id, Problem problem, String code, Language language) {
        this.id = id;
        this.problem = problem;
        this.code = code;
        this.language = language;
        this.result = Result.Queue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getStdAns() {
        return stdAns;
    }

    public void setStdAns(String stdAns) {
        this.stdAns = stdAns;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
