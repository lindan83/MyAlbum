package com.lance.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void test(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_test_nested:
                startActivity(new Intent(this, TestNestedRecyclerViewActivity.class));
                break;
            case R.id.btn_test_grid_group:
                startActivity(new Intent(this, TestGroupRecyclerViewActivity.class));
                break;
        }
    }
}
