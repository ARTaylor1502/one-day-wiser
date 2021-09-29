package educationapplication.onedaywiser;

public class MessageThread {

    private int Id;
    private String Name;

    public MessageThread(int id, String name) {
        Id = id;
        Name = name;
    }

    public int getId(){
        return Id;
    }

    public String getName(){
        return Name;
    }
}
