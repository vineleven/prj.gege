using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using Global;

using UnityEngine;
//using System.Reflection;

class MgrSocket : MgrBase
{
	//端口及IP  
    //static IPEndPoint ipe = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 51234);
    static IPEndPoint ipe = new IPEndPoint(IPAddress.Parse("192.168.1.124"), 51234);


    // 重新连接周期
	int m_reconncetTime = 100;

    // 是否正在连接
	bool m_connectting = false;

	
	const char MSG_END_FLAG = '$';


    static Socket m_socket = null;
    static ByteBuffer m_buffer = new ByteBuffer(1024);
	static byte[] m_transfer_buffer = new byte[128];


    public static void setServerHost(string ip, int port)
    {
        ipe = new IPEndPoint(IPAddress.Parse(ip), port);
    }


	void Awake()
	{
	}


    public override void onDestory()
    {
        Tools.Log("net destory");
        if (m_socket.Connected)
        {
            try
            {
                m_socket.Shutdown(SocketShutdown.Both);
                m_socket.Close();
            }
            catch (Exception e)
            {
                Tools.LogError("close socket:" + e.Message);
            }
        }

        m_socket = null;
        m_buffer.reset();
        m_reconncetTime = 100;
    }


	void Start(){
        //StartCoroutine(getServerInfo());
        StartCoroutine(Connect());
	}


    IEnumerator getServerInfo()
    {
        string url = "http://h005.ultralisk.cn:4022/u3d_patch/gege/server_info.json";
        WWW www = new WWW(url);
        yield return www;

        if (string.IsNullOrEmpty(www.error))
        {
            string json = www.text;
            try
            {
                Hashtable data = Json.DecodeMap(json);
                string ip = data["ip"] as string;
                int port = Convert.ToInt32(data["port"]);
                setServerHost(ip, port);
                StartCoroutine(Connect());
            }
            catch(Exception e)
            {
                Tools.LogError("getServerInfo:" + e.Message);
            }
        }
        else
        {
            yield return new WaitForSeconds(2);
            Tools.LogWarn("re try get server info.");
            StartCoroutine(getServerInfo());
        }
    }


	IEnumerator Connect()
	{
		Tools.Log ("begin connet.");
        if (m_connectting)
            yield break;

        if (connected())
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

            EventDispatcher.getGlobalInstance().dispatchUiEvent(EventId.MSG_CONNECTED);
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
            StartCoroutine(Connect());
		}
	}


	static void Receive(Socket socket)
	{
		try{
//			Tools.Log("begin receive");
			socket.BeginReceive(m_transfer_buffer, 0, m_transfer_buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
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
				if(m_transfer_buffer[i] == MSG_END_FLAG){
                    string receive = m_buffer.toUTF8String();
                    m_buffer.reset();
                    
                    Tools.Log("receive msg suc:" + receive);

                    var rsp = Json.DecodeMap(receive);
                    EventDispatcher.getGlobalInstance().dispatchEvent(EventId.GLOBAL_RESPONSE, rsp);
				} else {
                    m_buffer.append(m_transfer_buffer, i, 1);
				}
			}

			Receive(socket);
		} catch(Exception e){
            Tools.LogError("ReceiveCallback:" + e.Message);
            Tools.LogError("cur msg:" + m_buffer.ToString());
            Tools.LogError(e.StackTrace);
		}
	}


    static void SendMsg(Socket socket, string msg)
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
            //Tools.Log("Send len:" + len);
        }
        catch (Exception e)
        {
            Tools.LogError("SendCallback:" + e.Message);
        }
    }



    public static void Send(int cmd, Hashtable data = null)
    {
        if (connected())
        {
            if (data == null)
                data = new Hashtable();

            Hashtable sendData = new Hashtable();
            sendData.Add(Consts.MSG_KEY_CMD, cmd);
            sendData.Add(Consts.MSG_KEY_DATA, data);
            string sendMsg = Json.JsonEncode(sendData);
            SendMsg(m_socket, sendMsg + Consts.MSG_END_FLAG);
        }
        else
        {
            Tools.LogWarn("socket disconnect");
        }
    }


    public static bool connected()
    {
        if (m_socket != null && m_socket.Connected)
        {
            return true;
        }

        return false;
    }
}
