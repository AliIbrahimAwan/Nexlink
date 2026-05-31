
package nexlink.server.models;
/**
 *
 * @author ALI
 */

public class FileModel {
    private int id;
    private String fileName;
    private String sender;
    private String receiver;

    // Streamlined constructor
    public FileModel(String fileName, String sender, String receiver) {
        this.fileName = fileName;
        this.sender = sender;
        this.receiver = receiver;
    }

    // Getters
    public int getId() { return id; }
    public String getFileName() { return fileName; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
}