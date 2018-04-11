package com.wanniu.game.five2Five;

import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveMatchTeamThread implements Runnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Five2FiveMatchPool.doMatchJob();
			JobFactory.addDelayJob(this, Const.Five2Five.five2five_thread_delay_time.value, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Out.error(e);
		}
	}

}
