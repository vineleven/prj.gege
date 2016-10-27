package gege.game;


import gege.util.Tools;

import java.util.Vector;

public class MazeMaker {
	private static int[][] data = null;
	/**
	 * 状态
	 */
//	private static final int NONE =			0;
	
	public static final int UP = 			1 << 0;		//1
	public static final int DOWN = 			1 << 1;			//2
	public static final int LEFT = 			1 << 2;			//4
	public static final int RIGHT = 		1 << 3;		//8
	public static final int ALL = 			UP | DOWN | LEFT | RIGHT;
	
	private static final int READ = 			1 << 4;			//16
	public static final int START = 		1 << 5;			//32
	public static final int END = 			1 << 6;			//64
	/**
	 * debuff
	 */
	public static final int TunMoShou = 	1 << 7;
	
	public static final int JingXiang = 	1 << 8;
	public static final int XuKong = 		1 << 9;
	public static final int TengXu = 		1 << 10;
	public static final int JinGu = 		1 << 11;
	public static final int YeSe = 			1 << 12;
	/**
	 * 物品
	 */
	public static final int JingHua = 		1 << 13;
	public static final int SuiPian = 		1 << 14;
	
	public static final int MyBody = 1 << 15;
	
	public static final int Arrow = 1 << 16;
	
	public static final int CollObject = 1 << 17;
	

	public static final int NOT_EMPTY = TunMoShou | JingXiang | XuKong | TengXu | JinGu | YeSe | JingHua | SuiPian | START | END;
	
	public static int m_width;		//迷宫规模
	public static int m_height;
	public static int m_length;
	
	public int range = 100;
	public int posX = 0;	//视野中心 屏幕坐标
	public int posY = 0;
	

	public static int startX ;		//迷宫起始点
	public static int startY ;
	public static int endX ;
	public static int endY ;
	
	public static boolean isRange = false;
	
	private static boolean isUserData = false;	//设置为固定迷宫标记
	
	private static Points<Point> readyPoints = new Points<Point>();
	
	private static MazeMaker instance;
	
	/**
	 * 动画
	 */
	
	
	
	private MazeMaker(){}
	
	public static MazeMaker getInstance(){
		if(instance == null){
			instance = new MazeMaker();
		}
		return instance;
	}
	

	/**
	 * 设置动画 1
	 * @param id
	 */
	public void setAnim(int id){
	}
	
	/**
	 * 设置边距 3
	 * @param offset_x
	 * @param offset_y
	 */
	public void setOffsetXY(int offset_x, int offset_y){
	}
	
	/**
	 * 设置尺寸 ：迷宫规模、地图大小
	 * @param wallWidth
	 * @param wallHeight
	 */
	public void setMazeSize(int arrayWidth, int arrayHeight, int length, int mapWidth, int mapHeight){
		m_width = arrayWidth;
		m_height = arrayHeight;
		m_length = length;
	}
	
	/**
	 * 获取一个随机空格
	 * @return
	 */
	public Point getRandPoint(boolean isArrayLocation){
		int _data = 0;
		int _x,_y;
		do{
			_x = Tools.getRandomInt(0, m_width - 1);
			_y = Tools.getRandomInt(0, m_height - 1);
			_data = data[_x][_y] & NOT_EMPTY;
		}while(_data != 0);
		
		if(!isArrayLocation){
//			_x = getWorldX(_x);
//			_y = getWorldY(_y);
		}
		return new Point(_x, _y);
	}
	/**
	 * 距离迷宫起点距离
	 * @param endPoint
	 * @param distance
	 * @return
	 */
	public boolean inDistance(int x, int y,int distance){
		return Tools.Math_NormPow(startX - x, startY - y) < (distance * distance);
	}
	
	
	/**
	 * 设置视野中心
	 * @param x 世界坐标
	 * @param y
	 */
	public void setCenter(float x, float y){
		this.posX = (int) x;
		this.posY = (int) y;
	}
	
	
	/**
	 * 创建迷宫数组，之前一定要set 1-5 的 数据
	 * @param width
	 * @param height
	 * @param length
	 * @return
	 */
	private boolean buildMaze(){
		return setPath(setStart());
	}
	
