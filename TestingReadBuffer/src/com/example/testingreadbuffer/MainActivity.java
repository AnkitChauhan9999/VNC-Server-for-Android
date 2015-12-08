package com.example.testingreadbuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Object;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
boolean readState;
TextView text;
String fbstr;
String filename = "ScreenSho";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView)findViewById(R.id.editText1);
		File fbfile = new File("/dev/graphics/fb0");
		byte[] filebyte = new byte[(int) fbfile.length()];
		try{
		//DataInputStream dis = new DataInputStream(new FileInputStream(file));
		FileInputStream fin = new FileInputStream(fbfile);
		fin.read(filebyte);
		//	dis.readFully(filebyte);
		//  dis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		readState = isExternalStorageWritable();
		if(readState==true){
			File myFile = new File("/sdcard/"+filename+"2.raw");  
            try {
			myFile.createNewFile();  
            FileOutputStream fOut = new FileOutputStream(myFile);  
            fOut.write(filebyte);
            fOut.flush();
            fOut.close();
            fbstr = filebyte.toString();
            text.setText(fbstr);
            /*DataOutputStream dout = new DataOutputStream(fOut);
            dout.write(filebyte);
            dout.close();  
            fOut.close();*/    
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//text.setText("Storage Complete");
	}
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
