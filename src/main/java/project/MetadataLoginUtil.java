package project;

import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import java.util.HashMap;
import java.util.Map;

public class MetadataLoginUtil {

    public static final String LOGIN_URL = "https://login.salesforce.com/services/Soap/u/43.0";
    private static LoginResult loginResultCommon;

    public static Map<String, String> mapUserToSessionId = new HashMap<>();
    public static Map<String, LoginResult> mapUserToLoginResult = new HashMap<>();

    public static LoginResult getLoginResult() {
        return loginResultCommon;
    }

    public static MetadataConnection login(String username, String pass) throws ConnectionException {

        final LoginResult loginResult = loginToSalesforce(username, pass, LOGIN_URL);
        loginResultCommon = loginResult;

        mapUserToSessionId.put(username, loginResult.getSessionId());
        mapUserToLoginResult.put(username, loginResult);


        return createMetadataConnection(loginResult);
    }

    private static MetadataConnection createMetadataConnection(final LoginResult loginResult) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(loginResult.getMetadataServerUrl());
        config.setSessionId(loginResult.getSessionId());
        return new MetadataConnection(config);
    }

    private static LoginResult loginToSalesforce(
            final String username,
            final String password,
            final String loginUrl) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(loginUrl);
        config.setServiceEndpoint(loginUrl);
        config.setManualLogin(true);


        return (new PartnerConnection(config)).login(username, password);
    }
}

