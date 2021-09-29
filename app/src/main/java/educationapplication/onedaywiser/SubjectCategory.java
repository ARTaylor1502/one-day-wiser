package educationapplication.onedaywiser;

public class SubjectCategory {

    private int Id;
    private String Name;

    public SubjectCategory(int id,String name) {
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
