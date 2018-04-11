package com.wanniu.game.five2Five;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveMatchTeamVo {
	public String tempTeamId;

	/** 对阵方临时队伍ID */
	public String oppoTempTeamId;

	public Date joinTime;

	public CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMember;

	public int teamScore;

	public List<Five2FiveTeamApplyVo> teamMatchVos;

	public List<Five2FiveSingleApplyVo> singleMatchVos;

	public AtomicBoolean isAllChoice = new AtomicBoolean(false);

	public AtomicInteger useNumber = new AtomicInteger(0);
}
