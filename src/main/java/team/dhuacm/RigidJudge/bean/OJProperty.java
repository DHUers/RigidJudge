package team.dhuacm.RigidJudge.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wujy on 15-1-7.
 */
public class OJProperty {

    private String ojName;
    private String ojUrl;
    private String ojCharset;
    private String[] ojLanguages;
    private String[] ojResults;

    private String loginUrl;
    private String loginUsername;
    private String loginPassword;

    private String problemUrl;
    private String problemKey;
    private String problemTitlePrefix;
    private String problemTitleSuffix;
    private String problemContentPrefix;
    private String problemContentSuffix;

    private String submitUrl;
    private String submitProblem;
    private String submitCode;
    private String submitLanguage;

    private String queryUrl;
    private String queryUsername;
    private int queryTableColumns;
    private int queryResultColumn;
    private int queryMemoryColumn;
    private String queryMemoryUnit;
    private int queryRuntimeColumn;
    private String queryRuntimeUnit;

    public OJProperty() {
    }

    public OJProperty(File file) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.ojName = p.getProperty("oj.name");
        this.ojCharset = p.getProperty("oj.charset");
        this.ojUrl = p.getProperty("oj.url");
        this.ojLanguages = p.getProperty("oj.languages").trim().split(",");
        this.ojResults = p.getProperty("oj.results").trim().split(",");
        this.loginUrl = p.getProperty("login.url");
        this.loginUsername = p.getProperty("login.username");
        this.loginPassword = p.getProperty("login.password");
        this.submitUrl = p.getProperty("submit.url");
        this.submitCode = p.getProperty("submit.code");
        this.submitLanguage = p.getProperty("submit.language");
        this.submitProblem = p.getProperty("submit.problem");
        this.queryUrl = p.getProperty("query.url");
        this.queryUsername = p.getProperty("query.username");
        this.queryTableColumns = Integer.parseInt(p.getProperty("query.table.columns"));
        this.queryResultColumn = Integer.parseInt(p.getProperty("query.result.column"));
        this.queryMemoryColumn = Integer.parseInt(p.getProperty("query.memory.column"));
        this.queryMemoryUnit = p.getProperty("query.memory.unit");
        this.queryRuntimeColumn = Integer.parseInt(p.getProperty("query.runtime.column"));
        this.queryRuntimeUnit = p.getProperty("query.runtime.unit");
    }

    public String getOjName() {
        return ojName;
    }

    public void setOjName(String ojName) {
        this.ojName = ojName;
    }

    public String getOjUrl() {
        return ojUrl;
    }

    public void setOjUrl(String ojUrl) {
        this.ojUrl = ojUrl;
    }

    public String getOjCharset() {
        return ojCharset;
    }

    public void setOjCharset(String ojCharset) {
        this.ojCharset = ojCharset;
    }

    public String[] getOjLanguages() {
        return ojLanguages;
    }

    public void setOjLanguages(String[] ojLanguages) {
        this.ojLanguages = ojLanguages;
    }

    public String[] getOjResults() {
        return ojResults;
    }

    public void setOjResults(String[] ojResults) {
        this.ojResults = ojResults;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getProblemUrl() {
        return problemUrl;
    }

    public void setProblemUrl(String problemUrl) {
        this.problemUrl = problemUrl;
    }

    public String getProblemKey() {
        return problemKey;
    }

    public void setProblemKey(String problemKey) {
        this.problemKey = problemKey;
    }

    public String getProblemTitlePrefix() {
        return problemTitlePrefix;
    }

    public void setProblemTitlePrefix(String problemTitlePrefix) {
        this.problemTitlePrefix = problemTitlePrefix;
    }

    public String getProblemTitleSuffix() {
        return problemTitleSuffix;
    }

    public void setProblemTitleSuffix(String problemTitleSuffix) {
        this.problemTitleSuffix = problemTitleSuffix;
    }

    public String getProblemContentPrefix() {
        return problemContentPrefix;
    }

    public void setProblemContentPrefix(String problemContentPrefix) {
        this.problemContentPrefix = problemContentPrefix;
    }

    public String getProblemContentSuffix() {
        return problemContentSuffix;
    }

    public void setProblemContentSuffix(String problemContentSuffix) {
        this.problemContentSuffix = problemContentSuffix;
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
    }

    public String getSubmitProblem() {
        return submitProblem;
    }

    public void setSubmitProblem(String submitProblem) {
        this.submitProblem = submitProblem;
    }

    public String getSubmitCode() {
        return submitCode;
    }

    public void setSubmitCode(String submitCode) {
        this.submitCode = submitCode;
    }

    public String getSubmitLanguage() {
        return submitLanguage;
    }

    public void setSubmitLanguage(String submitLanguage) {
        this.submitLanguage = submitLanguage;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getQueryUsername() {
        return queryUsername;
    }

    public void setQueryUsername(String queryUsername) {
        this.queryUsername = queryUsername;
    }

    public int getQueryTableColumns() {
        return queryTableColumns;
    }

    public void setQueryTableColumns(int queryTableColumns) {
        this.queryTableColumns = queryTableColumns;
    }

    public int getQueryResultColumn() {
        return queryResultColumn;
    }

    public void setQueryResultColumn(int queryResultColumn) {
        this.queryResultColumn = queryResultColumn;
    }

    public int getQueryMemoryColumn() {
        return queryMemoryColumn;
    }

    public void setQueryMemoryColumn(int queryMemoryColumn) {
        this.queryMemoryColumn = queryMemoryColumn;
    }

    public String getQueryMemoryUnit() {
        return queryMemoryUnit;
    }

    public void setQueryMemoryUnit(String queryMemoryUnit) {
        this.queryMemoryUnit = queryMemoryUnit;
    }

    public int getQueryRuntimeColumn() {
        return queryRuntimeColumn;
    }

    public void setQueryRuntimeColumn(int queryRuntimeColumn) {
        this.queryRuntimeColumn = queryRuntimeColumn;
    }

    public String getQueryRuntimeUnit() {
        return queryRuntimeUnit;
    }

    public void setQueryRuntimeUnit(String queryRuntimeUnit) {
        this.queryRuntimeUnit = queryRuntimeUnit;
    }

}
