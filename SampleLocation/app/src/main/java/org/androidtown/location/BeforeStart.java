package org.androidtown.location;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017-03-19.
 */

public class beforeStart extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relative = (RelativeLayout)inflater.inflate(R.layout.before_start_layout,null);
        relative.setBackgroundColor(Color.BLACK);

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.before_start_layout);
        setContentView(relative);


        button = (Button) findViewById(R.id.button);




    }

    public void onStartButtonClicked(View view) {

        Intent intent = new Intent(beforeStart.this, MainActivity.class);
        startActivity(intent);

    }
}
