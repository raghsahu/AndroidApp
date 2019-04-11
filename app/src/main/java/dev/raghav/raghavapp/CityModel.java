package dev.raghav.raghavapp;

class CityModel {
private String masterdata_id;
private String masterdata_name;

    public CityModel(String masterdata_id, String masterdata_name) {
        this.masterdata_id=masterdata_id;
        this.masterdata_name=masterdata_name;

    }

    public String getMasterdata_id() {
        return masterdata_id;
    }

    public String getMasterdata_name() {
        return masterdata_name;
    }

    public void setMasterdata_name(String masterdata_name) {
        this.masterdata_name = masterdata_name;
    }

    public void setMasterdata_id(String masterdata_id) {
        this.masterdata_id = masterdata_id;
    }
}
