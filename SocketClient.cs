using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;


namespace SocketTest
{
    class SocketClient
    {
        
        static void Main(string[] args)
        {
            new Thread(StartServer).Start();

            Thread.Sleep(1000);

            
        }


        static void StartClient()
        {
            Console.WriteLine("[Client]");
            try
            {
                Socket socketClient = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socketClient.Connect(IPAddress.Parse("127.0.0.1"), 4444);
                Console.WriteLine(" ");
                //获取发送内容

                Receive(socketClient);

                string sendStr = Console.ReadLine();
                while (!(sendStr.ToLower() == "q"))
                {
                    Console.WriteLine("发送消息：" + sendStr);
                    //同步发送数据
                    socketClient.Send(Encoding.ASCII.GetBytes(sendStr));
                    sendStr = Console.ReadLine();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine("按任意键退出");
                Console.ReadKey();
            }
        }


        static byte[] m_buffer = new byte[1024];
        static void Receive(Socket socket)
        {
            socket.BeginReceive(m_buffer, 0, m_buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
        }


        static void ReceiveCallback(IAsyncResult result)
        {
            Socket socket = result.AsyncState as Socket;
            int len = socket.EndReceive(result);
            Console.WriteLine("收到回复：" + Encoding.ASCII.GetString(m_buffer));
            m_buffer = new byte[1024];

            Receive(socket);
        }


        static void StartServer()
        {
            new SocketServer();
        }
    }


    class SocketServer
    {
        //服务端buffer为4字节
        static byte[] buffer = new byte[4];
        public SocketServer()
        {

            Console.WriteLine("[Server]");
            try
            {
                Socket socketServer = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                socketServer.Bind(new IPEndPoint(IPAddress.Parse("127.0.0.1"), 51234));
                socketServer.Listen(int.MaxValue);
                Console.WriteLine("服务端已启动，等待连接...");
                //接收连接
                Socket ts = socketServer.Accept();
                Console.WriteLine("客户端已连接");

                //开始异步接收
                ts.BeginReceive(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), ts);
                Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        static void ReceiveCallback(IAsyncResult result)
        {
            Socket ts = (Socket)result.AsyncState;
            int len = ts.EndReceive(result);
            result.AsyncWaitHandle.Close();
            Console.WriteLine("收到消息：{0}",len );
            ts.Send(Encoding.ASCII.GetBytes("has receive;" + Encoding.ASCII.GetString(buffer)));
            //清空数据，重新开始异步接收
            buffer = new byte[buffer.Length];
            ts.BeginReceive(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), ts);
        }
    }
}