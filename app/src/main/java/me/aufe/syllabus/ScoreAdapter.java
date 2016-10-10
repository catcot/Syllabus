package me.aufe.syllabus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<ScoreData> mData;

    public ScoreAdapter(Context context, List<ScoreData> mData) {
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
            convertView = layoutInflater.inflate(R.layout.s_list_item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.s_name);
            viewHolder.org = (TextView) convertView.findViewById(R.id.s_org);
            viewHolder.sno = (TextView) convertView.findViewById(R.id.s_sno);


            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScoreData scoreData =  mData.get(position);
        viewHolder.name.setText(scoreData.getName());
        viewHolder.org.setText(scoreData.getOrg());
        viewHolder.sno.setText(scoreData.getSno());
        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView org;
        TextView sno;
    }
}
