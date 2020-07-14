package com.example.map_pa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private ArrayList<PostItem> items;

    public PostAdapter (Context context, ArrayList<PostItem> memos) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = memos;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PostItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) {
            view = inflater.inflate(R.layout.item_layout, viewGroup, false);
        }

        PostItem item = items.get(i);

        TextView tv1 = (TextView)view.findViewById(R.id.usernamePost);
        TextView tv2 = (TextView)view.findViewById(R.id.contentPost);
        TextView tv3 = (TextView)view.findViewById(R.id.tagPost);

        tv1.setText(item.getUsername());
        tv2.setText(item.getContent());
        tv3.setText(item.getTag());

        return view;
    }
}
