
package com.pletely.insane.myweatherapp.pojos.fiveday;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FiveDayWeather implements Parcelable
{

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Double message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<com.pletely.insane.myweatherapp.pojos.fiveday.List> list = null;
    @SerializedName("city")
    @Expose
    private City city;
    public final static Parcelable.Creator<FiveDayWeather> CREATOR = new Creator<FiveDayWeather>() {


        @SuppressWarnings({
            "unchecked"
        })
        public FiveDayWeather createFromParcel(Parcel in) {
            return new FiveDayWeather(in);
        }

        public FiveDayWeather[] newArray(int size) {
            return (new FiveDayWeather[size]);
        }

    }
    ;

    protected FiveDayWeather(Parcel in) {
        this.cod = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cnt = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.list, (com.pletely.insane.myweatherapp.pojos.fiveday.List.class.getClassLoader()));
        this.city = ((City) in.readValue((City.class.getClassLoader())));
    }

    public FiveDayWeather() {
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Double getMessage() {
        return message;
    }

    public void setMessage(Double message) {
        this.message = message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public java.util.List<com.pletely.insane.myweatherapp.pojos.fiveday.List> getList() {
        return list;
    }

    public void setList(java.util.List<com.pletely.insane.myweatherapp.pojos.fiveday.List> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cod);
        dest.writeValue(message);
        dest.writeValue(cnt);
        dest.writeList(list);
        dest.writeValue(city);
    }

    public int describeContents() {
        return  0;
    }

}
