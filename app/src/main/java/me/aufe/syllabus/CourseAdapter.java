package me.aufe.syllabus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CourseAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<CourseData> mData;

    public CourseAdapter(Context context, List<CourseData> mData) {
        layoutInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.org = (TextView) convertView.findViewById(R.id.org);
            viewHolder.sno = (TextView) convertView.findViewById(R.id.sno);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CourseData courseData =  mData.get(position);
        viewHolder.name.setText(courseData.getName());
        viewHolder.org.setText(courseData.getOrg());
        viewHolder.sno.setText(courseData.getSno());
        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView org;
        TextView sno;
    }
}
