package com.garrettshorr.gooutside;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 5/21/2016.
 */
public class AdventurePlace {
    private double latitude;
    private double longitude;
    private String icon;
    private String id;
    private String name;
    private String placeId;

    public AdventurePlace(double latitude, double longitude, String icon, String id, String name, String placeId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public String toString() {
        return "AdventurePlace{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                '}';
    }

    public static List<AdventurePlace> parseJSONAdventurePlaces(JSONObject j) {
        List<AdventurePlace> places = new ArrayList<>();
        try {
            JSONArray results = j.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                AdventurePlace p = new AdventurePlace(
                        place.getJSONObject("geometry")
                                .getJSONObject("location")
                                .getDouble("lat"),
                        place.getJSONObject("geometry")
                                .getJSONObject("location")
                                .getDouble("lng"),
                        place.getString("icon"),
                        place.getString("id"),
                        place.getString("name"),
                        place.getString("place_id")
                );
                places.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return places;
    }
}
