package team.dhuacm.RigidJudge.util;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import team.dhuacm.RigidJudge.config.OJProperty;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujy on 15-1-10.
 */
public class Submit {

    public static boolean doSubmit(CloseableHttpClient httpClient, OJProperty ojProperty, Solution solution) throws JudgeException, NetworkException {

        HttpPost post = new HttpPost(ojProperty.getSubmitUrl());

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        NameValuePair problemNvp = new BasicNameValuePair(ojProperty.getSubmitProblem(), solution.getOjIndex() + "");
        NameValuePair languageNVP = new BasicNameValuePair(ojProperty.getSubmitLanguage(), ojProperty.getOjLanguages()[(solution.getLanguage().ordinal())]);
        NameValuePair codeNVP = new BasicNameValuePair(ojProperty.getSubmitCode(), solution.getCode());
        nvps.add(problemNvp);
        nvps.add(languageNVP);
        nvps.add(codeNVP);
        post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println("INFO: status code is: " + statusCode);
            if (HttpStatus.SC_MOVED_PERMANENTLY == statusCode || HttpStatus.SC_MOVED_TEMPORARILY == statusCode)
                return true;
            //code is too long or too short, so compile error should be return.
            if(HttpStatus.SC_OK == statusCode && !ojProperty.getSubmitUrl().equals("http://acm.zju.edu.cn/onlinejudge/submit.do"))
                return false;
            else
                return true;
        } catch (ClientProtocolException e) {
            throw new JudgeException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new NetworkException(e.getMessage(), e.getCause());
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            post.releaseConnection();
        }
        //return false;
    }
}
