using UnityEngine;
using System.Collections;

public class Map {



	int[][] tileData;


	public Map() {
		// random a map
		int row = 30;
		int col = 30;
		tileData = new int[row][];
		for(int i=0; i<row; i++){
			int[] cols = new int[30];
			for(int j=0; j<col; j++){
				int r = Tools.Random (1, 10);
				r = r > 8 ? 2 : 1;
				cols [j] = r;
			}
			tileData [i] = cols;
		}
	}


	public void CreateMap(Transform ground){
		for(int i = 0; i < tileData.Length; i++){
			int[] cols = tileData [i];
			for(int j = 0; j < cols.Length; j++){
				GameObject go = Resources.Load<GameObject> ("Prefab/Tile" + cols[j]);
				go = GameObject.Instantiate (go);
				go.transform.SetParent (ground);
				go.transform.position = new Vector3 (j, 0, i);
			}
		}
	}

	
}
