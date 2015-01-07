package team.dhuacm.RigidJudge.bean;

/**
 * Created by wujy on 15-1-7.
 */
public class Problem {
    private int id;
    private int judgeType;
    private String inputFileName;
    private String outputFileName;
    private String specialJudgeFileName;

    private boolean prepare() {
        // TODO
        // download

        // check

        // pre-compile special judge code

        // store

        return true;
    }

    public boolean load() {
        // TODO
        prepare();

        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(int judgeType) {
        this.judgeType = judgeType;
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

    public String getSpecialJudgeFileName() {
        return specialJudgeFileName;
    }

    public void setSpecialJudgeFileName(String specialJudgeFileName) {
        this.specialJudgeFileName = specialJudgeFileName;
    }
}
