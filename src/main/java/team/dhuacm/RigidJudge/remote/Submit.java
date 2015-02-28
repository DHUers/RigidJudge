package team.dhuacm.RigidJudge.remote;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.OJAccount;
import team.dhuacm.RigidJudge.config.OJProperty;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujy on 15-1-10.
 */
class Submit {

    private static final Logger logger = LoggerFactory.getLogger(Submit.class.getSimpleName());

    public static boolean doSubmit(CloseableHttpClient httpClient, OJProperty ojProperty, OJAccount ojAccount, Solution solution) throws JudgeException, NetworkException {

        HttpPost post = new HttpPost(ojProperty.getSubmitUrl());

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        NameValuePair problemNvp = new BasicNameValuePair(ojProperty.getSubmitProblem(), ((RemoteProblem) solution.getProblem()).getOjIndex() + "");
        NameValuePair languageNVP = new BasicNameValuePair(ojProperty.getSubmitLanguage(), ojProperty.getOjLanguages()[(solution.getLanguage().ordinal())]);
        NameValuePair codeNVP = new BasicNameValuePair(ojProperty.getSubmitCode(), solution.getCode());
        nvps.add(problemNvp);
        nvps.add(languageNVP);
        nvps.add(codeNVP);
        if (ojProperty.getOjName().equals("sgu")) {  // TODO: move additional form values to OJProperty
            nvps.add(new BasicNameValuePair("id", ojAccount.getUsername()));
            nvps.add(new BasicNameValuePair("pass", ojAccount.getPassword()));
        }
        if (ojProperty.getOjName().equals("ural")) {  // TODO: move additional form values to OJProperty
            nvps.add(new BasicNameValuePair("JudgeID", ojAccount.getPassword()));
            nvps.add(new BasicNameValuePair("Action", "submit"));
            nvps.add(new BasicNameValuePair("SpaceID", "1"));
        }
        if (ojProperty.getOjName().equals("codeforces")) {  // TODO: move additional form values to OJProperty
            nvps.add(new BasicNameValuePair("action", "submitSolutionFormSubmitted"));
        }
        if (ojProperty.getOjName().equals("aizu")) {  // TODO: move additional form values to OJProperty
            nvps.add(new BasicNameValuePair("userID", ojAccount.getUsername()));
            nvps.add(new BasicNameValuePair("password", ojAccount.getPassword()));
        }
        post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println("INFO: status code is: " + statusCode);
            if (HttpStatus.SC_MOVED_PERMANENTLY == statusCode || HttpStatus.SC_MOVED_TEMPORARILY == statusCode)
                return true;
            //code is too long or too short, so compile error should be return.  // TODO: move specific requirements to OJProperty
            return !((HttpStatus.SC_OK == statusCode) && !ojProperty.getOjName().equals("zoj") && !ojProperty.getOjName().equals("sgu") && !ojProperty.getOjName().equals("spoj") && !ojProperty.getOjName().equals("ural") && !ojProperty.getOjName().equals("aizu"));
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
            post.releaseConnection();
        }
    }
}