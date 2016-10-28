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

	int m_reconncetTime = 100;
	bool m_connectting = false;

	
	const int BUFFER_SIZE = 128;
	const char MSG_END_FLAG = '$';


    static Socket m_socket = null;
	static byte[] m_buffer = new byte[BUFFER_SIZE];
	static StringBuilder sb = new StringBuilder ();


	void Awake()
	{
	}


	void Start(){
		StartCoroutine (Connect());
	}


    void OnDestroy()
    {
		Tools.Log("net destory");
        if (m_socket.Connected)
        {
			try{
				m_socket.Shutdown (SocketShutdown.Both);
				m_socket.Close ();
			} catch(Exception e){
				Tools.LogError ("close socket:" + e.Message);
			}
		}

		m_socket = null;
        m_buffer = new byte[BUFFER_SIZE];
	    sb = new StringBuilder ();
        m_reconncetTime = 100;
	}


	IEnumerator Connect()
	{
		Tools.Log ("begin connet.");
        if (m_connectting)
            yield break;

        if (m_socket != null && m_socket.Connected)
        {
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
            m_connectting = true;
            m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			m_socket.Connect(ipe);

			Tools.Log("Connect suc.");
			Receive(m_socket);
		}
		catch (Exception e)
		{
			Tools.LogError ("Connect:"+e.Message);
		}

        m_connectting = false;
	}


	void Update(){
        if (!m_connectting && m_socket!= null && !m_socket.Connected && --m_reconncetTime <= 0)
        {
            m_reconncetTime = 150;
			Tools.Log("Fail Connect");
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
                    sb.Append(Encoding.UTF8.GetString(m_buffer,i, 1));
				}
			}

			m_buffer = new byte[BUFFER_SIZE];
			
			Receive(socket);
		} catch(Exception e){
			Tools.LogError ("ReceiveCallback:" + e.Message);
		}
	}


    static void Send(Socket socket, string msg)
    {
        try
        {
            Tools.Log("Send:" + msg);
            var data = Encoding.UTF8.GetBytes(msg);
            socket.BeginSend(data, 0, data.Length, SocketFlags.None, new AsyncCallback(SendCallback), socket);
        }
        catch (Exception e)
        {
            Tools.LogError("Send:" + e.Message);
        }
	}


    static void SendCallback(IAsyncResult result)
    {
        try
        {
            Socket socket = result.AsyncState as Socket;
            int len = socket.EndSend(result);
            Tools.Log("Send len:" + len);
        }
        catch (Exception e)
        {
            Tools.LogError("SendCallback:" + e.Message);
        }
    }



    public static void Send(int cmd, Hashtable data)
    {
        if (m_socket != null && m_socket.Connected)
        {
            Hashtable sendData = new Hashtable();
            sendData.Add(Consts.MSG_KEY_CMD, cmd);
            sendData.Add(Consts.MSG_KEY_DATA, data);
            string sendMsg = Json.JsonEncode(sendData);
            Send(m_socket, sendMsg + Consts.MSG_END_FLAG);
        }
        else
            Tools.LogWarn("socket disconnect");

        
    }
}
