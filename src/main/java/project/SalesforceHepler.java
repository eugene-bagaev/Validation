package project;

import project.Rules.*;
import project.Processors.RequestProcessor;
import project.Storages.FileStorage;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class SalesforceHepler {

    public static String templateNotFoundFile =  "Не найден файл: {0}";

    private String tempUsername;
    private String tempPassword;
    private Map<String, UserInfoWrapper> userResults;

    public String zip_file_for_read = "";

    public TaskMapping taskMapping;

    public SalesforceHepler(String username, String password, Map<String, UserInfoWrapper> userResults, TaskMapping taskMapping) {
        this.tempUsername = username;
        this.tempPassword = password;
        this.userResults = userResults;
        this.taskMapping = taskMapping;
    }

    public void processUser() {
        DeployRetrieveHelper instance = new DeployRetrieveHelper(tempUsername, tempPassword, userResults, this);
        if (!zip_file_for_read.equalsIgnoreCase("")) {
            checkZipFile();
        }
    }


    private void checkZipFile() {

        Map<String, List<Results>> TASK_RESULT = new LinkedHashMap<>();
        try {
            System.out.println(tempUsername + "     ---" + zip_file_for_read);
            ZipFile file = new ZipFile(zip_file_for_read);

            for (String taskName : taskMapping.nameTask_mapResults.keySet()) {

                List<Results> results = new ArrayList<>();
                for (String nameMetadata : taskMapping.nameTask_mapResults.get(taskName).keySet()) {
                    System.out.println(nameMetadata);
                    Enumeration<? extends ZipEntry> e = file.entries();
                    boolean fileFound = false;
                    while (e.hasMoreElements()) {
                        ZipEntry entry = e.nextElement();

                        if (entry.getName().contains("/" + nameMetadata + ".") && !entry.getName().contains(".xml")) {
                            fileFound = true;
                            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
                            String allFile = "";
                            String theFile = "";
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                allFile = allFile + line;
                                theFile += "<br/>" + line.replaceAll(" ", "&nbsp;")
                                        .replaceAll("<", "&lt").replaceAll(">", "&gt;");
                            }
                            if (!addFileToContainer(tempUsername, nameMetadata, theFile)) {
                                RequestProcessor.files.add(new FileStorage(nameMetadata, tempUsername, theFile));
                            }
                            results.addAll(taskMapping.nameTask_mapResults.get(taskName).get(nameMetadata).checkCondition(allFile, this.tempUsername));
                            break;
                        }
                    }
                    // not found file
                    if (!fileFound) {
                        results.add(new Results(nameMetadata, MessageFormat.format(templateNotFoundFile, nameMetadata), false));
                    }
                }
                TASK_RESULT.put(taskName, results);
            }

        } catch (IOException ex) {
            System.out.println("ioEx.SFHelper.readZip: " + ex.getMessage());
        }
        RequestProcessor.getMapValue(tempUsername, userResults).setResults(TASK_RESULT);

    }

    private Boolean addFileToContainer(String owner, String fileName, String fileContent) {
        for (int i = 0; i < RequestProcessor.files.size(); i ++) {
            FileStorage file = RequestProcessor.files.get(i);
            if (file.fileName.equalsIgnoreCase(fileName) && file.fileOwner.equalsIgnoreCase(owner)) {
                if (!fileContent.equalsIgnoreCase(file.content)) {
                    System.out.println(file.content);
                    RequestProcessor.files.get(i).content = fileContent;
                }
                return true;
            }
        }
        return false;
    }
}
