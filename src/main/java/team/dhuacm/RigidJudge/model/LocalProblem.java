package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalProblem extends Problem {
    private String inputFileUrl;
    protected String input;
    private String outputFileUrl;
    protected String output;
    private String limitType;
    private Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
    private Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();

    public LocalProblem(int id, String inputFileName, String outputFileName, String limitType, Map<Language, Integer> timeLimit, Map<Language, Integer> memoryLimit) {
        super(id);
        this.inputFileUrl = inputFileName;
        this.outputFileUrl = outputFileName;
        this.limitType = limitType;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public String getInputFileUrl() {
        return inputFileUrl;
    }

    public void setInputFileUrl(String inputFileUrl) {
        this.inputFileUrl = inputFileUrl;
    }

    public String getOutputFileUrl() {
        return outputFileUrl;
    }

    public void setOutputFileUrl(String outputFileUrl) {
        this.outputFileUrl = outputFileUrl;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public Map<Language, Integer> getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Map<Language, Integer> timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Map<Language, Integer> getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Map<Language, Integer> memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
}
