package com.jhcomn.lambda.packages.test.model;

import java.io.Serializable;

/**
 * weibo user profile
 * Created by shimn on 2016/12/27.
 */
public class User implements Serializable{

    private String id;

    private String account;

//    private String password;
//
//    private String photoAddress;
//
//    private String nickname;
//
//    private String identity;

    private String lastTime;

    private String gender;

    private String age;

    private String location;

    private String province;

    private String city;

//    private String personalSignature;

    private String advantagedSubject;

    private String disAdvantagedSubject;

//    private String bi_followers_count;
//
//    private String followers_count;
//
//    private String friends_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdvantagedSubject() {
        return advantagedSubject;
    }

    public void setAdvantagedSubject(String advantagedSubject) {
        this.advantagedSubject = advantagedSubject;
    }

    public String getDisAdvantagedSubject() {
        return disAdvantagedSubject;
    }

    public void setDisAdvantagedSubject(String disAdvantagedSubject) {
        this.disAdvantagedSubject = disAdvantagedSubject;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", advantagedSubject='" + advantagedSubject + '\'' +
                ", disAdvantagedSubject='" + disAdvantagedSubject + '\'' +
                '}';
    }
}
