package gzt.manus.findqueuke;

public class DataHolder {
    private static DataHolder instance;
    private String email;
    private String location;
    private String name;



    private DataHolder() {
        // Private constructor to prevent instantiation
    }

    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
