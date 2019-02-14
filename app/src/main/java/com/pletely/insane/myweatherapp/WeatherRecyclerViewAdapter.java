package com.pletely.insane.myweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pletely.insane.myweatherapp.pojos.fiveday.List;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private ArrayList<List> forecastList;
    private ArrayList<String> days;

    public WeatherRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<List> forecastList, ArrayList<String> days) {
        this.forecastList = forecastList;
        this.days = days;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weather_day_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (forecastList.size() > 0) {
            holder.mDay.setText(days.get(position));
            holder.mDayTemp.setText(String.format(Locale.getDefault(), "%.0f", forecastList.get(position).getMain().getTemp()));

            switch ((forecastList.get(position).getWeather().get(0).getMain()).toLowerCase()) {

                case "rain":
                    holder.mType.setImageResource(R.drawable.rain);
                    break;

                case "clear sky":
                    holder.mType.setImageResource(R.drawable.clear);
                    break;

                case "few clouds":
                    holder.mType.setImageResource(R.drawable.partlysunny);
                    break;

                default:
                    holder.mType.setImageResource(R.drawable.clear);
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (forecastList != null) {
            size = forecastList.size();
        }
        return size;
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    public TextView mDay;
    public ImageView mType;
    public TextView mDayTemp;

    public ViewHolder(View v) {
        super(v);

        mDay = v.findViewById(R.id.day);
        mType = v.findViewById(R.id.weather_type);
        mDayTemp = v.findViewById(R.id.temp);
    }
}
