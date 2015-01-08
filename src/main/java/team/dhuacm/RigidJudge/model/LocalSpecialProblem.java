package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;

import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalSpecialProblem extends LocalProblem {
    private String judgerProgramName;
    private Language judgerProgramLanguage;

    public LocalSpecialProblem(int id, int judgeType, String inputFileName, String outputFileName, Map<Language, Integer> timeLimit, Map<Language, Integer> memoryLimit, String judgerProgramName, Language judgerProgramLanguage) {
        super(id, judgeType, inputFileName, outputFileName, timeLimit, memoryLimit);
        this.judgerProgramName = judgerProgramName;
        this.judgerProgramLanguage = judgerProgramLanguage;
    }

    public String getJudgerProgramName() {
        return judgerProgramName;
    }

    public void setJudgerProgramName(String judgerProgramName) {
        this.judgerProgramName = judgerProgramName;
    }

    public Language getJudgerProgramLanguage() {
        return judgerProgramLanguage;
    }

    public void setJudgerProgramLanguage(Language judgerProgramLanguage) {
        this.judgerProgramLanguage = judgerProgramLanguage;
    }
}
