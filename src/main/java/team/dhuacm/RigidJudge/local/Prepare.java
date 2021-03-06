package team.dhuacm.RigidJudge.local;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    public static void doPrepare(Solution solution, String source) throws IOException, JudgeException, NetworkException {
        LocalProblem problem = (LocalProblem) solution.getProblem();

        prepareData(problem);

        solution.setInput(FileUtils.getFileContent(new File("data/" + problem.getInputFileUrl().replace("/store/", "").replace("/", "_"))));
        solution.setStdAns(FileUtils.getFileContent(new File("data/" + problem.getOutputFileUrl().replace("/store/", "").replace("/", "_"))));
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

        file = new File(source + DataProvider.LOCAL_FILE_SUFFIX.get(solution.getLanguage()));
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

            file = new File("tmp/spj" + DataProvider.LOCAL_FILE_SUFFIX.get(specialProblem.getJudgerProgramLanguage()));
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(specialProblem.getJudgerProgramCode());
            writer.close();

            if (!Compile.doCompile(specialProblem.getJudgerProgramLanguage(), "tmp/spj", "tmp/spj")) {
                throw new JudgeException("SPJ compile error!" + Compile.compileInfo);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void prepareData(LocalProblem problem) throws JudgeException, NetworkException {
        File file;
        file = new File("data");
        if (!file.exists()) {
            file.mkdir();
        }

        String dataServerPrefix = "http://" + DataProvider.LOCAL_DATA_SERVER_URL + "/attachments/";
        downloadIfNeed("data/" + problem.getInputFileUrl().replace("/store/", "").replace("/", "_"), dataServerPrefix + getToken(problem.getInputFileUrl()) + problem.getInputFileUrl());
        downloadIfNeed("data/" + problem.getOutputFileUrl().replace("/store/", "").replace("/", "_"), dataServerPrefix + getToken(problem.getOutputFileUrl()) + problem.getOutputFileUrl());
    }

    private static String getToken(String text) throws JudgeException {
        try {
            Mac hmacSha1;
            try {
                hmacSha1 = Mac.getInstance("HmacSHA1");
            } catch (Exception e) {
                hmacSha1 = Mac.getInstance("HMAC-SHA-1");
            }
            hmacSha1.init(new SecretKeySpec(DataProvider.LOCAL_DATA_SERVER_TOKEN.getBytes(), "RAW"));
            String hexString = Hex.encodeHexString(hmacSha1.doFinal(text.getBytes()));
            logger.debug("'{}' => '{}'", text, hexString);
            return hexString;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JudgeException(e.getMessage(), e.getCause());
        }
    }

    @Deprecated
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void downloadIfNeed(String filename, String url) throws JudgeException, NetworkException {
        if (filename.contains("test.")) return;
        File file = new File(filename);

        //if (!file.exists() || getLastModified(url).compareTo(new Date(file.lastModified())) > 0) {
        if (!file.exists()) {
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
        httpClient = HttpClientUtil.get(DataProvider.REMOTE_RETRY_TIMES, DataProvider.REMOTE_SOCKET_TIMEOUT, DataProvider.REMOTE_CONNECTION_TIMEOUT, credentialsProvider);
    }
}
