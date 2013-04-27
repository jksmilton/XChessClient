package com.jksmilton.xchessclient.model;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jksmilton.xchessclient.R;

public class TranscriptAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context parentContext;
	public TranscriptAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);

		items = objects;
		parentContext = context;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(parentContext);
            v = vi.inflate(R.layout.child_element, parent, false);
        }
        String s = items.get(position);
        
        if (s != null) {
                TextView tt = (TextView) v.findViewById(R.id.child_name);
                
                tt.setText(s);
                tt.setTextColor(ColorStateList.valueOf(Color.WHITE));
        }
        return v;
}
	
	public void addItem(String item){
		
		items.add(0, item);
		this.notifyDataSetChanged();
		
	}
	
	
}
