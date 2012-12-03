package com.klient_projekttermin;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class QoSInterface extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qo_sinterface);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_qo_sinterface, menu);
        return true;
    }
}
