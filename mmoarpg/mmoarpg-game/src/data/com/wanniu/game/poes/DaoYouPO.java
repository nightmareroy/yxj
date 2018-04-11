package com.wanniu.game.poes;

import java.util.Date;
import java.util.List;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.daoyou.DaoYouMessageVo;

/**
 * 道友实体类
 * 
 * @author wanghaitao
 *
 */
public class DaoYouPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;

	/** 管理员ID */
	public String adminPlayerId;

	/** 道友名称 */
	public String name;

	/** 战力 */
	public int fightPower;

	/** 公告 */
	public String notice;

	/** 是否修改过道友名称 */
	public int isEditedDyName;

	/** 上次改名时间 */
	public Date lastEditNameTime;

	/** 系统和留言消息 */
	public List<DaoYouMessageVo> messages;

	/** 留言消息 */

	/** 创建时间 */
	public Date createTime;

	/** 更新时间 */
	public Date updateTime;
	
	public DaoYouPO() {
		
	}
}
