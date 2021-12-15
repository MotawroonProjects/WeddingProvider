package com.apps.weddingprovider.model;

import com.apps.weddingprovider.model.WeddingHallModel;

import java.io.Serializable;
import java.util.List;

public class ResevisionModel  implements Serializable {
    private int id;
    private String service_name;
    private String user_name;
    private String user_phone;
    private int service_id;
    private int user_id;
    private String date;
    private double main_item_price;
    private double extra_item_price;
    private String bar_code;
    private String bar_code_image;
    private String day;
    private String hour;
    private String status;
    private WeddingHallModel service;
    private List<ResevisionExtraItems> reservation_extra_items;

    public int getId() {
        return id;
    }

    public String getService_name() {
        return service_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public int getService_id() {
        return service_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public double getMain_item_price() {
        return main_item_price;
    }

    public double getExtra_item_price() {
        return extra_item_price;
    }

    public String getBar_code() {
        return bar_code;
    }

    public String getBar_code_image() {
        return bar_code_image;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getStatus() {
        return status;
    }

    public WeddingHallModel getService() {
        return service;
    }

    public List<ResevisionExtraItems> getReservation_extra_items() {
        return reservation_extra_items;
    }

    public class ResevisionExtraItems implements Serializable{
        private int id;
        private int reservation_id;
        private int service_item_id;
        private String item_name;
        private double item_price;

        public int getId() {
            return id;
        }

        public int getReservation_id() {
            return reservation_id;
        }

        public int getService_item_id() {
            return service_item_id;
        }

        public String getItem_name() {
            return item_name;
        }

        public double getItem_price() {
            return item_price;
        }
    }
}
