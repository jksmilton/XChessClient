package com.jksmilton.xchessclient.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.jksmilton.xchessclient.R;

public class XpandableListAdapter extends BaseExpandableListAdapter {

	private List<String> parents;
	private List<List<Object>> children;
	private LayoutInflater inflater;
	
	public XpandableListAdapter(Context context, List<String> parents, List<List<Object>> children){
		
		inflater = LayoutInflater.from(context);
		this.parents = parents;
		this.children = children;
		
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		return children.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View view,
			ViewGroup group) {
		
		if(view==null){
			view = inflater.inflate(R.layout.child_element, group, false);
		}
		
		TextView content = (TextView) view.findViewById(R.id.child_name);
		content.setText(children.get(arg0).get(arg1).toString());
		
		return view;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return children.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		return parents.get(arg0);
	}

	@Override
	public int getGroupCount() {
		
		return parents.size();
	}

	@Override
	public long getGroupId(int arg0) {
		
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View view, ViewGroup group) {
		if(view == null){
			
			view = inflater.inflate(R.layout.parent_element, group, false);
			
		}
		
		TextView content = (TextView) view.findViewById(R.id.group_name);
		content.setText(parents.get(arg0).toString());
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		
		return true;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer){
		super.registerDataSetObserver(observer);
	}

}
