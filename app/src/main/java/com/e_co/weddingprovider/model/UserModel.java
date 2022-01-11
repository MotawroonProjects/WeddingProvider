package com.e_co.weddingprovider.model;

import java.io.Serializable;

public class UserModel extends StatusResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable {
        private int id;
        private String user_type;
        private String logo;
        private String name;
        private String phone_code;
        private String phone;
        private String gender;
        private double latitude;
        private double longitude;
        private String address;
        private String token;
        private static String firebase_token;

        public int getId() {
            return id;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getLogo() {
            return logo;
        }

        public String getName() {
            return name;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getGender() {
            return gender;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getAddress() {
            return address;
        }

        public String getToken() {
            return token;
        }

        public String getFirebase_token() {
            return firebase_token;
        }

        public void setFirebase_token(String firebase_token) {
            this.firebase_token = firebase_token;
        }
    }

}
