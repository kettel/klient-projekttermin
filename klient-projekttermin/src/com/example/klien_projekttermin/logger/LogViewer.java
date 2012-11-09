package com.example.klien_projekttermin.logger;

import com.example.klien_projekttermin.MainActivity;
import com.example.klien_projekttermin.R;
import com.example.klien_projekttermin.R.layout;
import com.example.klien_projekttermin.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
/**
 * En väldigt enkel logviewer för enheten.
 * @author Eric Karlsson
 *
 */
public class LogViewer extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent intent = getIntent();
        String content = intent.getStringExtra(MainActivity.LOGCONTENT);
     
        TextView textView = new TextView(this);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setTextSize(10);
        textView.setText(content);
       
        setContentView(textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_log_viewer, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
