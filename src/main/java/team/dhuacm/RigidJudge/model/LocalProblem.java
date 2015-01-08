package team.dhuacm.RigidJudge.model;

import team.dhuacm.RigidJudge.config.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujy on 15-1-8.
 */
public class LocalProblem extends Problem {
    protected String inputFileName;
    protected String outputFileName;
    protected Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
    protected Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();

}
