package project.Rules;

public class Results {

    public String nameMetadata;
    public String status;
    public String message;
    public Boolean isShowed;

    public Results (String nameMetadata, String message, Boolean status){
        this.nameMetadata = nameMetadata;
        this.message = message;
        setStatus(status);
        isShowed = false;
    }

    public Results (VisualforcePageRule vfRule, String message, Boolean status) {
        this.nameMetadata = vfRule.nameFile;
        this.message = message;
        setStatus(status);
        isShowed = true;
    }

    private void setStatus(Boolean status) {
        if(status){
            this.status = "SUCCESS";
        } else {
            this.status = "ERROR";
        }
    }
}
