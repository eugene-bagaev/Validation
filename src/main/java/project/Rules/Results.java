package project.Rules;

public class Results {

    public String nameMetadata;
    public String status;
    public String message;
    public String file;

    public Results (String nameMetadata, String message, Boolean status){
        this.nameMetadata = nameMetadata;
        this.message = message;
        if(status){
            this.status = "SUCCESS";
        } else {
            this.status = "ERROR";
        }
    }

    public Results (String nameMetadata, String message, Boolean status, String file ){
        this.nameMetadata = nameMetadata;
        this.message = message;
        this.file = file;
        if(status){
            this.status = "SUCCESS";
        } else {
            this.status = "ERROR";
        }
    }


}