	public void createMaze(){
		if(!isUserData){
			init();
			if( !buildMaze()){
				return;
			}
		} else {
			readyPoints.removeAll();
		}
		try{
			throw new Exception("my exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		bodyManager.setData(data);
	}
	
	public void createMaze(int[][] userData){
		isUserData = true;
		data = userData;
		createMaze();
		isUserData = false;
	}
	
	private void init(){
		data = null;
		data = new int[m_width][m_height];
		findEnd = false; 
		readyPoints.removeAll();
	}
	/**
	 * 设置随机起点
	 */
	private Point setStart(){
		Point startPoint = getRandPoint(true);
		data[startPoint.x][startPoint.y] |= START;
		return startPoint;
	}
	
	public void setStart(int arrayX, int arrayY){
		data[arrayX][arrayY] |= START;
	}
	
	/**
	 * 设置终点
	 */
	public void setEnd(int arrayX, int arrayY){
		data[arrayX][arrayY] |= END;
		endX = arrayX;
		endY = arrayY;
	}
	private boolean findEnd = false;
	/**
	 * 设置路径
	 */
	private boolean setPath(Point startPoint){
		int i = 0;
		while(i < m_length){								//设定起点到终点的路径
			startPoint = nextPoint(startPoint.x, startPoint.y);
			i++;
			if(startPoint == null){
				createMaze();
				return false;
			}
		}
		findEnd = true;
		setEnd(startPoint.x, startPoint.y);
		startPoint = readyPoints.get();
		while(startPoint != null){								//填充迷宫
			startPoint = nextPoint(startPoint.x, startPoint.y);
		}
		return true;
	}
	
	private static boolean checkPoint = false;

	/**
	 * 从当前点开始搜索
	 * @param x
	 * @param y
	 * @return 连接当前点的下一个有效点
	 */
	private Point nextPoint(int x, int y){
		if(x < 0 || x > m_width - 1 || y < 0 || y > m_height - 1)
			try {
				throw new Exception("---坐标超出范围");
			} catch (Exception e) {
				e.printStackTrace();
			}
		int _data = data[x][y];
		int _dir = 0;
			if((x + 1) < m_width)
				if(checkPonit(data[x + 1][y])){
					_dir |= RIGHT;
					checkPoint = true;
				}
			
			if((x - 1) >= 0)
				if(checkPonit(data[x - 1][y])){
					_dir |= LEFT;
					checkPoint = true;
				}
			
			if((y + 1) < m_height)
				if(checkPonit(data[x][y + 1])){
					_dir |= DOWN;
					checkPoint = true;
				}
			
			if((y - 1) >= 0)
				if(checkPonit(data[x][y - 1])){
					_dir |= UP;
					checkPoint = true;
				}
		
		if(_dir == 0){
			if(!findEnd){
				return null;
			}
			if(!checkPoint){
				readyPoints.del();
			} else {
				data[x][y] |= READ;
			}
			checkPoint = false;
			Point _point = readyPoints.get();
			if(_point != null){
				return nextPoint(_point.x, _point.y);
			}
		} else {
			int _temp = 0;
			if((_data & READ) != READ){
				readyPoints.add(new Point(x, y));
				data[x][y] |= READ;
			}
			do {
				_temp = 1 << Tools.getRandomInt(0, 4);
			} while ((_temp & _dir) == 0);
			switch (_temp) {
			case UP:
				data[x][y] |= UP;
				y--;
				data[x][y] |= DOWN;
				break;
			case DOWN:
				data[x][y] |= DOWN;
				y++;
				data[x][y] |= UP;
				break;
			case LEFT:
				data[x][y] |= LEFT;
				x--;
				data[x][y] |= RIGHT;
				break;
			case RIGHT:
				data[x][y] |= RIGHT;
				x++;
				data[x][y] |= LEFT;
				break;
			default:
				System.err.println("有问题 - nextPoint");
			}
			return new Point(x, y);
		}
		return null;
	}
	
	private boolean checkPonit(int vdata){
		if((vdata & READ) == 0)
			if((vdata & END) == 0)
				if((vdata & START) == 0)
			return true;
		return false;
	}
	
	
	public void unLoad(){
		data = null;
		instance = null;
	}

}

class Points<E> extends Vector<E>{
	private static final long serialVersionUID = 1L;
	public E get(){
		if(elementCount == 0)
			return null;
		return firstElement();
	}
	public void set(E e){
		add(e);
	}
	
	public void del(){
		if(elementCount != 0)
			remove(0);
	}
	
	public void removeAll(){
		removeAllElements();
	}
}

class Point{
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	public int x = -1;
	public int y = -1;
}