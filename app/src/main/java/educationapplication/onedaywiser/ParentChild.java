package educationapplication.onedaywiser;

public class ParentChild {
    private int Id;
    private String Name;

    public ParentChild(int id,String name) {
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
