using UnityEngine;
using System;
using System.Collections;
using System.IO;
using System.Net;
using System.Text;
using System.Net.Sockets;

public class data : MonoBehaviour{

	public Socket connection;
	public Transform mobile;
	bool startprev = false;
	public float x=0;
	public float y=0;
	public float z=0;
	public float prevx=0;
	public float prevy=0;
	public float prevz=0;
	public string port = "25000";
	public bool connected =  false;
	void Start(){



	}

	void Update(){
		if (connected) {
						connection = new Socket (AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
						connection.Connect ("192.168.1.101", int.Parse(port));
						byte[] buffer = new byte[1024];
						int iRx = connection.Receive (buffer);
						char[] chars = new char[iRx];
		
						System.Text.Decoder d = System.Text.Encoding.UTF8.GetDecoder ();
						int charLen = d.GetChars (buffer, 0, iRx, chars, 0);
						System.String recv = new System.String (chars);
						Debug.Log (recv);
						connection.Close ();

						string[] tokens = recv.Split (' ');

						if (!startprev) {
								prevx = float.Parse (tokens [0]);
								prevy = float.Parse (tokens [1]);
								prevz = float.Parse (tokens [2]);
								x = float.Parse (tokens [0]);
								y = float.Parse (tokens [1]);
								z = float.Parse (tokens [2]);
								startprev = true;
						}

						x = float.Parse (tokens [0]);
						y = float.Parse (tokens [1]);
						z = float.Parse (tokens [2]);

						x = Mathf.Lerp (prevx, x, Time.time);
						y = Mathf.Lerp (prevy, y, Time.time);
						z = Mathf.Lerp (prevz, z, Time.time);

						prevx = x;
						prevy = y;
						prevz = z;

						Debug.Log ("Split " + float.Parse (tokens [0]) + " " + float.Parse (tokens [1]) + " " + float.Parse (tokens [2]));
						Quaternion rot = Quaternion.Euler (new Vector3 (y, z, x));
						Debug.Log ("rot " + rot);
						mobile.transform.rotation = rot;
				}
	}

	void OnGUI(){
		GUI.Box(new Rect(10,10,100,90), "Connect");
		port = GUI.TextField (new Rect (11, 30, 98, 19), port);
		// Make the first button. If it is pressed, Application.Loadlevel (1) will be executed
		if(GUI.Button(new Rect(20,75,80,20), "Connect") && !connected) {

			Debug.Log(port);
			connected=true;
		}

	}
}

