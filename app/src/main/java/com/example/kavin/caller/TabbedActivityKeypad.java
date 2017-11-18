package com.example.kavin.caller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * Created by kavin on 7/4/17.
 */
public class TabbedActivityKeypad extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.keypad, container, false);

        FloatingActionButton callFloatingButton = (FloatingActionButton) rootView.findViewById(R.id.call);
        callFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneNumberEditText = (EditText) rootView.findViewById(R.id.phoneNumber);
                String phoneNumber = phoneNumberEditText.getText().toString();
                Snackbar.make(view, "Should call " + phoneNumber, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL2/CallLog"));
                    CallLog callLog = (CallLog) objectInputStream.readObject();
                    objectInputStream.close();
                    callLog.add(phoneNumber, Calendar.getInstance().getTime());
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
            }
        });

        Button addToContact = (Button) rootView.findViewById(R.id.addToContacts);
        addToContact.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
                EditText phoneNumberEditText = (EditText) rootView.findViewById(R.id.phoneNumber);
                String phoneNumber = phoneNumberEditText.getText().toString();
                Intent nextIntent = new Intent(getActivity(), DrawOnScreenActivity.class);
                nextIntent.putExtra("phoneNumber", phoneNumber);
                startActivity(nextIntent);
            }
        });

        return rootView;
    }
}
