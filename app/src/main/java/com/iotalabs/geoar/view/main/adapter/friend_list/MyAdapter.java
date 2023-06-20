package com.iotalabs.geoar.view.main.adapter.friend_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lotalabsappui.R;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<FriendData> data;

    public MyAdapter(Context ctx,ArrayList<FriendData> data){
        this.ctx=ctx;
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view ==null){
            LayoutInflater inflater = LayoutInflater.from(ctx);
            view = inflater.inflate(R.layout.friendlist,viewGroup,false);
        }

        TextView text1 = view.findViewById(R.id.nameText);
        text1.setText(data.get(i).getName());

        return view;
    }
}