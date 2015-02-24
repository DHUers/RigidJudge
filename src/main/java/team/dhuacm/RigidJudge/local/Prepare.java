package team.dhuacm.RigidJudge.local;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Solution;
import team.dhuacm.RigidJudge.utils.FileUtils;
import team.dhuacm.RigidJudge.utils.HttpClientUtil;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wujy on 15-1-18.
 */
class Prepare {

    private static final Logger logger = LoggerFactory.getLogger(Prepare.class.getSimpleName());
    private static CloseableHttpClient httpClient = null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean doPrepare(Solution solution, String source) throws IOException {

        LocalProblem problem = (LocalProblem) solution.getProblem();

        try {
            prepareData(problem);
        } catch (Exception e) {
            logger.error(null, e);
            return false;
        }

        solution.setInput(FileUtils.getFileContent(new File("data/" + problem.getInputFileName().substring(problem.getInputFileName().lastIndexOf("/") + 1))));
        solution.setStdAns(FileUtils.getFileContent(new File("data/" + problem.getOutputFileName().substring(problem.getOutputFileName().lastIndexOf("/") + 1))));
        if (!solution.getInput().endsWith("\n")) {  // normalize I/O to prevent PE
            solution.setInput(solution.getInput() + "\n");
        }
        if (!solution.getStdAns().endsWith("\n")) {
            solution.setStdAns(solution.getStdAns() + "\n");
        }

        if (problem.getTimeLimit().containsKey(solution.getLanguage())) {
            solution.setTimeLimit(problem.getTimeLimit().get(solution.getLanguage()));
        } else {
            solution.setTimeLimit(problem.getTimeLimit().get(Language.DEFAULT));
        }
        if (problem.getMemoryLimit().containsKey(solution.getLanguage())) {
            solution.setMemoryLimit(problem.getMemoryLimit().get(solution.getLanguage()));
        } else {
            solution.setMemoryLimit(problem.getMemoryLimit().get(Language.DEFAULT));
        }

        File file = new File("tmp");
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(source + DataProvider.Local_FileSuffix.get(solution.getLanguage()));
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(solution.getCode());
        writer.close();

        // Prepare for Java solutions, copy Java wrapper (JavaWrapper.class)
        if (solution.getLanguage().equals(Language.JAVA)) {
            FileUtils.fileTransferCopy(new File("sandbox/JavaWrapper.class"), new File("tmp/JavaWrapper.class"));
        }

        // Prepare for Special judge, Pre-Compile SPJ
        if (problem instanceof LocalSpecialProblem) {
            LocalSpecialProblem specialProblem = (LocalSpecialProblem) problem;

            file = new File("tmp/spj" + DataProvider.Local_FileSuffix.get(specialProblem.getJudgerProgramLanguage()));
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(specialProblem.getJudgerProgramCode());
            writer.close();

            if (!Compile.doCompile(specialProblem.getJudgerProgramLanguage(), "tmp/spj", "tmp/spj")) {
                logger.error("SPJ compile error!");
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void prepareData(LocalProblem problem) throws JudgeException, NetworkException {
        File file;
        file = new File("data");
        if (!file.exists()) {
            file.mkdir();
        }

        String dataServerPrefix = "http://" + DataProvider.Local_DataServerHost + ":" + DataProvider.Local_DataServerPort;
        downloadIfNeed("data/" + problem.getInputFileName().substring(problem.getInputFileName().lastIndexOf("/") + 1), dataServerPrefix + problem.getInputFileName());
        downloadIfNeed("data/" + problem.getOutputFileName().substring(problem.getOutputFileName().lastIndexOf("/") + 1), dataServerPrefix + problem.getOutputFileName());
    }

    private static Date getLastModified(String url) throws JudgeException, NetworkException {
        Date lastModified = null;
        HttpHead head = new HttpHead(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(head);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            lastModified = sdf.parse(response.getFirstHeader("last-modified").getValue());
            logger.info("Last-Modified: {}", lastModified);
        } catch (ClientProtocolException e) {
            throw new JudgeException(e.getMessage(), e.getCause());
        } catch (ParseException e) {
            throw new JudgeException(e.getMessage(), e.getCause());
        } catch (Exception e) {
            throw new NetworkException(e.getMessage(), e.getCause());
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                logger.error(null, e);
            }
            head.releaseConnection();
        }
        return lastModified;
    }

    private static void downloadIfNeed(String filename, String url) throws JudgeException, NetworkException {
        if (filename.startsWith("data/test")) return;
        File file = new File(filename);

        if (!file.exists() || getLastModified(url).compareTo(new Date(file.lastModified())) > 0) {
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(get);
                HttpEntity entity = response.getEntity();
                BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filename)));
                int inByte;
                while ((inByte = bis.read()) != -1) {
                    bos.write(inByte);
                }
                bis.close();
                bos.close();
                logger.info("Downloaded '{}'", filename);
            } catch (ClientProtocolException e) {
                throw new JudgeException(e.getMessage(), e.getCause());
            } catch (IOException e) {
                throw new NetworkException(e.getMessage(), e.getCause());
            } finally {
                try {
                    if (null != response)
                        response.close();
                } catch (IOException e) {
                    logger.error(null, e);
                }
                get.releaseConnection();
            }
        } else {
            logger.info("Already cached '{}'", filename);
        }
    }

    static {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(DataProvider.Local_DataServerHost, DataProvider.Local_DataServerPort),
                new UsernamePasswordCredentials(DataProvider.Local_DataServerUsername, DataProvider.Local_DataServerPassword));
        httpClient = HttpClientUtil.get(DataProvider.Remote_RetryTimes, DataProvider.Remote_SocketTimeout, DataProvider.Remote_ConnectionTimeout, credentialsProvider);
    }
}
