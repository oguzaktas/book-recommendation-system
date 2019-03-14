
package main;

/**
 * Yazilim Laboratuvari I Proje 1
 * @author Oguz Aktas
 */
public class Users {
    
    private int id;
    private String location;
    private int age;
    private String password;

    public Users(int id, String location, int age, String password) {
        this.id = id;
        this.location = location;
        this.age = age;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public int getAge() {
        return age;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
