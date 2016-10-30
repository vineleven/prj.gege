using UnityEngine;
using UnityEditor;
using NUnit.Framework;
using System.IO;
using System.Collections;
using System.Collections.Generic;


public class Packager {

    /*
     
         合图代码，我们将SpritePacker中的tag信息设置好，我们这里需要避开直接设置assetBundleName，所以我们采用黑科技，将assetBundleName放入userdata中。
         在build时统一处理设置assetBundleName，userData统一采用JSON格式，方便扩展

        exp: userdata = {"assetBundleName":"share"}
         
         */
    public static readonly string SpritePackJsonKey = "assetBundleName";
    [MenuItem("Assets/Pack Textures By SpritePacker", false, 1003)]
    public static void SpritePackTexture()
    {

        string path = GetSelectionPath();
        string directoryName = Path.GetFileName(path).ToLower();
        //string splitName = "_";

        AssetImporter importer = AssetImporter.GetAtPath(path);
        Hashtable jsonHashTable = null;

        if (importer.userData != "")
        {
            //获取userdata中的json
            jsonHashTable = Json.DecodeMap(importer.userData);
            if (jsonHashTable != null && jsonHashTable.ContainsKey(SpritePackJsonKey))
            {
                jsonHashTable.Remove(SpritePackJsonKey);
            }
        }
        if (jsonHashTable == null)
        {
            jsonHashTable = new Hashtable();
        }

        jsonHashTable.Add(SpritePackJsonKey, directoryName);

        importer.userData = Json.JsonEncode(jsonHashTable);
        importer.SaveAndReimport();

        if (!string.IsNullOrEmpty(path))
        {
            string[] files = Directory.GetFiles(path, "*.png", SearchOption.AllDirectories);

            int startIndex = 0;
            //替换路径中的反斜杠为正斜杠       
            //string strTempPath = path.Replace(@"\", "/");

            EditorApplication.update = delegate()
            {
                string file = files[startIndex];

                bool isCancel = EditorUtility.DisplayCancelableProgressBar("合图中..", file, (float)startIndex / (float)files.Length);

                TextureImporter texImp = AssetImporter.GetAtPath(file) as TextureImporter;

                //设置assetbundlename
                //BuildAssetBundleName(file, strTempPath.Replace("/", splitName).Replace(".", splitName));
                //BuildAssetBundleName(file, directoryName);

                //设置tag
                texImp.textureType = TextureImporterType.Sprite;
                texImp.mipmapEnabled = false;
                texImp.spritePackingTag = directoryName;
                texImp.SaveAndReimport();

                //if (Regex.IsMatch(File.ReadAllText(file), guid))
                //{
                //    Debug.Log(file, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(file)));
                //}

                startIndex++;
                if (isCancel || startIndex >= files.Length)
                {
                    EditorUtility.ClearProgressBar();
                    EditorApplication.update = null;
                    startIndex = 0;
                    //Debug.Log("匹配结束");
                }
            };
        }
    }


    public static string GetSelectionPath()
    {
        string path = null;

        Object[] selections = Selection.GetFiltered(typeof(Object), SelectionMode.Assets);
        if (selections != null && selections.Length > 0)
            path = AssetDatabase.GetAssetPath(selections[0]);

        return path;


    }
}
