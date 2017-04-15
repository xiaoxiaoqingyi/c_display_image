package com.magicing.ndktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener

{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.txt1).setOnClickListener(this);
        findViewById(R.id.txt2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt1:
                startActivity(new Intent(this, SingleActivity.class));
                break;
            case R.id.txt2:
                startActivity(new Intent(this, HugeActivity.class));
                break;
        }
    }
}
