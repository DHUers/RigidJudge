package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.*;

/**
 * Created by wujy on 15-1-18.
 */
public class Prepare {

    private final static Logger logger = LoggerFactory.getLogger(Prepare.class.getSimpleName());

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean doPrepare(Solution solution) throws IOException {
        // download  // TODO
        File file;
        file = new File("data");
        if (!file.exists()) {
            file.mkdir();
        }

        LocalProblem problem = (LocalProblem) solution.getProblem();
        solution.setInput(FileUtils.getFileContent(new File("data/" + problem.getInputFileName())));
        solution.setStdAns(FileUtils.getFileContent(new File("data/" + problem.getOutputFileName())));
        solution.setTimeLimit(problem.getTimeLimit().get(solution.getLanguage()));
        solution.setMemoryLimit(problem.getMemoryLimit().get(solution.getLanguage()));
        //System.out.println(solution.getInput());
        //System.out.println(solution.getStdAns());

        file = new File("tmp");
        if (!file.exists()) {
            file.mkdir();
        }
        if (solution.getLanguage().equals(Language.C)) {  // Temporarily, will change to DataProvider
            file = new File("tmp/test.c");
        } else if (solution.getLanguage().equals(Language.CPP)) {
            file = new File("tmp/test.cpp");
        } else {
            file = new File("tmp/test.java");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(solution.getCode());
        writer.close();

        // Prepare for Special judge, copy input/output data and pre-compile spj
        if (problem instanceof LocalSpecialProblem) {
            FileUtils.fileTransferCopy(new File("data/" + problem.getInputFileName()), new File("tmp/test.in"));
            FileUtils.fileTransferCopy(new File("data/" + problem.getOutputFileName()), new File("tmp/test.out"));

            if (((LocalSpecialProblem) problem).getJudgerProgramLanguage().equals(Language.C)) {  // Temporarily, will change to DataProvider
                file = new File("tmp/spj.c");
            } else if (((LocalSpecialProblem) problem).getJudgerProgramLanguage().equals(Language.CPP)) {
                file = new File("tmp/spj.cpp");
            } else {
                file = new File("tmp/spj.java");
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(((LocalSpecialProblem) problem).getJudgerProgramCode());
            writer.close();

            if (!Compile.doCompile(((LocalSpecialProblem) problem).getJudgerProgramLanguage(), "tmp/spj", "tmp/spj")) {
                logger.error("SPJ compile error!");
                solution.setResult(Result.Judge_Error);
                return false;
            }
        }

        return true;
    }

}
