package team.dhuacm.RigidJudge.main;

import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.config.OJAccount;
import team.dhuacm.RigidJudge.config.OJProperty;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;

/**
 * Created by wujy on 15-1-8.
 */
public class RemoteResolver implements Runnable {

    private Solution solution;

    public RemoteResolver(Solution solution) {
        this.solution = solution;
    }

    @Override
    public void run() {
        OJ oj = solution.getProblem().getOJ();  // TODO
        OJProperty ojProperty = DataProvider.OJs.get(oj);
        OJAccount ojAccount = null;
        CloseableHttpClient client = null;
        String info = Thread.currentThread().getName();
        System.out.println(info + " - " + oj.toString() + " " + solution.getOjIndex() + " - solution id: " + solution.getId());
        try {
            ojAccount = DataProvider.Remote_OJAccounts.get(oj).take();
            System.out.println(info + " - " + ojAccount.getUsername());
            client = HttpClientUtil.get(DataProvider.Remote_RetryTimes, DataProvider.Remote_SocketTimeout, DataProvider.Remote_ConnectionTimeout);
            if (Login.doLogin(client, ojProperty, ojAccount)) {
                System.out.println(info + " - Login success!");
                if (Submit.doSubmit(client, oj, solution)) {
                    System.out.println(info + " - Submit success!");
                    Query.doQuery(client, oj, ojAccount, solution);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JudgeException e) {
            solution.setResult(Result.Judge_Error);
        } catch (NetworkException e) {
            solution.setResult(Result.Network_Error);
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

        System.out.println(info + " - result is " + solution.getResult());

        try {
            DataProvider.JudgedSolutionQueue.put(solution);
            System.out.println(info + " - send to finished queue success!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
