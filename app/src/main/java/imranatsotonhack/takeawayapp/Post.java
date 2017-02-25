package imranatsotonhack.takeawayapp;

/**
 * Created by imran on 25/02/2017.
 */

public class Post {
    String food;
    float lng;
    float lat;
    String uid;

    public Post(String food, float lng, float lat, String uid) {
        this.food = food;
        this.lng = lng;
        this.lat = lat;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Post{" +
                "food='" + food + '\'' +
                ", friends=" + lng +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }

    public String getFood() {
        return food;
    }

    public float getLng() {
        return lng;
    }

    public float getLat() {
        return lat;
    }

    public String getUid() {
        return uid;
    }
}
