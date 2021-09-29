package educationapplication.onedaywiser;

public class Lesson {
    private int Id;
    private String Name;

    public Lesson(int id,String name) {
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
