using System.Collections;
using System;
using System.Text;


public class ByteBuffer
{

    private byte[] m_buffer;

    private int m_size = 0;



    public ByteBuffer(int maxLen)
    {
        m_buffer = new byte[maxLen];
    }


    public void append(byte b)
    {
        if (m_buffer.Length < m_size)
        {
            //Tools.LogError("max:" + m_buffer.Length);
            throw new IndexOutOfRangeException("ByteBuffer max size:" + m_buffer.Length);
        }

        m_buffer[m_size++] = b;
    }


    public void append(byte[] bs)
    {
        for (int i = 0; i < bs.Length; i++)
        {
            append(bs[i]);
        }
    }


    public void append(byte[] bs, int offset, int len)
    {
        int end = offset + len;

        if (end > bs.Length)
            end = bs.Length;

        for (int i = offset; i < end; i++)
        {
            append(bs[i]);
        }
    }


    public void reset()
    {
        m_size = 0;
    }


    public string toUTF8String()
    {
        byte[] sour = new byte[m_size];
        Array.Copy(m_buffer, sour, m_size);
        return Encoding.UTF8.GetString(sour);
    }


    public override string ToString()
    {
        return toUTF8String();
    }



}
