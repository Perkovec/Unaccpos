package com.perkovec.unaccpos;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.*;

public class MainActivity extends Activity implements SensorEventListener {

 TextView infoip;
 Button startserver;
 String message = "";
 ServerSocket serverSocket;
 String GyroData = "0 0 0";
 SensorManager sensorManager = null;
 Boolean serverstart = false;
 EditText port;
 static int servport = 25000;


 protected void onCreate(Bundle savedInstanceState) {
	 
  super.onCreate(savedInstanceState);
  sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
  setContentView(R.layout.main);
  infoip = (TextView) findViewById(R.id.infoip);
  port = (EditText) findViewById(R.id.port);
  startserver = (Button) findViewById(R.id.startserver);
  
  startserver.setOnClickListener(new OnClickListener(){
  	public void onClick(View v){
		if (!serverstart){
			servport = Integer.valueOf(port.getText().toString());
			Thread socketServerThread = new Thread(new SocketServerThread());
			socketServerThread.start();
			serverstart=true;
		}
	}
});
  
 }

 
 
 protected void onResume() {
	    super.onResume();
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_GAME);
	 }
 
 @Override
 protected void onStop() {
    super.onStop();
    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
 }
 
 public void onSensorChanged(SensorEvent event) {
	    synchronized (this) {
	        switch (event.sensor.getType()){
	        case Sensor.TYPE_ORIENTATION:
	        	GyroData=Float.toString(Math.round(event.values[0]))+" "+Float.toString(Math.round(event.values[1]))+" "+Float.toString(Math.round(event.values[2]));
	        break;
	 
	        }
	    }
	 }
 
 
 @Override
 public void onAccuracyChanged(Sensor sensor, int accuracy) {}  
 
 
 @Override
 protected void onDestroy() {
  super.onDestroy();

  if (serverSocket != null) {
   try {
    serverSocket.close();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }
 }

 private class SocketServerThread extends Thread {
  int count = 0;

  @Override
  public void run() {
   try {
    serverSocket = new ServerSocket(servport);
    MainActivity.this.runOnUiThread(new Runnable() {

     @Override
     public void run(){
					infoip.setText("Server was created, port: " + serverSocket.getLocalPort() );
		
     }
    });

    while (true) {
     Socket socket = serverSocket.accept();
     
     SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
       socket, count);
     socketServerReplyThread.run();

    }
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }

 }

 private class SocketServerReplyThread extends Thread {

  private Socket hostThreadSocket;
  int cnt;

  SocketServerReplyThread(Socket socket, int c) {
   hostThreadSocket = socket;
   cnt = c;
  }

  @Override
  public void run() {
   OutputStream outputStream;

   try {
    outputStream = hostThreadSocket.getOutputStream();
             PrintStream printStream = new PrintStream(outputStream);
             printStream.print(GyroData);
             printStream.close();

 

   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    message += "Something wrong! " + e.toString() + "\n";
   }

   MainActivity.this.runOnUiThread(new Runnable() {

    @Override
    public void run() {
     infoip.setText(message);
    }
   });
  }

 }


}