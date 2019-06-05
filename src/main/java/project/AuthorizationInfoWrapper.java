package project;

public class AuthorizationInfoWrapper {
    public String userName;
    public String authorizationToken;

    public AuthorizationInfoWrapper(String userName, String authorizationToken) {
        this.userName = userName;
        this.authorizationToken = authorizationToken;
    }

}
