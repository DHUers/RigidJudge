package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalProblem extends Problem {
    protected String inputFileName;
    protected String input;
    protected String outputFileName;
    protected String output;
    protected Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
    protected Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();

    public LocalProblem(int id, String judgeType, String inputFileName, String outputFileName, Map<Language, Integer> timeLimit, Map<Language, Integer> memoryLimit) {
        super(id, judgeType);
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
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
