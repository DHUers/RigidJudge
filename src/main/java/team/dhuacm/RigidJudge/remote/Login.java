package team.dhuacm.RigidJudge.remote;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormControlType;
import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.Source;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.OJAccount;
import team.dhuacm.RigidJudge.config.OJProperty;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujy on 15-1-10.
 */
class Login {

    private static final Logger logger = LoggerFactory.getLogger(Login.class.getSimpleName());

    public static boolean doLogin(CloseableHttpClient client, OJProperty ojProperty, OJAccount ojAccount) throws JudgeException, NetworkException {

        //name value pair
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        // step1:get hidden params
        HttpGet get = new HttpGet(ojProperty.getLoginUrl());

        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode())
                return false;
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                String html = EntityUtils.toString(entity, ojProperty.getOjCharset());
                Source source = new Source(html);
                List<Element> formElements = source.getAllElements("form");
                //System.out.println("INFO: html form size: " + formElements.size());
                for (Element element : formElements) {
                    if (element.getAttributeValue("action").contains("login")) {
                        for (FormField field : element.getFormFields()) {
                            if (field.getFormControl().getFormControlType() == FormControlType.HIDDEN) {
                                NameValuePair nvp = new BasicNameValuePair(field.getName(), field.getValues().get(0));
                                nvps.add(nvp);
                            }
                        }
                        break;
                    }
                }
            }
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
            get.releaseConnection();
        }

        // step 2:use http post to login
        HttpPost post = new HttpPost(ojProperty.getLoginUrl());

        NameValuePair userNameValue = new BasicNameValuePair(ojProperty.getLoginUsername(), ojAccount.getUsername());
        NameValuePair passwordNameValue = new BasicNameValuePair(ojProperty.getLoginPassword(), ojAccount.getPassword());
        nvps.add(userNameValue);
        nvps.add(passwordNameValue);
        //System.out.println("INFO: name value pair size: " + nvps.size());
        post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        try {
            response = client.execute(post);
            //uva 301 200, poj 302 302, hdu 302 200, zoj 302 200
            //登录成功 301 或者 302
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println("INFO: content length: " + response.getEntity().getContentLength());
            if (HttpStatus.SC_MOVED_PERMANENTLY == statusCode || HttpStatus.SC_MOVED_TEMPORARILY == statusCode)
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
                logger.error(null, e);
            }
            post.releaseConnection();
        }
        return false;
    }
}
