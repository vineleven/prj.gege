package gege.consts;

public interface CODE {
	enum MSG {
		 NONE
		,SUCCESS	// 成功	1
		,FAIL		// 失败
		,INVALID	// 非法
		,DUPLICATE	// 重复
		,EMPTY		// 空	5
		,NOT_ENOUGH_MONEY // 金币不足
		,NOT_ENOUGH_RES	//资源不足		7
		,FULL		// 满
		;
		
		public int getId(){
			return ordinal();
		}
	}
	
	
	enum UP {
		INVALID,				// 无效
		SUCCESS,				// 升级成功
		LACK_REQ,				// 需求不满足
		LACK_DEP,				// 依赖不满足
		IN_CDTIME,				// cd中
		IN_PROTIME,				// 正在升级
		MAXLV,					// 已经到最大等级
		FAIL,					// 未知错误
		DUPLICATE;				// 重复

		public int getId(){
			return ordinal();
		}
	}
}
