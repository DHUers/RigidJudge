package team.dhuacm.RigidJudge.remote;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.dhuacm.RigidJudge.config.*;
import team.dhuacm.RigidJudge.exception.JudgeException;
import team.dhuacm.RigidJudge.exception.NetworkException;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by wujy on 15-1-10.
 */
class Query {

    private static final Logger logger = LoggerFactory.getLogger(Query.class.getSimpleName());

    public static void doQuery(CloseableHttpClient client, OJProperty ojProperty, OJAccount ojAccount, Solution solution) throws JudgeException, NetworkException {

        for (int i = 0; i < DataProvider.Remote_QueryInterval.size(); i++) {
            long sleepTime = DataProvider.Remote_QueryInterval.get(i) * 1000;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Tried {} times!", i);

            getResult(client, ojProperty, ojAccount, solution);

            if (Result.Queue == solution.getResult()) {
                if (i + 1 == DataProvider.Remote_QueryInterval.size()) {
                    solution.setResult(Result.Network_Error);
                }
            } else {
                break;
            }
        }
    }

    //get result and other info form query page
    private static void getResult(CloseableHttpClient client, OJProperty ojProperty, OJAccount ojAccount, Solution solution) throws JudgeException, NetworkException {

        URI uri = URI.create(ojProperty.getQueryUrl().replace("{username}", ojAccount.getUsername()));
        String queryUsername = ojProperty.getQueryUsername();
        if (null != queryUsername && !"".equals(queryUsername)) {
            try {
                uri = new URIBuilder(uri).addParameter(queryUsername, ojAccount.getUsername()).build();
            } catch (URISyntaxException e) {
                logger.error(null, e);
            }
        }
        //System.out.println(uri);

        HttpGet get = new HttpGet(uri);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
                return;
            }
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                return;
            }
            String html = EntityUtils.toString(entity, ojProperty.getOjCharset());
            Source source = new Source(html);
            if (ojProperty.getOjName().equals("uvalive")) {  // TODO: move specific requirements to OJProperty
                source = new Source(source.getAllElementsByClass("maincontent").toString());
            }
            source.setLogger(null);
            List<Element> tableElements = source.getAllElements("table");
            if (ojProperty.getOjName().equals("sgu")) {  // TODO: move specific requirements to OJProperty
                tableElements = source.getAllElements("cellspacing", "3", true);
            }
            for (Element tableElement : tableElements) {
                List<Element> trElements = tableElement.getAllElements("tr");
                if (trElements.size() >= 2) {
                    List<Element> tdElements = trElements.get(1).getAllElements("td");

                    if (ojProperty.getOjName().equals("ural")) {  // TODO: move specific requirements to OJProperty
                        if (trElements.size() >= 3) {
                            tdElements = trElements.get(2).getAllElements("td");
                        }
                    }
                    if (ojProperty.getOjName().equals("aizu")) {  // TODO: move specific requirements to OJProperty
                        boolean flag = false;
                        for (int i = 1; i < trElements.size(); i++) {
                            tdElements = trElements.get(i).getAllElements("td");
                            if (tdElements.size() == ojProperty.getQueryTableColumns()) {
                                if (tdElements.get(1).getTextExtractor().toString().equals(ojAccount.getUsername())) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (!flag) continue;
                    }

                    if (tdElements.size() == ojProperty.getQueryTableColumns()) {
                        Element tdElement = tdElements.get(ojProperty.getQueryResultColumn() - 1);
                        String resultStr = tdElement.getTextExtractor().toString();
                        //System.out.println(resultStr);
                        String[] results = ojProperty.getOjResults();
                        Result result = null;
                        for (int i = 0; i < results.length; i++) {
                            if (resultStr.toLowerCase().contains(results[i].toLowerCase())) {
                                result = Result.values()[i];
                                break;
                            }
                        }
                        //no result match
                        if (null == result)
                            return;
                        solution.setResult(result);
                        //if (result == Result.Accepted) {
                        if (ojProperty.getQueryMemoryColumn() != 0) {
                            Element memoryTdElement = tdElements.get(ojProperty.getQueryMemoryColumn() - 1);
                            String memoryStr = memoryTdElement.getTextExtractor().toString();
                            memoryStr = memoryStr.replace(ojProperty.getQueryMemoryUnit(), "");

                            if (ojProperty.getOjName().equals("spoj") && memoryStr.contains("k")) {  // TODO: move specific requirements to OJProperty
                                memoryStr = memoryStr.replace("k", "");
                                memoryStr = String.valueOf(Double.parseDouble(memoryStr) / 1024);
                            }

                            //System.out.println(memoryStr);
                            int memory;
                            if (ojProperty.getQueryMemoryUnit().contains("M")) {
                                memory = (int) (Double.parseDouble(memoryStr.trim()) * 1024);
                            } else {
                                memory = Integer.parseInt(memoryStr.trim());
                            }
                            solution.setMemory(memory);
                        }
                        if (ojProperty.getQueryRuntimeColumn() != 0) {
                            Element runtimeTdElement = tdElements.get(ojProperty.getQueryRuntimeColumn() - 1);
                            String runtimeStr = runtimeTdElement.getTextExtractor().toString();
                            runtimeStr = runtimeStr.replace(ojProperty.getQueryRuntimeUnit(), "");
                            //System.out.println(runtimeStr);
                            int runtime;
                            if (ojProperty.getQueryRuntimeUnit().equalsIgnoreCase("S")) {
                                runtime = (int) (Double.parseDouble(runtimeStr.trim()) * 1000);
                            } else {
                                runtime = Integer.parseInt(runtimeStr.trim());
                            }
                            solution.setTime(runtime);
                        }
                        //}
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
                logger.error(null, e);
            }
            get.releaseConnection();
        }
    }
}
