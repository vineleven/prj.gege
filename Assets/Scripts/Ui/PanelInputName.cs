using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.Events;
using System.Text;


public class PanelInputName : PanelBase
{

    public override int getLayer()
    {
        return MgrPanel.LAYER_UI;
    }


    public override int getStyle()
    {
        return MgrPanel.STYLE_FULL;
    }


    public override string getResName()
    {
        return "PanelInputName";
    }



    Text m_inputText;
    public override void onBuild(Hashtable param)
    {
        m_inputText = gameObject.transform.FindChild("BG/InputField/Text").GetComponent<Text>();

        var btnYes = gameObject.transform.FindChild("BG/BtnYes");
        btnYes.GetComponent<Button>().onClick.AddListener(onClickYes);
    }


    public void onClickYes()
    {
        string text = m_inputText.text;

        if (!string.IsNullOrEmpty(text))
        {
            if (MgrSocket.connected())
            {
                close();
                MgrNet.reqSetName(text);
            }
            else
            {
                MgrPanel.openDialog("can't connect to server, please ask LDW for help.");
            }
        }
        else
        {
            MgrPanel.openDialog("this is an invalid name");
        }
        
    }





    



}
