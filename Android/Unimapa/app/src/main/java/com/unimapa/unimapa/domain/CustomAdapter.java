package com.unimapa.unimapa.domain;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.unimapa.unimapa.ServerConnection;
import com.unimapa.unimapa.R;
import com.unimapa.unimapa.dataBase.MapaDataBase;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by hardik on 9/1/17.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    public static ArrayList<Mapa> modelArrayList;

    public CustomAdapter(Context context, ArrayList<Mapa> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MapaDataBase MDB;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            holder.tvMapa = (TextView) convertView.findViewById(R.id.mapa);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvMapa.setText(modelArrayList.get(position).getName());

        holder.checkBox.setChecked(modelArrayList.get(position).getSelected());

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        MDB = new MapaDataBase(this.context);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                //TextView tv = (TextView) tempview.findViewById(R.id.mapa);
                Integer pos = (Integer)  holder.checkBox.getTag();

                if(modelArrayList.get(pos).getSelected()){
                    modelArrayList.get(pos).setSelected(false);
                    MDB.removeById(modelArrayList.get(pos).getId());
                    try {
                        ServerConnection serverConnection = new ServerConnection(context);
                        serverConnection.sendJson("/maps/"+ modelArrayList.get(pos).getId()+"/subscriptions"   //TODO: em producao"https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps/"
                                ,"{\"name\": \"testando \"}","DELETE");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Retirou: " + modelArrayList.get(pos).getName(), Toast.LENGTH_SHORT).show();

                    deleteNotificationChannel(modelArrayList.get(pos).getName());
                }else {
                    modelArrayList.get(pos).setSelected(true);
                    MDB.insertData(new Mapa(modelArrayList.get(pos).getId(),modelArrayList.get(pos).getName(),modelArrayList.get(pos).getPosts(),true, "HEAT"));
                    try {
                        ServerConnection serverConnection = new ServerConnection(context);
                        serverConnection.sendJson("/maps/"+ modelArrayList.get(pos).getId()+"/subscriptions"   //TODO: em producao"https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps/"
                                ,"{\"name\": \"testando \"}","POST");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Adicionou: " + modelArrayList.get(pos).getName(), Toast.LENGTH_SHORT).show();

                    createNotificationChannel(modelArrayList.get(pos).getName());
                }
            }
        });

        return convertView;
    }

    private void createNotificationChannel(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String descriptionText = context.getResources().getString(R.string.maps_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(name, name, importance);
            mChannel.setDescription(descriptionText);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private void deleteNotificationChannel(String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.deleteNotificationChannel(name);
        }
    }

    private class ViewHolder {

        protected CheckBox checkBox;
        private TextView tvMapa;

    }

}


