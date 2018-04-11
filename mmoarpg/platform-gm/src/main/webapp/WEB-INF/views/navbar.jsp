<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<aside class="main-sidebar">
	<section class="sidebar">
		<ul class="sidebar-menu">
			<li class="header">主菜单</li>
			
			<!-- 数据仓库 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_WAREHOUSE']}">
				<li class="treeview"><a href="#"><i class="fa fa-line-chart"></i> <span>数据仓库</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_DAILY']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/daily/manage/"><i class="fa fa-circle-o"></i> 每日数据报表</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_ONLINE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/online/manage/"><i class="fa fa-circle-o"></i> 同时在线曲线</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_LEVEL']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/level/manage/"><i class="fa fa-circle-o"></i> 玩家等级分布</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_RECHARGE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/recharge/manage/"><i class="fa fa-circle-o"></i> 充值大盘走势</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_RECHARGE_SECTION']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/recharge/section/manage/"><i class="fa fa-circle-o"></i> 充值区间分布</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_PRODUCE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/produce/manage/"><i class="fa fa-circle-o"></i> 货币产出分布</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_CONSUME']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/consume/manage/"><i class="fa fa-circle-o"></i> 货币消耗分布</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_SHOP_DISTRIBUTION']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/shop/manage/"><i class="fa fa-circle-o"></i> 商城销售额分布</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['DATA_REMAIN']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/data/remain/manage/"><i class="fa fa-circle-o"></i> 留存率</a></li>
						</c:if>
					</ul>
				</li>
			</c:if>
			
			<!-- 游戏区运营管理功能 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['GAME_MANAGER']}">
				<li class="treeview active"><a href="#"><i class="fa fa-gamepad"></i> <span>游戏管理</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_RECHARGE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/query/recharge/manage/"><i class="fa fa-circle-o"></i> 充值查询</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_PLAYER']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/query/player/manage/"><i class="fa fa-circle-o"></i> 角色查询</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_GUILD']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/query/guild/manage/"><i class="fa fa-circle-o"></i> 仙盟查询</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_DAOYOU']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/query/daoyou/manage/"><i class="fa fa-circle-o"></i> 道友查询</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_RANK']}">
							<!-- <li><a class="ajax" href="${pageContext.request.contextPath}/game/query/rank/manage/"><i class="fa fa-circle-o"></i> 排行查询</a></li> -->
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGER_LOGIN_NOTICE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/login/notice/manage/"><i class="fa fa-circle-o"></i> 登录公告</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGER_ROLL_NOTICE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/roll/notice/manage/"><i class="fa fa-circle-o"></i> 滚动公告</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGER_GAME_NOTICE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/notice/list/"><i class="fa fa-circle-o"></i> 游戏内公告</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_RECHARGE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/recharge/manage/"><i class="fa fa-circle-o"></i> 充值补单</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_PUBLISH']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/game/publish/manage/"><i class="fa fa-circle-o"></i> 处罚管理</a></li>
						</c:if>
						
						<!-- 福利管理 -->
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPLY'] || sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPROVAL'] || sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_BATCH']}">
							<li><a href="#"><i class="fa fa-navicon"></i> 福利管理<span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
								<ul class="treeview-menu menu-open">
									<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPLY']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/game/mail/manage/"><i class="fa fa-circle-o"></i> 福利申请</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPROVAL']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/game/mail/approval/manage/"><i class="fa fa-circle-o"></i> 福利审批</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_BATCH']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/game/mail/batch/apply/"><i class="fa fa-circle-o"></i> 批量补偿</a></li>
									</c:if>
									<!-- 模拟充值 -->					
									<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_FICTITIOUS']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/game/fictitious/manage/"><i class="fa fa-circle-o"></i> 模拟充值</a></li>
									</c:if>
								</ul>
							</li>
						</c:if>
						
						<!-- CDKEY -->
						<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_CDKEY_QUERY']}">
							<li><a href="#"><i class="fa fa-navicon"></i> CDKEY管理<span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
								<ul class="treeview-menu menu-open">
									<li><a class="ajax" href="${pageContext.request.contextPath}/game/cdkey/manage/"><i class="fa fa-circle-o"></i> 礼包列表</a></li>
									<li><a class="ajax" href="${pageContext.request.contextPath}/game/cdkey/query/ui/"><i class="fa fa-circle-o"></i> CDKEY查询</a></li>
								</ul>
							</li>
						</c:if>
					</ul>
				</li>
			</c:if>
	
			<!-- 日志查询功能 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['LOG_QUERY']}">
				<li class="treeview"><a href="#"><i class="fa fa-search"></i> <span>日志查询</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_RECHARGE_RANK']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/rechargerank/manage/"><i class="fa fa-circle-o"></i> 查询充值排行</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_LEVELUP']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/level/manage/"><i class="fa fa-circle-o"></i> 查询玩家升级</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_MONEY']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/money/manage/"><i class="fa fa-circle-o"></i> 查询货币日志</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_ITEMS']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/item/manage/"><i class="fa fa-circle-o"></i> 查询道具日志</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_PET']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/pet/manage/"><i class="fa fa-circle-o"></i> 查询宠物升级</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['QUERY_MOUNT']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/query/mount/manage/"><i class="fa fa-circle-o"></i> 查询坐骑升级</a></li>
						</c:if>
					</ul>
				</li>
			</c:if>
			
			<!-- 监控相关的功能 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['MONITOR']}">
				<li class="treeview"><a href="#"> <i class="fa fa-eye"></i> <span>监控系统</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['MONITOR_CHAT']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/chat/index/"><i class="fa fa-circle-o"></i> 聊天监控</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MONITOR_PAY']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/charge/index/"><i class="fa fa-circle-o"></i> 充值监控</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MONITOR_MONEY']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/money/manage/"><i class="fa fa-circle-o"></i> 货币监控</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MONITOR_ITEM']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/item/manage/"><i class="fa fa-circle-o"></i> 道具监控</a></li>
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/item/manage/"><i class="fa fa-circle-o"></i> 大R监控</a></li>
							<li><a class="ajax" href="${pageContext.request.contextPath}/monitor/item/manage/"><i class="fa fa-circle-o"></i> 小R监控</a></li>
						</c:if>
					</ul>
				</li>
			</c:if>
			
			<!-- 运维相关功能 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['MAINTAIN_MANAGER']}">
				<li class="treeview"><a href="#"> <i class="fa fa-briefcase"></i> <span>运维工具</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['SERVER_MANAGE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/server/manage/"><i class="fa fa-circle-o"></i> 区服管理</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['MLOG_EXPORT']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/log/list/"><i class="fa fa-circle-o"></i> 日志提取</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['GROOVY_EXEC']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/script/index/"><i class="fa fa-circle-o"></i> 执行脚本</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['JAVA_HOTFIX']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/hotfix/index/"><i class="fa fa-circle-o"></i> 代码热更新</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['WHITELIST_MANAGE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/whitelist/manage/"><i class="fa fa-circle-o"></i> 登录白名单</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['CHATCONFIG_EDIT']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/mchat/list/"><i class="fa fa-circle-o"></i> 反广告配置</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['EMAIL_CONFIG']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/email/manage/"><i class="fa fa-circle-o"></i> 监控邮箱配置</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['REBATE']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/rebate/manage/"><i class="fa fa-circle-o"></i> 上传充值得利</a></li>
						</c:if>
						<!-- 自智运维 -->
						<c:if test="${sessionScope.SESSION_USER.auth.have['STOP_READY'] || sessionScope.SESSION_USER.auth.have['GAME_CONFIG'] || sessionScope.SESSION_USER.auth.have['ONLINE_UPDATE']}">
							<li><a href="#"><i class="fa fa-navicon"></i> 智能运维平台<span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
								<ul class="treeview-menu menu-open">
									<c:if test="${sessionScope.SESSION_USER.auth.have['GAME_CONFIG']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/serverconfig/manage/"><i class="fa fa-circle-o"></i> 区服配置中心</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['STOP_READY']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/ready/manage/"><i class="fa fa-circle-o"></i> 停机维护准备</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['STOP_KICK']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/kick/manage/"><i class="fa fa-circle-o"></i> 停机维护T人</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['ONLINE_UPDATE']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/onlineupdate/manage/"><i class="fa fa-circle-o"></i> 在线更新版本</a></li>
									</c:if>
									<c:if test="${sessionScope.SESSION_USER.auth.have['ONLINE_UPDATE']}">
										<li><a class="ajax" href="${pageContext.request.contextPath}/maintain/combined/manage/"><i class="fa fa-circle-o"></i> 在线合服操作</a></li>
									</c:if>
								</ul>
							</li>
						</c:if>
					</ul>
				</li>
			</c:if>

			<!-- 后台管理 -->
			<c:if test="${sessionScope.SESSION_USER.auth.have['BACKSTAGE_MANAGER']}">
				<li class="treeview"><a href="#"><i class="fa fa-users"></i><span> 后台管理</span> <span class="pull-right-container"> <i class="fa fa-angle-left pull-right"></i></span></a>
					<ul class="treeview-menu">
						<c:if test="${sessionScope.SESSION_USER.auth.have['LOGINPLATFORM_LIST']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/loginplatform/manage/"><i class="fa fa-circle-o"></i> 登录平台</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['USER_LIST']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/user/manage/"><i class="fa fa-circle-o"></i> 账号管理</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['AUTH_GROUP_LIST']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/auth/manage/"><i class="fa fa-circle-o"></i> 权限管理</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['ACCESS_LIST']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/access/manage/"><i class="fa fa-circle-o"></i> 访问限制</a></li>
						</c:if>
						<c:if test="${sessionScope.SESSION_USER.auth.have['LOGGER_LIST']}">
							<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/log/manage/"><i class="fa fa-circle-o"></i> 操作日志</a></li>
						</c:if>
					</ul>
				</li>
			</c:if>

			<!-- 个人信息 -->
			<li class="header">个人信息</li>
			<c:if test="${sessionScope.SESSION_USER.auth.have['MODIFY_PASSWORD']}">
				<li><a class="ajax" href="${pageContext.request.contextPath}/backstage/user/password/modifyUI/"><i class="fa fa-circle-o text-red"></i> <span>修改密码</span></a></li>
			</c:if>
			<li><a href="${pageContext.request.contextPath}/lockscreen?username=${sessionScope.SESSION_USER.username}"><i class="fa fa-circle-o text-yellow"></i> <span>一键锁定</span></a></li>
			<li><a href="${pageContext.request.contextPath}/logout"><i class="fa fa-circle-o text-aqua"></i> <span>安全退出</span></a></li>
		</ul>
	</section>
</aside>