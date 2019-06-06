package project.Storages;


public class FileStorage {
    public String fileName;
    public String fileOwner;
    public String content;

    public FileStorage(String fileName, String fileOwner, String content) {
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.content = content;
    }
}
