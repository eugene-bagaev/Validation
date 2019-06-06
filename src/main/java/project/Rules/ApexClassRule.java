package project.Rules;

import com.sforce.soap.apex.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import project.MetadataLoginUtil;
import project.Util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ApexClassRule implements  Rule {

    public String nameClass;
    public List<String> methodsForSearch;
    public List<CheckExecuteMethodWraper> checkExecuteMethods;
    public String username;
    public ApexClassRule(String name, List<String> methodsForSearch, List<CheckExecuteMethodWraper> checkExecuteMethods) {
        this.nameClass = name;
        this.methodsForSearch = methodsForSearch;
        this.checkExecuteMethods = checkExecuteMethods;
    }

    public List<Results> checkCondition(String file, String userName) {
        this.username = userName;
        List<Results> results = new ArrayList<>();
        for (String method : methodsForSearch) {
            for (Integer i : Util.checkNesting(file, method)) {
                System.out.println(i);
            }
            if (Util.checkOnlyNesting(file, method, 1)) {
                results.add(new Results(nameClass, MessageFormat.format(Constants.APEXCLASS_FOUND_METHOD, nameClass, method), true));
            } else {
                results.add(new Results(nameClass, MessageFormat.format(Constants.APEXCLASS_NOT_FOUND_METHOD, nameClass, method), false));
            }
        }
        results.addAll(validateApexMethod());
        return results;
    }



    public List<Results> validateApexMethod( ) {
        System.out.println("validateApexMethod");
        List<Results> res = new ArrayList<>();
        for(CheckExecuteMethodWraper methodWraper : this.checkExecuteMethods){
            String apexCode = methodWraper.stringExecuted;
            System.out.println(apexCode);
            res.add(executeAnonymousWithReturnStringDebug(apexCode, methodWraper));
        }
        return res;
    }


    public Results executeAnonymousWithReturnStringDebug(String apexCode, CheckExecuteMethodWraper methodWraper) {
        String debug = "none(((";
        ConnectorConfig soapConfig = new ConnectorConfig();
        soapConfig.setAuthEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
        soapConfig.setServiceEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl().replace("/u/", "/s/"));
        soapConfig.setSessionId(MetadataLoginUtil.mapUserToSessionId.get(this.username));
        try {
            SoapConnection connection = new SoapConnection(soapConfig);
            LogInfo infoApex = new LogInfo();
            infoApex.setCategory(LogCategory.Apex_code);
            infoApex.setLevel(LogCategoryLevel.Debug);
            connection.setDebuggingHeader(new LogInfo[] { infoApex }, LogType.Debugonly);
            ExecuteAnonymousResult result  = connection.executeAnonymous(apexCode);
            debug = connection.getDebuggingInfo().getDebugLog();
            if (result.isCompiled()) {
                if (result.isSuccess()) {
                    System.out.println("Apex code excuted sucessfully");
                    System.out.println(">>" + debug + "<<");
                    return new Results(methodWraper.nameClass, MessageFormat.format(Constants.METHOD_SUCCESS, methodWraper.nameMethod, methodWraper.nameClass), true);
                } else {
                    return new Results(methodWraper.nameClass, MessageFormat.format(Constants.METHOD_EXECUTE_FAIL, methodWraper.nameMethod, methodWraper.nameClass), false);
//                    throw new RuntimeException("Apex code execution failed :" + result.getExceptionMessage());
                }
            } else {
                return new Results(methodWraper.nameClass, MessageFormat.format(Constants.METHOD_NOT_COMPILE, methodWraper.nameMethod, methodWraper.nameClass), false);
//                throw new RuntimeException("Apex code compilition failed :" + result.getCompileProblem());
            }
        } catch (ConnectionException ex) {
            System.out.println( this.username + ".ToolingHelper >>executeAnonymousWithReturnStringDebug>> Connection Exception: " + ex);
        }
        return new Results(methodWraper.nameClass, MessageFormat.format(Constants.METHOD_NOT_COMPILE, methodWraper.nameMethod, methodWraper.nameClass), false);
    }



}




