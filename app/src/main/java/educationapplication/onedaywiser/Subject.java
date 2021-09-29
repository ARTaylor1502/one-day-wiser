package educationapplication.onedaywiser;

public class Subject {

    private int Id;
    private String Name;

    public Subject(int id,String name) {
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
