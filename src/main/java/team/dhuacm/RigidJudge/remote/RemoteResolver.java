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

    private Solution solution;
    private OJ oj;
    private OJProperty ojProperty;
    private OJAccount ojAccount;
    private CloseableHttpClient client = HttpClientUtil.get(DataProvider.Remote_RetryTimes, DataProvider.Remote_SocketTimeout, DataProvider.Remote_ConnectionTimeout);
    private final static Logger logger = LoggerFactory.getLogger(RemoteResolver.class.getSimpleName());

    public RemoteResolver(Solution solution) throws InterruptedException {
        this.solution = solution;
        oj = ((RemoteProblem)solution.getProblem()).getOj();
        logger.info("{} {} - solution id: {}", oj.toString(), ((RemoteProblem)solution.getProblem()).getOjIndex(), solution.getId());
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
                    // 提交失败，代码长度不符合规定， Compile Error
                    logger.info("Submit failed! Compile_Error!");
                    solution.setResult(Result.Compile_Error);
                }
            } else {
                // 登录失败，可能是用户名密码错误，Judge Error
                logger.info("Login failed! Judge_Error");
                solution.setResult(Result.Judge_Error);
            }
        } catch (JudgeException e) {
            logger.error("Judge_Error!");
            solution.setResult(Result.Judge_Error);
        } catch (NetworkException e) {
            logger.error("Network_Error!");
            solution.setResult(Result.Network_Error);
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported Encoding!", e);
            solution.setResult(Result.Judge_Error);
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
