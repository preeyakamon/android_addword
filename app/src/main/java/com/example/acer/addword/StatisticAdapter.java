package com.example.acer.addword;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ACER on 7/22/2017.
 */

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    private Context ctx;
    private JSONArray mData;

    public StatisticAdapter(Context c, JSONArray a) {
        this.ctx = c;
        this.mData = a;
    }

    @Override
    public StatisticAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.custom_item_statistic, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(StatisticAdapter.ViewHolder holder, int position) {
        try {
            JSONObject item = mData.getJSONObject(position);
            holder.tvUsername.setText(item.getString("name"));
            holder.tvTime.setText(item.getString("totaltime"));
            holder.tvScore.setText(item.getString("score"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvScore, tvTime;
        public ViewHolder(View v) {
            super(v);
            tvUsername = (TextView) v.findViewById(R.id.tvUsername);
            tvScore = (TextView) v.findViewById(R.id.tvScore);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
        }
    }
}
