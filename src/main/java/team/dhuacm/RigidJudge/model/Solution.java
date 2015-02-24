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
    private String executeInfo;
    private String compareInfo;
    private long time;
    private long memory;
    private String output;

    private long timeLimit;
    private long memoryLimit;
    private String input;
    private String stdAns;

    public Solution(Solution solution) {
        this.id = solution.id;
        this.problem = solution.problem;
        this.code = solution.code;
        this.language = solution.language;
        this.result = solution.result;
        this.compileInfo = solution.compileInfo;
        this.executeInfo = solution.executeInfo;
        this.compareInfo = solution.compareInfo;
        this.time = solution.time;
        this.memory = solution.memory;
        this.output = solution.output;
        this.timeLimit = solution.timeLimit;
        this.memoryLimit = solution.memoryLimit;
        this.input = solution.input;
        this.stdAns = solution.stdAns;
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

    public String getExecuteInfo() {
        return executeInfo;
    }

    public void setExecuteInfo(String executeInfo) {
        this.executeInfo = executeInfo;
    }

    public String getCompareInfo() {
        return compareInfo;
    }

    public void setCompareInfo(String compareInfo) {
        this.compareInfo = compareInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(long memoryLimit) {
        this.memoryLimit = memoryLimit;
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
}
