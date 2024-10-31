package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import java.util.List;

public class CustomAdapterFinalList extends BaseAdapter{
    private Context context;
    private List<String> data;

    public CustomAdapterFinalList(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {return data.size();}

    @Override
    public Object getItem(int position) { return data.get(position);}

    @Override
    public long getItemId(int position) {return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_final, parent, false);
        }
        ImageButton deletebutton = convertView.findViewById(R.id.delete_button);

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Entrant Delelted", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
