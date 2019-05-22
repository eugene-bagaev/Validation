package project.Processors;

import project.GoogleHelper;
import project.Rules.Results;
import project.SalesforceHepler;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import project.TaskMapping;
import project.Storages.FileStorage;

public class RequestProcessor {
    String users;
    public Map<String, List<Results>> userResults;
    public List<CredentialsStorage> userLogins;
    public static List<FileStorage> files = new ArrayList<>();

    public RequestProcessor() {
        userLogins = new ArrayList<>();
        GoogleHelper.callDocument(userLogins);
    }

    public RequestProcessor(String userNames) {
        users = userNames;
        if (userNames.equalsIgnoreCase("all")) {
            users = String.join(";", GoogleHelper.userCreds.keySet());
        }
    }

    public Map<String, List<Results>> getUsersInfo() {
        userResults = new HashMap<>();
        Stream<String> creds = Arrays.stream(users.split(";"));
//        TaskMapping.generatePackageXML();
        TaskMapping taskMap = new TaskMapping("DEFAUT");
        creds.parallel().forEach(value -> {
            SalesforceHepler helper = new SalesforceHepler(value, GoogleHelper.userCreds.get(value), userResults, taskMap);
            helper.processUser();

        });
        return userResults;
    }

    public List<CredentialsStorage> getUserLogins() {

        return userLogins;
    }

    public static class CredentialsStorage {
        public String userName;
        public String password;
        public String fio;
        public String group;
        public String lastTaskUserName;
        public String lastTaskPassword;

        public CredentialsStorage(List<Object> info) {
            this.userName = String.valueOf(info.get(0));
            this.password = String.valueOf(info.get(1));
            this.lastTaskPassword = String.valueOf(info.get(3));
            this.lastTaskUserName = String.valueOf(info.get(2));
            this.fio = String.valueOf(info.get(4));
            this.group = String.valueOf(info.get(5));
        }
    }
}
