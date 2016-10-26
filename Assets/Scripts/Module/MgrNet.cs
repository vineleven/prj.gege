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
	byte[] result = new byte[1024];
	//端口及IP  
	IPEndPoint ipe = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 51234);  

	Socket m_socket = null;


	void Awake()
	{
		//通过clientSocket接收数据
//		int receiveLength = m_socket.Receive(result);
//		Console.WriteLine("接收服务器消息：{0}", Encoding.ASCII.GetString(result,0,receiveLength));  
//		//通过 clientSocket 发送数据
//		for (int i = 0; i < 10; i++)
//		{
//			try
//			{
//				Thread.Sleep(1000);    //等待1秒钟
//				string sendMessage = "client send Message Hellp" + DateTime.Now;
//				m_socket.Send(Encoding.ASCII.GetBytes(sendMessage));
//				Console.WriteLine("向服务器发送消息：{0}" + sendMessage);
//			}
//			catch
//			{
//				m_socket.Shutdown(SocketShutdown.Both);
//				m_socket.Close();
//				break;
//			}
//		}
	}














	/// <summary>  
	/// 连接到服务器  
	/// </summary>  
	IEnumerator AsynConnect()
	{
		if(m_socket != null){
			try{
				m_socket.Shutdown(SocketShutdown.Both);
				m_socket.Close();
			} catch(Exception e) {
				Tools.LogError("socket close fail:" + e.ToString());
			}

			m_socket = null;
		}

		yield return null;

		//创建套接字  
		m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		//开始连接到服务器
		try{
			m_socket.BeginConnect(ipe, asyncResult =>
				{
					Socket _client = (Socket)asyncResult.AsyncState;
					_client.EndConnect(asyncResult);
					//向服务器发送消息
//					AsynSend(_client,"你好我是客户端");
					//接受消息
					AsynReceive(_client);
				}, null);
			
			yield break;
		} catch(Exception e){
			Tools.LogError("create socket fail:" + e.ToString());
		}

		yield return null;

		Tools.Log("start reconnect.");
		StartCoroutine(AsynConnect());
	}


	/// <summary>
	/// 发送消息
	/// </summary>
	public static void AsynSend(Socket socket, string message)
	{  
		if (socket == null || message == string.Empty) return;

		byte[] msgBytes = Encoding.UTF8.GetBytes(message);
		byte[] sendBytes = new byte[msgBytes.Length + 2];
		Array.Copy(msgBytes, 0, sendBytes, 2, msgBytes.Length);
		try
		{
			socket.BeginSend(sendBytes, 0, sendBytes.Length, SocketFlags.None, asyncResult =>
				{
					//完成发送消息
					int length = socket.EndSend(asyncResult);
					Tools.Log(string.Format("send:{0}", message));
				}, null);
		}
		catch (Exception ex)
		{
			Tools.LogError("send fail:{0}" + ex.Message);
		}
	}


	/// <summary>
	/// 接收消息
	/// </summary>
	public static void AsynReceive(Socket socket, int len = -1)
	{
		try
		{
			bool bHead = false;
			// 默认为消息头长度
			if(len == -1){
				len = 2;
				bHead = true;
			} else if(len <= 0){
				Tools.LogError("msg len invalid:" + len);
				return;
			}

			byte[] data = new byte[len];

			//开始接收数据
			socket.BeginReceive(data, 0, data.Length, SocketFlags.None,
				asyncResult =>
				{
					int length = socket.EndReceive(asyncResult);
					if(length != len)
						Tools.LogError("receive len " + length + " != need len" + len );
					
					Tools.Log(string.Format("receive:{0}", Encoding.UTF8.GetString(data)));
					if(bHead){
						AsynReceive(socket);
					} else {
						
					}
				}, null);
		}
		catch (Exception ex)
		{
			Tools.LogError("receive fail:" + ex.Message);
		}
	}  
}
