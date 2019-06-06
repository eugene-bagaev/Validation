package project;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import javax.xml.parsers.*;

import com.google.api.client.util.DateTime;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.metadata.CodeCoverageWarning;
import com.sforce.soap.metadata.DeployDetails;
import com.sforce.soap.metadata.DeployMessage;
import com.sforce.soap.metadata.RunTestFailure;
import com.sforce.soap.metadata.RunTestsResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import com.sforce.soap.metadata.*;
import project.Emails.MailService;
import project.Processors.RequestProcessor;
import project.Rules.Results;
import project.Rules.Constants;

public class DeployRetrieveHelper {

    public String username;
    public String pass;
    private Map<String, UserInfoWrapper> userResults;

    public MetadataConnection metadataConnection;

    private String ZIP_FILE = "";
    private String manifest_file = TaskMapping.PathToXMLFile;

    private static final double API_VERSION = 45.0;
    // one second in milliseconds
    private static final long ONE_SECOND = 1000;
    // maximum number of attempts to deploy the zip file
    private static final int MAX_NUM_POLL_REQUESTS = 50;
    private SalesforceHepler salesforceHepler;

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public DeployRetrieveHelper(String username, String pass, Map<String, UserInfoWrapper> userResults, SalesforceHepler salesforceHepler) {
        this.username = username;
        this.pass = pass;
        this.userResults = userResults;
        this.salesforceHepler = salesforceHepler;
        this.ZIP_FILE = "src/main/resources/" +  username + ".zip";
        try {
            loginInOrg();
            retrieveZip();
            RequestProcessor.getMapValue(this.username, this.userResults).setLoginHistoryList(getLoginHistory());
        } catch (ConnectionException ex) {
            System.out.println(username + ". >> Connection Exception: " + ex.toString());
            MailService.getInstance().setSubject("Connection Exception")
                    .setBody(MessageFormat.format(Constants.CONNECTION_EX_MESSAGE, username, ex.toString()) + " " +getClass())
                    .sendMail();
            UserInfoWrapper info = RequestProcessor.getMapValue(this.username, this.userResults);
            info.addError("Invalid username, password, security token; or user locked out");
            salesforceHepler.zip_file_for_read = "";
        }
    }

    private void loginInOrg() throws ConnectionException {
        metadataConnection = MetadataLoginUtil.login(
                this.username,
                this.pass
        );
    }

    public void deployZip(boolean checkonly) throws Exception {
        byte zipBytes[] = readZipFile();

        DeployOptions deployOptions = new DeployOptions();

        deployOptions.setPerformRetrieve(false);
        deployOptions.setRollbackOnError(true);
        deployOptions.setCheckOnly(checkonly);

        AsyncResult asyncResult = metadataConnection.deploy(zipBytes, deployOptions);
        DeployResult result     = waitForDeployCompletion(asyncResult.getId());

        if (!result.isSuccess()) {
            printErrors(result, "Final list of failures:\n");
            throw new Exception("The files were not successfully deployed");
        }

        System.out.println("Number of components deployed "+result.getNumberComponentsDeployed());
        System.out.println("Number of components total "+result.getNumberComponentsTotal());
        System.out.println("Created By "+result.getCreatedByName());
        System.out.println("The file " + ZIP_FILE + " was successfully deployed\n");
    }

