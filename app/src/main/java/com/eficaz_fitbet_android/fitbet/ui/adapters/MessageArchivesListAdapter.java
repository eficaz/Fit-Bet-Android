package com.eficaz_fitbet_android.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.model.Archives;
import com.eficaz_fitbet_android.fitbet.ui.MessageActivity;
import com.eficaz_fitbet_android.fitbet.ui.fragments.CreateGroupFragment;
import com.eficaz_fitbet_android.fitbet.utils.CircleImageView;
import com.eficaz_fitbet_android.fitbet.utils.Contents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageArchivesListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public ArrayList<Archives> groupListModels;
   // public List<Archives> selected_usersList=new ArrayList<>();
    private List<Archives> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    public MessageArchivesListAdapter(Context context, ArrayList<Archives> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_archives_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Archives m = groupListModels.get(position);
        final ViewHolder viewholder = (ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
        try{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(m.getDate());
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String output = null;
            output = outputformat.format(date);
            viewholder.tv_Description.setText(output);
        }catch (Exception e){
            e.printStackTrace();
        }
        //viewholder.tv_Description.setText(m.getDescription());
        viewholder.left_days.setText(""+Double.parseDouble(m.getDistance())/1000+" km");


        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, MessageActivity.class);
                i.putExtra(Contents.MYBETS_betid,m.getBetid());
                i.putExtra(Contents.MYBETS_betname,m.getBetname());
                i.putExtra(Contents.TOTAL_PARTICIPANTS,m.getTotal());
                constant.startActivity(i);
            }
        });
        viewholder.tv_groupCount.setText(m.getCredit());


        /*if (!m.getProfile_pic().equals("NA")) {
            if (m.getReg_key().equals("normal")&&m.getImage_status().equals("0")){
                //Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC)).into(img_user);
                Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            }else{
                Picasso.get().load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            }
        }
        else{
            viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
        }*/



    }
    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        }else {
            for (Archives wp : contactListFiltered) {
                if (wp.getBetname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Description,left_days,tv_groupCount;
        LinearLayout rowView;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            left_days= convertView.findViewById(R.id.left_days);
            tv_groupCount = convertView.findViewById(R.id.users_count);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

