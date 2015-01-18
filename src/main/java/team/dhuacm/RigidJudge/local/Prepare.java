package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wujy on 15-1-18.
 */
public class Prepare {

    private final static Logger logger = LoggerFactory.getLogger(Prepare.class.getSimpleName());

    public static boolean doPrepare(Solution solution) {
        // download  // TODO
        LocalProblem problem = (LocalProblem) solution.getProblem();
        solution.setInput(getFileContent(new File(problem.getInputFileName())));
        solution.setStdAns(getFileContent(new File(problem.getOutputFileName())));

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
            raf.close();
        } catch (IOException e) {
            logger.error(null, e);
        }
        return fileContent;
    }
}
