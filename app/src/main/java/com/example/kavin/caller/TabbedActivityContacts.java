package com.example.kavin.caller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kavin.caller.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.jar.Manifest;

/**
 * Created by kavin on 7/4/17.
 */
public class TabbedActivityContacts extends Fragment {

    public class ContactArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public ContactArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.contact_list, parent, false);
            final TextView nameTextView = (TextView) rowView.findViewById(R.id.name_entry);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.NEW);
            nameTextView.setText(values[position]);
            Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL/" + values[position]);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true));

//            rowView.setId(position);
//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "Clicked " + values[rowView.getId()], Toast.LENGTH_SHORT).show();
//                }
//            });


            Button deleteButton = (Button) rowView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "delete " + nameTextView.getText(), Toast.LENGTH_SHORT).show();
                    File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL/" + nameTextView.getText());
                    file.delete();
                    startActivity(new Intent(getActivity(), TabbedActivity.class));
                }
            });

            return rowView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contacts, container, false);

        GridView contactListView = (GridView) rootView.findViewById(R.id.gridView);
        if (contactListView == null) {
            System.out.println("contactListView is null");
        }
        String[] contacts = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MARVEL").list();
        contactListView.setAdapter(new ContactArrayAdapter(getActivity(), contacts));

//        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getActivity(), "REQUESTING CALL PERMISSION", Toast.LENGTH_LONG).show();
//            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, 0);
//            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getActivity(), "UNABLE", Toast.LENGTH_LONG).show();
//            }
//        }

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView phoneNumberView = (TextView) view.findViewById(R.id.name_entry);


                Toast.makeText(getActivity(), "should call number:" + phoneNumberView.getText(), Toast.LENGTH_SHORT).show();

//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + phoneNumberView.getText()));
//                startActivity(callIntent);

                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
                    CallLog callLog = (CallLog) objectInputStream.readObject();
                    objectInputStream.close();
                    callLog.add((String) phoneNumberView.getText(), Calendar.getInstance().getTime());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
                    objectOutputStream.writeObject(callLog);
                    objectOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + phoneNumberView.getText()));
                startActivity(phoneIntent);

                //new ObjectInputStream(new FileInputStream(Environment.DIRECTORY_DOCUMENTS + "/MARVEL/CallLog"))
            }
        });

//        Button deleteButton = (Button) contactListView.findViewById(R.id.deleteButton);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "delete", Toast.LENGTH_SHORT).show();
//            }
//        });

        return rootView;
    }
}
