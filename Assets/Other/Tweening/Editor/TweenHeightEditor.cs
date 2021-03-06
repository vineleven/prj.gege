﻿using UnityEditor;
using UnityEngine;

[CustomEditor(typeof(TweenHeight))]
public class TweenHeightEditor : UITweenerEditor
{
    public override void OnInspectorGUI()
    {
        GUILayout.Space(6f);
        EditorGUIUtility.labelWidth = 120f;
        TweenHeight tw = target as TweenHeight;
        GUI.changed = false;

        float from = EditorGUILayout.FloatField("From", tw.from);
        float to = EditorGUILayout.FloatField("To", tw.to);

        if (GUI.changed)
        {
            RegisterUndo("Tween Change", tw);
            tw.from = from;
            tw.to = to;
            EditorUtility.SetDirty(tw);
        }

        DrawCommonProperties();
    }
}