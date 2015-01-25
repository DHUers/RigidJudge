package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;

import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalSpecialProblem extends LocalProblem {
    private String judgerProgramCode;
    private Language judgerProgramLanguage;

    public LocalSpecialProblem(int id, String judgeType, String inputFileName, String outputFileName, Map<Language, Integer> timeLimit, Map<Language, Integer> memoryLimit, String judgerProgramCode, Language judgerProgramLanguage) {
        super(id, judgeType, inputFileName, outputFileName, timeLimit, memoryLimit);
        this.judgerProgramCode = judgerProgramCode;
        this.judgerProgramLanguage = judgerProgramLanguage;
    }

    public String getJudgerProgramCode() {
        return judgerProgramCode;
    }

    public void setJudgerProgramCode(String judgerProgramCode) {
        this.judgerProgramCode = judgerProgramCode;
    }

    public Language getJudgerProgramLanguage() {
        return judgerProgramLanguage;
    }

    public void setJudgerProgramLanguage(Language judgerProgramLanguage) {
        this.judgerProgramLanguage = judgerProgramLanguage;
    }
}
