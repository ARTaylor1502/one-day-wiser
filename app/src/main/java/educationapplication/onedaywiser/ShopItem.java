package educationapplication.onedaywiser;

import java.net.URL;

public class ShopItem {

    private int Id;
    private String Name;
    private URL Uri;
    private int Cost;

    public ShopItem(int id, String name, URL uri, int cost) {
        Id = id;
        Name = name;
        Uri = uri;
        Cost = cost;
    }

    public int getId(){
        return Id;
    }

    public String getName(){
        return Name;
    }

    public URL getUri(){
        return Uri;
    }

    public int getCost(){
        return Cost;
    }
}
