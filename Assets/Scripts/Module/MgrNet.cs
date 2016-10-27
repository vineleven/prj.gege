using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections;
using System.Collections.Generic;

using UnityEngine;
//using System.Reflection;

class MgrNet: MonoBehaviour
{
	//端口及IP  
	IPEndPoint ipe = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 51234);

	int reconncetTime = 0;
	bool connected = false;

	Socket m_socket = null;

	const int BUFFER_SIZE = 128;
	const char MSG_END_FLAG = '$';
	static byte[] m_buffer = new byte[BUFFER_SIZE];
	static StringBuilder sb = new StringBuilder ();

	void Awake()
	{
		m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
	}


	void Start(){
		StartCoroutine (Connect());
	}



	IEnumerator Connect()
	{
		Tools.Log ("begin connet.");
		if(connected){
			connected = false;
			try{
				m_socket.Shutdown (SocketShutdown.Both);
				m_socket.Close ();
			} catch(Exception e){
				Tools.LogError ("close socket:" + e.Message);
			}
		}

		yield return null;

		try
		{
			m_socket.Connect(ipe);
			connected = true;
			Receive(m_socket);
//			string sendStr = Console.ReadLine();
//			while (!(sendStr.ToLower() == "q"))
//			{
//				socketClient.Send(Encoding.ASCII.GetBytes(sendStr));
//				sendStr = Console.ReadLine();
//			}
		}
		catch (Exception e)
		{
			Tools.LogError ("Connect:"+e.Message);
		}

	}


	void Update(){
		if(!m_socket.Connected && --reconncetTime <= 0){
			reconncetTime = 150;
			StartCoroutine (Connect());
		}
	}


	static void Receive(Socket socket)
	{
		try{
//			Tools.Log("begin receive");
			socket.BeginReceive(m_buffer, 0, m_buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
		} catch (Exception e){
			Tools.LogError ("Receive:" + e.Message);
		}
	}


	static void ReceiveCallback(IAsyncResult result)
	{
		try{
			Socket socket = result.AsyncState as Socket;
			int len = socket.EndReceive(result);
//			Console.WriteLine("收到回复：" + Encoding.ASCII.GetString(m_buffer));

			for(int i = 0; i < len; i++){
				if(m_buffer[i] == MSG_END_FLAG){
					Tools.Log("receive msg suc:" + sb.ToString());
					sb = new StringBuilder();
				} else {
					sb.Append(m_buffer[i]);
				}
			}

			m_buffer = new byte[BUFFER_SIZE];
			
			Receive(socket);
		} catch(Exception e){
			Tools.LogError ("ReceiveCallback" + e.Message);
		}
	}



	static void Send(string msg){
		
	}

}
