package com.example.kavin.caller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kavin.caller.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kavin on 7/4/17.
 */
public class TabbedActivityRecent extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recent, container, false);

        CallLog callLog = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
            callLog = (CallLog) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (String number : callLog.getNumbers()) {
            System.out.println("PUNDA:" + number);
        }
        for (Date time : callLog.getTimes()) {
            System.out.println("YO:" + time);
        }

        String[] log = new String[callLog.getNumbers().size()];
        for (int i=0; i<callLog.getNumbers().size(); ++i) {
            log[callLog.getNumbers().size() - 1 - i] = callLog.getNumbers().get(i) + "&" + callLog.getTimes().get(i);
        }

        ListView recentListView = (ListView) rootView.findViewById(R.id.recentListView);
        recentListView.setAdapter(new RecentArrayAdapter(getActivity(), log));

        recentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView phoneNumberView = (TextView) view.findViewById(R.id.textView);
                String phoneNumber = phoneNumberView.getText().toString().substring(0,phoneNumberView.getText().toString().indexOf('\n'));

//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + phoneNumberView.getText()));
//                startActivity(callIntent);

                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
                    CallLog callLog = (CallLog) objectInputStream.readObject();
                    objectInputStream.close();
                    callLog.add((String) phoneNumber, Calendar.getInstance().getTime());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
                    objectOutputStream.writeObject(callLog);
                    objectOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(phoneIntent);

                //new ObjectInputStream(new FileInputStream(Environment.DIRECTORY_DOCUMENTS + "/MARVEL/CallLog"))
            }
        });

        return rootView;
    }

    public class RecentArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public RecentArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.recent_list_item, parent, false);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            TextView textView = (TextView) rowView.findViewById(R.id.textView);
            String numberTime = values[position];
            String number = numberTime.substring(0, numberTime.indexOf('&'));
            String time = numberTime.substring(numberTime.indexOf('&') + 1, numberTime.length());
            if (new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL/" + number).isFile()) {
                Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL/" + number);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
            Date date = null;
            try {
                date = sdf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timeDiff = Calendar.getInstance().getTime().getTime() - date.getTime();
            System.out.println("OTHA: " + timeDiff);

            textView.setText(number + "\n" + getTimeDiffString(timeDiff));
            return rowView;
        }

        public String getTimeDiffString(long diff) {
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));

            if (diffInDays >= 1) {
                return diffInDays + " days ago";
            } else if (diffHours >= 1) {
                return diffHours + " hours ago";
            }
            return diffMinutes + " minutes ago";
        }
    }
}
