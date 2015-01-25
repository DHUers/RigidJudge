package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.*;

/**
 * Created by wujy on 15-1-18.
 */
public class Prepare {

    private final static Logger logger = LoggerFactory.getLogger(Prepare.class.getSimpleName());

    public static boolean doPrepare(Solution solution) throws IOException {
        // download  // TODO
        LocalProblem problem = (LocalProblem) solution.getProblem();
        solution.setInput(getFileContent(new File(problem.getInputFileName())));
        solution.setStdAns(getFileContent(new File(problem.getOutputFileName())));
        solution.setTimeLimit(problem.getTimeLimit().get(solution.getLanguage()));
        solution.setMemoryLimit(problem.getMemoryLimit().get(solution.getLanguage()));
        //System.out.println(solution.getInput());
        //System.out.println(solution.getStdAns());
        File file;
        if (solution.getLanguage().equals(Language.C)) {  // Temporarily, will change to DataProvider
            file = new File("test.c");
        } else if (solution.getLanguage().equals(Language.CPP)) {
            file = new File("test.cpp");
        } else {
            file = new File("test.java");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(solution.getCode());
        writer.close();

        // pre-compile special judge code
        if (problem instanceof LocalSpecialProblem) {
            File judgerProgramFile = new File(((LocalSpecialProblem) problem).getJudgerProgramName());
            // Compile  // TODO
        }

        return true;
    }

    private static String getFileContent(File file) {
        String fileContent = "";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            byte[] fileBytes = new byte[(int) raf.length()];
            raf.read(fileBytes);
            fileContent = new String(fileBytes);
            fileContent = fileContent.replace("\r\n", "\n");
            if (fileContent.endsWith("\n")) {
                fileContent = fileContent.substring(0, fileContent.length()-1);
            }
            raf.close();
        } catch (IOException e) {
            logger.error(null, e);
        }
        return fileContent;
    }
}
