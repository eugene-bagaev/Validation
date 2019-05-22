package project.REST.API;

import org.springframework.web.bind.annotation.*;
import project.AuthorizationValidator;
import project.GoogleDocsWriter;
import project.Processors.RequestProcessor;
import project.Rules.Constants;
import project.Rules.Results;
import project.Storages.FileStorage;
import org.springframework.web.bind.annotation.*;
import project.Processors.RequestProcessor;
import project.Rules.Results;
import project.TaskMapping;
import project.ToolingHelper;

import java.io.FileNotFoundException;
import java.util.*;

@RestController
public class PageRestController {

    @CrossOrigin
    @PostMapping("/usersInfo")
    public Map<String, List<Results>> getUsersInfo(@RequestBody String userNames) {
        return new RequestProcessor(userNames).getUsersInfo();
    }
    public static class User {
        public String userName;
        public String pass;
        public String data = "";
    }

    @CrossOrigin
    @GetMapping("/getUsers")
    public List<RequestProcessor.CredentialsStorage> getAllUsers() {
        return new RequestProcessor().getUserLogins();
    }

    @CrossOrigin
    @PostMapping("/userFile")
    public FileStorage getUserFile(@RequestBody String userNameAndFile) {
        String[] info = userNameAndFile.split(";");

        for (FileStorage file : RequestProcessor.files) {
            String fileName = (file.fileName.contains(".")  ? file.fileName.substring(0, file.fileName.indexOf('.')) : file.fileName);
            if (!info[0].equalsIgnoreCase(file.fileOwner)
                    || !fileName.equalsIgnoreCase(info[1])) continue;
            return file;
        }


        return null;
    }

    @CrossOrigin
    @PostMapping("/feedback")
    public void appendFeedBackTextToFile(@RequestBody String text) {
        new GoogleDocsWriter(Constants.FEEDBACK_DOCUMENT_ID, text).writeTextToFile();
    }

    @CrossOrigin
    @PostMapping("/authorization")
    public String checkCreds(@RequestBody String nameAndPassword) throws FileNotFoundException {
        String[] creds = nameAndPassword.split(";");
        return AuthorizationValidator.checkCreds(creds[0], creds[1]);
    }
///////////////////////// TASK MAPPPING /////////////////////////
    @CrossOrigin
    @PostMapping("/saveTaskMapping")
    public String saveTaskMapping(@RequestBody String jsonFile) {
        TaskMapping.saveJsonFile(jsonFile);
        return "ok D9d9";
    }
    @CrossOrigin
    @GetMapping("/getTaskMapping")
    public String getTaskMapping() {
        return TaskMapping.getJsonFile();
    }
    @CrossOrigin
    @PostMapping("/refreshTaskMapping")
    public void refreshTaskMapping() {

//        TaskMapping.loadTaskMapping();
        System.out.println("loadTaskMapping " );
    }
}