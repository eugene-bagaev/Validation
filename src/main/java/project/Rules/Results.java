package project.Rules;

public class Results {

    public String nameMetadata;
    public String status;
    public String message;

    public Results (String nameMetadata, String message, Boolean status){
        this.nameMetadata = nameMetadata;
        this.message = message;
        if(status){
            this.status = "SUCCESS";
        } else {
            this.status = "ERROR";
        }
    }
}
