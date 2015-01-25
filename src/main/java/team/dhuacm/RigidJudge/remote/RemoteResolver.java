package team.dhuacm.RigidJudge.remote;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.*;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteResolver {

    private final Solution solution;
    private final OJ oj;
    private final OJProperty ojProperty;
    private OJAccount ojAccount;
    private final CloseableHttpClient client = HttpClientUtil.get(DataProvider.Remote_RetryTimes, DataProvider.Remote_SocketTimeout, DataProvider.Remote_ConnectionTimeout);
    private static final Logger logger = LoggerFactory.getLogger(RemoteResolver.class.getSimpleName());

    public RemoteResolver(Solution solution) throws InterruptedException {
        this.solution = solution;
        oj = ((RemoteProblem) solution.getProblem()).getOj();
        logger.info("{} {} - solution id: {}", oj, ((RemoteProblem) solution.getProblem()).getOjIndex(), solution.getId());
        ojProperty = DataProvider.OJs.get(oj);
        ojAccount = DataProvider.Remote_OJAccounts.get(oj).take();
        logger.info("Account: {}", ojAccount.getUsername());
    }

    public void handle() {
        try {
            if (Login.doLogin(client, ojProperty, ojAccount)) {
                logger.info("Login success!");
                if (Submit.doSubmit(client, ojProperty, ojAccount, solution)) {
                    logger.info("Submit success!");
                    Query.doQuery(client, ojProperty, ojAccount, solution);
                } else {
                    logger.info("Submit failed! Compile_Error!");
                    solution.setResult(Result.Compile_Error);
                }
            } else {
                logger.info("Login failed! Judge_Error");
                solution.setResult(Result.Judge_Error);
            }
        } catch (JudgeException e) {
            logger.error("Judge_Error!");
            solution.setResult(Result.Judge_Error);
        } catch (NetworkException e) {
            logger.error("Network_Error!");
            solution.setResult(Result.Network_Error);
        } catch (Exception e) {
            logger.error("Other_Error!", e);
            solution.setResult(Result.Other_Error);
        } finally {
            try {
                if (null != ojAccount) {
                    DataProvider.Remote_OJAccounts.get(oj).put(ojAccount);
                }
                if (null != client)
                    client.close();
            } catch (InterruptedException e) {
                logger.error(null, e);
            } catch (IOException e) {
                logger.error(null, e);
            }
        }
    }
}
