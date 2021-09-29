package educationapplication.onedaywiser;

public class SchoolClass {

    private int Id;
    private String Name;

    public SchoolClass(int id,String name) {
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
