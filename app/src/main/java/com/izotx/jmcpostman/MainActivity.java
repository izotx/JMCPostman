package com.izotx.jmcpostman;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    JMCVolleyRequest request = new JMCVolleyRequest();


  public File bitmapToFile(Bitmap bitmap, String filename){
      File f = new File(getApplicationContext().getCacheDir(), filename);
    try {
        f.createNewFile();
    }catch (Exception ex){

    }

//Convert bitmap to byte array
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
      byte[] bitmapdata = bos.toByteArray();

      try {
//write the bytes in file
          FileOutputStream fos = new FileOutputStream(f);
          fos.write(bitmapdata);
          fos.flush();
          fos.close();

      }catch(Exception ex)
      {

      }

      return f;
  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.big);
//        File f = bitmapToFile(icon,"big");
        request.sendFile(icon,"token","3",this);

     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
