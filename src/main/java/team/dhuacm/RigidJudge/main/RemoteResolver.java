package team.dhuacm.RigidJudge.main;

import org.apache.http.impl.client.CloseableHttpClient;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.config.OJAccount;
import team.dhuacm.RigidJudge.config.OJProperty;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;
import team.dhuacm.RigidJudge.util.HttpClientUtil;
import team.dhuacm.RigidJudge.util.Login;
import team.dhuacm.RigidJudge.util.Query;
import team.dhuacm.RigidJudge.util.Submit;

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
    private String info = Thread.currentThread().getName();

    public RemoteResolver(Solution solution) throws InterruptedException {
        this.solution = solution;
        oj = ((RemoteProblem)solution.getProblem()).getOj();
        System.out.println(info + " - " + oj.toString() + " " + ((RemoteProblem)solution.getProblem()).getOjIndex() + " - solution id: " + solution.getId());
        ojProperty = DataProvider.OJs.get(oj);
        ojAccount = DataProvider.Remote_OJAccounts.get(oj).take();
        System.out.println(info + " - " + ojAccount.getUsername());
    }

    public void handle () {
        try {
            if (Login.doLogin(client, ojProperty, ojAccount)) {
                System.out.println(info + " - Login success!");
                if (Submit.doSubmit(client, ojProperty, ojAccount, solution)) {
                    System.out.println(info + " - Submit success!");
                    Query.doQuery(client, ojProperty, ojAccount, solution);
                } else {
                    // 提交失败，代码长度不符合规定， Compile Error
                    System.out.println(info + " - Submit Fail! Complie_Error!");
                    solution.setResult(Result.Compile_Error);
                }
            } else {
                // 登录失败，可能是用户名密码错误，Judge Error
                System.out.println(info + " - Login Fail! Juege_Error!");
                solution.setResult(Result.Judge_Error);
            }
        } catch (JudgeException e) {
            solution.setResult(Result.Judge_Error);
        } catch (NetworkException e) {
            solution.setResult(Result.Network_Error);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ojAccount) {
                    DataProvider.Remote_OJAccounts.get(oj).put(ojAccount);
                }
                if (null != client)
                    client.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
