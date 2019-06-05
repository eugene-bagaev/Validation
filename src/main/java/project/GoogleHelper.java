package project;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import project.Processors.RequestProcessor;
import project.Rules.Results;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleHelper {

    // Configuration
    // app name
    private static final String APPLICATION_NAME        = "Google Sheets API Java Quickstart";
    // Document ID for read
    private static final String DOCUMENT_ID             = "1y5SmAB5364Iwzxpl12QLMWTbivsX9DYhwUZa6y55OHU";
    // Google cred JSON
    private static final String CREDENTIALS_FILE_PATH   = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH   = "tokens";
    // users creds for calling orgs
    public static Map<String, String> userCreds         = new HashMap<String, String>();
    public static String documentNname                  = "";

    private static final String range = "Q3:U";
    private static final String range1 = "A3:A";
    // Service config. Do not touch
    private static final JsonFactory JSON_FACTORY       = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES            = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleHelper.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void callDocument(List<RequestProcessor.CredentialsStorage> userLogins) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            System.out.println("Auth successfully");

            ValueRange table = service.spreadsheets().values().get(DOCUMENT_ID, range).execute();
            ValueRange table1 = service.spreadsheets().values().get(DOCUMENT_ID, range1).execute();

            populateMapWithUsersCreds(table.getValues(), table1.getValues(), userLogins);

        } catch (IOException ioEx) {
            System.out.println("IO Ex: " + ioEx);
        } catch (GeneralSecurityException genEx) {
            System.out.println("Gen Ex: " + genEx);
        }
    }

    public static void populateMapWithUsersCreds(
            List<List<Object>> tableStrings,
            List<List<Object>> groupsColumn,
            List<RequestProcessor.CredentialsStorage> userLogins
    ) {
        String currentGroup = "";
        for (int i = 0; i < tableStrings.size(); i ++) {
            if (tableStrings.get(i).size() == 5) {
                String text = String.valueOf(tableStrings.get(i).get(4));
                Pattern pattern = Pattern.compile("[а-яА-Яa-zA-Z]");

                Matcher matcher = pattern.matcher(text);
                if (!matcher.find()) continue;
                if (!pattern.matcher((String.valueOf(tableStrings.get(0)))).find()
                        || !pattern.matcher((String.valueOf(tableStrings.get(1)))).find()) continue;
                if (!(groupsColumn.size() - 1 < i)) {
                    if (!groupsColumn.get(i).isEmpty()) {
                        String gr = String.valueOf(groupsColumn.get(i).get(0));
                        if (gr.toLowerCase().contains("group")) {
                            currentGroup = gr;
                        }
                    }
                }
                if (!currentGroup.equalsIgnoreCase("")) {
                    tableStrings.get(i).add((Object) currentGroup);
                    if (userLogins == null) {
                        userLogins = new ArrayList<>();
                    }
                    userLogins.add(new RequestProcessor.CredentialsStorage(tableStrings.get(i)));
                    userCreds.put(String.valueOf(tableStrings.get(i).get(0)), String.valueOf(tableStrings.get(i).get(1)));
                }
            }
        }
    }
}
