package educationapplication.onedaywiser;

public class Group {

    private String Name;
    private String ImageUrl;
    private int BehaviourPoints;
    private int AchievementPoints;

    public Group(String name, String url, int behaviourPoints,int achievementPoints) {
        Name = name;
        ImageUrl = url;
        BehaviourPoints = behaviourPoints;
        AchievementPoints = achievementPoints;
    }

    public String getName(){
        return Name;
    }

    public String getImageUrl(){
        return ImageUrl;
    }

    public int getBehaviourPoints(){
        return BehaviourPoints;
    }

    public int getAchievementPoints(){
        return AchievementPoints;
    }
}
