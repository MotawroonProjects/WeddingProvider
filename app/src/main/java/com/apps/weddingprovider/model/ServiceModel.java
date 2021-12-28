package com.apps.weddingprovider.model;

import java.io.Serializable;
import java.util.List;

public class ServiceModel implements Serializable {
    private String id;
    private String name;
    private String price;
    private String department_id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String address;
    private String main_image;
    private String video;
    private String video_link;
    private String text;
    private String rates_val;
    private String rates_count;
    private String is_shown;
    private String created_at;
    private String updated_at;
    private String max_limit;
    private List<ServiceMainItem> service_main_items;
    private List<ServiceExtraItem> service_extra_items;
    private List<ServiceImage> service_images;
    private List<OfferModel> offer;
    private List<ServiceRate> service_rates;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getMain_image() {
        return main_image;
    }

    public String getVideo() {
        return video;
    }

    public String getText() {
        return text;
    }

    public String getRates_val() {
        return rates_val;
    }

    public String getRates_count() {
        return rates_count;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<ServiceMainItem> getService_main_items() {
        return service_main_items;
    }

    public List<ServiceExtraItem> getService_extra_items() {
        return service_extra_items;
    }

    public List<ServiceImage> getService_images() {
        return service_images;
    }

    public List<OfferModel> getOffer() {
        return offer;
    }

    public List<ServiceRate> getService_rates() {
        return service_rates;
    }

    public String getMax_limit() {
        return max_limit;
    }

    public String getVideo_link() {
        return video_link;
    }

    public static class ServiceMainItem implements Serializable {
        private String id;
        private String item_type;
        private String name;
        private String price;
        private String details;
        private String service_id;

        public String getId() {
            return id;
        }

        public String getItem_type() {
            return item_type;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getDetails() {
            return details;
        }

        public String getService_id() {
            return service_id;
        }
    }

    public static class ServiceExtraItem implements Serializable {
        private String id;
        private String item_type;
        private String name;
        private String price;
        private String details;
        private String service_id;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public String getItem_type() {
            return item_type;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getDetails() {
            return details;
        }

        public String getService_id() {
            return service_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class ServiceImage implements Serializable {
        private String id;
        private String image;
        private String service_id;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getService_id() {
            return service_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class ServiceRate implements Serializable {
        private String id;
        private String service_id;
        private String user_id;
        private String rate_value;
        private String comment;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public String getService_id() {
            return service_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getRate_value() {
            return rate_value;
        }

        public String getComment() {
            return comment;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class OfferModel implements Serializable {
        private String id;
        private String service_id;
        private String user_id;
        private String image;
        private String name;
        private String price;
        private String text;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public String getService_id() {
            return service_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getText() {
            return text;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }


}
