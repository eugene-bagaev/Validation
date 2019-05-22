package project.Rules;

import com.sforce.soap.apex.CodeCoverageResult;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import project.MetadataLoginUtil;
import project.Rules.Constants;
import project.TaskMapping;
import project.Util;

import java.text.MessageFormat;
import java.util.*;

public class TestRule implements Rule {

    // tests: Test Class => Class тестируемый
    public String TestClass;
    public String CoveregeClass;

    public TestRule(String TestClass, String CoveregeClass) {
        this.TestClass = TestClass;
        this.CoveregeClass = CoveregeClass;
    }

    public List<Results> checkCondition(String file, String userName) {
        List<Results> results = new ArrayList<>();
        if (    Util.checkNesting(file, "System.assert") > 1 ||
                Util.checkNesting(file, "System.assertEquals") > 1 ||
                Util.checkNesting(file, "System.assertNotEquals") > 1 ) {
            results.add(new Results("Test", MessageFormat.format(Constants.TEST_SUCCESS_ASSERT,  this.TestClass), true));
        } else {
            results.add(new Results("Test",  MessageFormat.format(Constants.TEST_FAIL_ASSERT,  this.TestClass), false));
        }
        results.addAll(validateUserResultUsingTest(userName,  this.TestClass, this.CoveregeClass));
        return results;
    }


    public List<Results> validateUserResultUsingTest(String userName,String testCl, String CoveregeCl) {

        List<Results> res = new ArrayList<>();

        String debugThreadName = userName;
        System.out.println(debugThreadName + ": >> Test Run");
        SoapConnection connection;
        ConnectorConfig soapConfig = new ConnectorConfig();

        try {
            System.out.println("*****************" + debugThreadName + " >> Un: " + userName);
            System.out.println("*****************" + debugThreadName + " >> SI " + MetadataLoginUtil.mapUserToSessionId.get(userName));

            soapConfig.setAuthEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(userName).getServerUrl());
            soapConfig.setServiceEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(userName).getServerUrl().replace("/u/", "/s/"));
            soapConfig.setSessionId(MetadataLoginUtil.mapUserToSessionId.get(userName));

            connection = new SoapConnection(soapConfig);

            com.sforce.soap.apex.RunTestsRequest request = new com.sforce.soap.apex.RunTestsRequest();
            String arr[] = new String[1];
            arr[0] = testCl;
            request.setClasses(arr);
            com.sforce.soap.apex.RunTestsResult result = connection.runTests(request);

            if (result.getCodeCoverage() != null) {

                    Boolean testRun = false;
                    for (CodeCoverageResult ccr : result.getCodeCoverage()) {

                        if (CoveregeCl.equals(ccr.getName())) {

//                        if (TaskMapping.TEST_CLASSES.get(nameTestClass).equals(ccr.getName())) {
                            testRun = true;
                            Integer percentCoverage = checkCoverage((ccr.getNumLocations() - ccr.getNumLocationsNotCovered()), ccr.getNumLocations());
                            if(percentCoverage < 75){
                                res.add(new Results("Test", MessageFormat.format(Constants.TEST_SUCCESS_BUT_NOT_ENOUGH_COVERAGE, testCl, percentCoverage), false));
                            } else {
                                res.add(new Results("Test", MessageFormat.format(Constants.TEST_SUCCESS,  testCl, percentCoverage), true));
                            }
                            System.out.println(debugThreadName + ": >> Test Result For Class: " + this.CoveregeClass);
                            System.out.println(debugThreadName + ": >> Coverage: " + (ccr.getNumLocations() - ccr.getNumLocationsNotCovered()) + " of " + ccr.getNumLocations()
                            );
                        }
                    }

                    if(!testRun) res.add(new Results("Test", MessageFormat.format(Constants.TEST_NOT_FOUND,  testCl), false));

//                }
            } else{
                System.out.println(" else: ");
            }

        } catch (ConnectionException ex) {
            System.out.println( debugThreadName + " >> ValidateByTestEx: " + ex);
        } catch (Exception commEx) {
            System.out.println("Ex: " + commEx);
        }
        System.out.println(" end: ");
        return res;
    }

    private Integer checkCoverage(Integer summNumLocationsCovered, Integer summNumLocations) {
        if (summNumLocationsCovered == 0) return 0;
        Integer total = summNumLocationsCovered * 100 / summNumLocations;
        return total;
    }




}
