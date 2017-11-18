package com.example.kavin.caller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DrawOnScreenActivity extends AppCompatActivity {
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_on_screen);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        ((TextView) findViewById(R.id.displayPhNo)).setText(phoneNumber);

        final View v = new TouchEventView(this,null);
        v.setId(R.id.canvas);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        rl.addView(v);
        //setContentView(new TouchEventView(this, null));

        //R.layout.activity_draw_on_screen

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "REQUESTING STORAGE PERMISSION", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                try {
                    File contactsDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MARVEL");
                    if (!contactsDir.isDirectory()) {
                        contactsDir.mkdir();
                    }

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(contactsDir.getPath() + "/" + phoneNumber));
                    Snackbar.make(view, "Saving canvas in " + contactsDir.getPath() + "/" + phoneNumber, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startActivity(new Intent(DrawOnScreenActivity.this, TabbedActivity.class));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    /**
     * Created by kavin on 8/4/17.
     */
    public static class TouchEventView extends View {
        private Paint paint = new Paint();
        private Path path = new Path();

        public TouchEventView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5f);
            this.setBackgroundColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float xPos = motionEvent.getX();
            float yPos = motionEvent.getY();

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(xPos, yPos);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(xPos, yPos);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }
    }
}