    private byte[] readZipFile() throws Exception {
        byte[] result = null;
        // We assume here that you have a deploy.zip file.
        // See the retrieve sample for how to retrieve a zip file.
        File zipFile = new File(ZIP_FILE);
        if (!zipFile.exists() || !zipFile.isFile()) {
            throw new Exception("Cannot find the zip file for deploy() on path:"
                    + zipFile.getAbsolutePath());
        }

        FileInputStream fileInputStream = new FileInputStream(zipFile);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while (-1 != (bytesRead = fileInputStream.read(buffer))) {
                bos.write(buffer, 0, bytesRead);
            }

            result = bos.toByteArray();
        } finally {
            fileInputStream.close();
        }
        return result;
    }

    private void printErrors(DeployResult result, String messageHeader) {
        DeployDetails details = result.getDetails();
        StringBuilder stringBuilder = new StringBuilder();
        if (details != null) {
            DeployMessage[] componentFailures = details.getComponentFailures();
            for (DeployMessage failure : componentFailures) {
                String loc = "(" + failure.getLineNumber() + ", " + failure.getColumnNumber();
                if (loc.length() == 0 && !failure.getFileName().equals(failure.getFullName()))
                {
                    loc = "(" + failure.getFullName() + ")";
                }
                stringBuilder.append(failure.getFileName() + loc + ":"
                        + failure.getProblem()).append('\n');
            }
            RunTestsResult rtr = details.getRunTestResult();
            if (rtr.getFailures() != null) {
                for (RunTestFailure failure : rtr.getFailures()) {
                    String n = (failure.getNamespace() == null ? "" :
                            (failure.getNamespace() + ".")) + failure.getName();
                    stringBuilder.append("Test failure, method: " + n + "." +
                            failure.getMethodName() + " -- " + failure.getMessage() +
                            " stack " + failure.getStackTrace() + "\n\n");
                }
            }
            if (rtr.getCodeCoverageWarnings() != null) {
                for (CodeCoverageWarning ccw : rtr.getCodeCoverageWarnings()) {
                    stringBuilder.append("Code coverage issue");
                    if (ccw.getName() != null) {
                        String n = (ccw.getNamespace() == null ? "" :
                                (ccw.getNamespace() + ".")) + ccw.getName();
                        stringBuilder.append(", class: " + n);
                    }
                    stringBuilder.append(" -- " + ccw.getMessage() + "\n");
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.insert(0, messageHeader);
            System.out.println(stringBuilder.toString());
        }
    }


    public void retrieveZip() {
        try {

            RetrieveRequest retrieveRequest = new RetrieveRequest();
            // The version in package.xml overrides the version in RetrieveRequest
            retrieveRequest.setApiVersion(API_VERSION);
            setUnpackaged(retrieveRequest);

            AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
            RetrieveResult result = waitForRetrieveCompletion(asyncResult);

            if (result.getStatus() == RetrieveStatus.Failed) {
                throw new Exception(result.getErrorStatusCode() + " msg: " +
                        result.getErrorMessage());
            } else if (result.getStatus() == RetrieveStatus.Succeeded) {
                // Print out any warning messages
                StringBuilder stringBuilder = new StringBuilder();
                if (result.getMessages() != null) {
                    for (RetrieveMessage rm : result.getMessages()) {
                        stringBuilder.append(rm.getFileName() + " - " + rm.getProblem() + "\n");
                    }
                }
                if (stringBuilder.length() > 0) {
                    System.out.println(this.username + ". >> Retrieve warnings:\n" + stringBuilder);
                }

                System.out.println(this.username + ". >> Writing results to zip file");
                System.out.println(ZIP_FILE + "  !!!!!!!!!!!!!!!!!!!!");
                File resultsFile = new File(ZIP_FILE);
                FileOutputStream os = new FileOutputStream(resultsFile);

                try {
                    os.write(result.getZipFile());
                } finally {
                    os.close();
                }

                salesforceHepler.zip_file_for_read = ZIP_FILE;
            }

        } catch (Exception ex) {
            MailService.getInstance().setBody(MessageFormat.format(Constants.RETRIEVE_EX_MESSAGE, username, ex.toString()))
                    .setSubject("Retrieve zip error").sendMail();
            System.out.println("Ex: " + ex.getMessage());
        }

    }

    private DeployResult waitForDeployCompletion(String asyncResultId) throws Exception {
        int poll = 0;
        long waitTimeMilliSecs = ONE_SECOND;
        DeployResult deployResult;
        boolean fetchDetails;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration

            waitTimeMilliSecs *= 2;
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception(
                        "Request timed out. If this is a large set of metadata components, " +
                                "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            // Fetch in-progress details once for every 3 polls
            fetchDetails = (poll % 3 == 0);

            deployResult = metadataConnection.checkDeployStatus(asyncResultId, fetchDetails);
            System.out.println("Status is: " + deployResult.getStatus());
            if (!deployResult.isDone() && fetchDetails) {
                printErrors(deployResult, "Failures for deployment in progress:\n");
            }
        }
        while (!deployResult.isDone());

        if (!deployResult.isSuccess() && deployResult.getErrorStatusCode() != null) {
            throw new Exception(deployResult.getErrorStatusCode() + " msg: " +
                    deployResult.getErrorMessage());
        }

        if (!fetchDetails) {
            // Get the final result with details if we didn't do it in the last attempt.
            deployResult = metadataConnection.checkDeployStatus(asyncResultId, true);
        }

        return deployResult;
    }

    private RetrieveResult waitForRetrieveCompletion(AsyncResult asyncResult) throws Exception {
        // Wait for the retrieve to complete
        int poll = 0;
        long waitTimeMilliSecs = ONE_SECOND;
        String asyncResultId = asyncResult.getId();
        RetrieveResult result = null;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // Double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception("Request timed out.  If this is a large set " +
                        "of metadata components, check that the time allowed " +
                        "by MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            result = metadataConnection.checkRetrieveStatus(
                    asyncResultId, true);
            System.out.println(this.username+ ". >> Retrieve Status: " + result.getStatus());
        } while (!result.isDone());

        return result;
    }

    private void setUnpackaged(RetrieveRequest request) throws Exception {
        // Edit the path, if necessary, if your package.xml file is located elsewhere
        File unpackedManifest = new File(manifest_file);
        System.out.println(Thread.currentThread().getName() + ". >> Manifest file: " + unpackedManifest.getAbsolutePath());

        if (!unpackedManifest.exists() || !unpackedManifest.isFile()) {
            throw new Exception("Should provide a valid retrieve manifest " +
                    "for unpackaged content. Looking for " +
                    unpackedManifest.getAbsolutePath());
        }
        // Note that we use the fully qualified class name because
        // of a collision with the java.lang.Package class
        com.sforce.soap.metadata.Package p = parsePackageManifest(unpackedManifest);
        request.setUnpackaged(p);
    }

    private com.sforce.soap.metadata.Package parsePackageManifest(File file)
            throws ParserConfigurationException, IOException, SAXException {

        com.sforce.soap.metadata.Package packageManifest    = null;
        List<PackageTypeMembers> listPackageTypes           = new ArrayList<PackageTypeMembers>();
        DocumentBuilder db                                  = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream                             = new FileInputStream(file);
        Element d                                           = db.parse(inputStream).getDocumentElement();

        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {

            if (c instanceof Element) {

                Element ce          = (Element) c;
                NodeList nodeList   = ce.getElementsByTagName("name");

                if (nodeList.getLength() == 0) {
                    continue;
                }

                String name             = nodeList.item(0).getTextContent();
                NodeList m              = ce.getElementsByTagName("members");
                List<String> members    = new ArrayList<String>();

                for (int i = 0; i < m.getLength(); i++) {
                    Node mm = m.item(i);
                    members.add(mm.getTextContent());
                }
                PackageTypeMembers packageTypes = new PackageTypeMembers();

                packageTypes.setName(name);
                packageTypes.setMembers(members.toArray(new String[members.size()]));

                listPackageTypes.add(packageTypes);
            }
        }
        packageManifest = new com.sforce.soap.metadata.Package();

        PackageTypeMembers[] packageTypesArray = new PackageTypeMembers[listPackageTypes.size()];

        packageManifest.setTypes(listPackageTypes.toArray(packageTypesArray));
        packageManifest.setVersion(API_VERSION + "");

        return packageManifest;
    }


    public void deleteFileZip()  {
        try {
            File file = new File(ZIP_FILE);
            if (file.exists()) {
                file.delete();
                System.out.println("DELETE FILE !!!!!!!!!" + ZIP_FILE + " ***********");
            } else {
                System.out.println("DELETE NOT EXIST !!!!!!!!!" + ZIP_FILE + " ***********");
            }

        }
        catch(Exception e){
            System.out.println("ERROR Exception DELETE FILE " + ZIP_FILE);
        }
    }

    private List<String> getLoginHistory() throws ConnectionException {
        List<String> loginTimeList = new ArrayList<>();
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(this.username);
        config.setPassword(this.pass);
        config.setServiceEndpoint(MetadataLoginUtil.LOGIN_URL);
        config.setAuthEndpoint(MetadataLoginUtil.LOGIN_URL);

        PartnerConnection partnerConnection = new PartnerConnection(config);
        QueryResult queryResult = partnerConnection.query(
                "Select LoginTime, Browser FROM LoginHistory WHERE SourceIp <> '80.249.88.240' ORDER BY LoginTime DESC"
        );
        SObject[] records = queryResult.getRecords();
        for (SObject sObject : records) {
            String fieldValue = String.valueOf(sObject.getSObjectField("Browser"));
            if (fieldValue.equalsIgnoreCase("Java (Salesforce.com)")) continue;
            loginTimeList.add(
                    getFormattedTime(String.valueOf(sObject.getSObjectField("LoginTime")))
            );
        }
        return loginTimeList;
    }

    private String getFormattedTime(String loginTime) {
        String[] dateAndTime = loginTime.split("T");
        String[] dateParts = dateAndTime[0].split("-");
        int year = Integer.valueOf(dateParts[0]);
        int month = Integer.valueOf(dateParts[1]);
        int day = Integer.valueOf(dateParts[2]);
        String[] timeParts = dateAndTime[1].split(":");
        int hour = Integer.valueOf(timeParts[0]);
        int minute = Integer.valueOf(timeParts[1]);
        int second = Integer.valueOf(timeParts[2].substring(0, timeParts[2].indexOf(".")));
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.add(Calendar.HOUR, 3);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return String.valueOf(calendar.getTime());
    }
}
