/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.backstage.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.auth.AuthCacheManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.backstage.UserConstant;
import cn.qeng.gm.module.backstage.domain.LoginPlatform;
import cn.qeng.gm.module.backstage.domain.User;
import cn.qeng.gm.module.backstage.domain.UserRepository;
import cn.qeng.gm.module.backstage.service.result.UserLoginResult;
import cn.qeng.gm.util.IpUtils;
import cn.qeng.gm.util.Md5Utils;

/**
 * 用户逻辑处理类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class UserService {
	private final static Logger logger = LogManager.getLogger(UserService.class);
	// 创建初始账号默认的密码
	public static final String DEFAULT_INIT_PASSWORD = "qeng.123";
	// 用于编辑账号时默认的系统密码
	public static final String DEFAULT_SHOW_PASSWORD = "******************";
	@Autowired
	private AuthService authService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordService passwordService;
	@Autowired
	private AuthCacheManager authCacheManager;
	@Autowired
	private LoginPlatformService loginPlatformService;
	@Autowired
	private AccessRestrictionService accessRestrictionService;

	// 初始化默认的管理员账号
	@PostConstruct
	void initDefaultAdminUser() {
		// 先判断一下库里面是否存在账号
		// 如果没有我们就创建一个初始账号
		if (userRepository.count() <= 0) {
			// 关联权限组和登录平台的两个属性，就是两个defaultAuthId，defaultLoginPlatformId
			this.initDefaultUser(authService.initDefaultAdminAuth(), loginPlatformService.initDefaultLoginPlatform());
		}
	}

	// 开始创建
	void initDefaultUser(int defaultAuthId, String defaultLoginPlatformId) {
		User user = new User();
		user.setName("运营大爷");
		user.setUsername("admin");
		user.setAuthGroupId(defaultAuthId);
		user.setLoginPlatformId(defaultLoginPlatformId);
		// MD5加密的方式
		user.setPassword(passwordService.encrypt(Md5Utils.encrypt(DEFAULT_INIT_PASSWORD), user.getUsername()));
		user.setCreateTime(new Date());
		user.setModifyTime(user.getCreateTime());
		// 保存一下
		userRepository.save(user);
		logger.info("初始创建用户名和登录后台的密码.user={} password={}", user.getUsername(), DEFAULT_INIT_PASSWORD);
	}

	/**
	 * 后台查看管理员的人数和相关信息
	 */
	public Page<User> getUsers(int page) {
		return userRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE));
	}

	/**
	 * 获取全部账号信息
	 */
	public List<User> getUser() {
		return userRepository.findAll();
	}

	/**
	 * 查找一个账号
	 */
	public User getUser(int id) {
		return userRepository.findOne(id);
	}

	/**
	 * 添加一个用户
	 */
	public int add(String username, String password, String name, int authId, String loginplatformId) {
		User user = new User();
		// 判断一下这个账号是否已经被注册
		// 如果新添加的账号没有被注册，那么就开始注册哈
		if (userRepository.findFirstByUsername(username) == null) {
			user.setName(name);
			user.setUsername(username);
			user.setPassword(passwordService.encrypt(Md5Utils.encrypt(password), user.getUsername()));
			user.setCreateTime(new Date());
			user.setModifyTime(user.getCreateTime());
			user.setAuthGroupId(authId);
			user.setLoginPlatformId(loginplatformId);
			user.setStatus(UserConstant.STATUS_NORMAL);
			// 保存一下
			userRepository.save(user);
			// 注册成功code
			return UserConstant.USER_ADD_SUCCESS;
		}
		// 如果已经被注册，那么就不给他注册，并温馨提示一下，返回重新注册code
		return UserConstant.USER_ADD_FAIL;
	}

	/**
	 * 编辑用户账号
	 */
	public void edit(int id, String username, String password, String name, int authId, String loginplatformId) {
		User user = this.getUser(id);
		// 判断一下账号是否存在
		// 如果不存在则就创建
		if (user == null) {
			this.add(username, password, name, authId, loginplatformId);
			// 存在就修改吧
		} else {
			user.setUsername(username);
			user.setName(name);
			if (!DEFAULT_SHOW_PASSWORD.equals(password)) {
				user.setPassword(passwordService.encrypt(Md5Utils.encrypt(password), user.getUsername()));
			}
			user.setModifyTime(new Date());
			user.setAuthGroupId(authId);
			user.setLoginPlatformId(loginplatformId);
			// 保存一下
			userRepository.save(user);
		}
	}

	/**
	 * 删除账号名称
	 */
	public User findName(int id) {
		return userRepository.findUsernameById(id);
	}

	/**
	 * 删除账号
	 */
	public void delete(int id) {
		userRepository.delete(id);
	}

	/**
	 * 效验登录用户
	 */
	public UserLoginResult checkLogin(HttpServletRequest request, String username, String password) {
		// 获得用户的数据
		User user = userRepository.findFirstByUsername(username);
		// 如果账号不存在
		if (user == null) {
			return new UserLoginResult(ErrorCode.ACCOUNT_DOES_NOT_EXIST);
		}

		// 锁定状态，那就不要用了
		if (user.getStatus() == UserConstant.STATUS_LOCK) {
			return new UserLoginResult(ErrorCode.ACCOUNT_STATUS_LOCK);
		}

		// 连错10次，锁定10分钟...
		if (user.getErrorCount() >= UserConstant.MAX_LOGIN_ERROR_COUNT && user.getLoginTime() != null) {
			if (System.currentTimeMillis() < user.getLoginTime().getTime() + 5L * 60 * 1000) {
				return new UserLoginResult(ErrorCode.ACCOUNT_STATUS_EXCEPTION);
			}
		}

		// 白名单
		if (!accessRestrictionService.checkLoginIP(user.getLoginPlatformId(), IpUtils.getRemoteHost(request))) {
			return new UserLoginResult(ErrorCode.IP_ACCESS_RESTRICTION);
		}

		// 如果密码不对
		if (!passwordService.encrypt(password, user.getUsername()).equalsIgnoreCase(user.getPassword())) {
			user.setErrorCount(user.getErrorCount() + 1);
			if (user.getErrorCount() >= UserConstant.MAX_LOGIN_ERROR_COUNT) {
				user.setStatus(UserConstant.STATUS_EXCEPTION);
			}
			userRepository.save(user);
			return new UserLoginResult(ErrorCode.PASSWORD_ERROR);
		}

		// 非密码登录的账号永远提示密码错误...
		LoginPlatform platform = loginPlatformService.getLoginPlatform(user.getLoginPlatformId());
		if (platform == null || !platform.isPassword()) {
			return new UserLoginResult(ErrorCode.PASSWORD_ERROR);
		}

		user.setErrorCount(0);
		user.setStatus(UserConstant.STATUS_NORMAL);

		user.setLastLoginTime(user.getLoginTime());
		user.setLastLoginCity(user.getLoginCity());
		user.setLastLoginIp(user.getLoginIp());

		user.setLoginTime(new Date());
		user.setLoginIp(IpUtils.getRemoteHost(request));
		user.setLoginCity(IpUtils.getCity(user.getLoginIp()));

		userRepository.save(user);

		SessionUser suser = new SessionUser(user);
		suser.setAuth(authCacheManager.getAuthCache(user.getAuthGroupId()));
		return new UserLoginResult(ErrorCode.OK, suser);
	}

	/**
	 * 修改密码
	 */
	public int modifyPassword(SessionUser sessionUser, String oldPassword, String newPassword) {
		logger.info("修改密码 username={},name={}", sessionUser.getUsername(), sessionUser.getName());
		User user = userRepository.findFirstByUsername(sessionUser.getUsername());
		if (passwordService.encrypt(Md5Utils.encrypt(oldPassword), user.getUsername()).equalsIgnoreCase(user.getPassword())) {
			user.setPassword(passwordService.encrypt(Md5Utils.encrypt(newPassword), user.getUsername()));
			user.setModifyTime(new Date());
			userRepository.save(user);
			return ErrorCode.OK;
		}
		return ErrorCode.PASSWORD_ERROR;
	}

	/**
	 * 效验平台用户登录
	 */
	public UserLoginResult checkPlatformLogin(HttpServletRequest request, String platform, String username) {
		// 获得用户的数据
		User user = userRepository.findFirstByUsername(username);
		// 如果账号不存在
		if (user == null) {
			return new UserLoginResult(ErrorCode.ACCOUNT_DOES_NOT_EXIST);
		}

		// 锁定状态，那就不要用了
		if (user.getStatus() == UserConstant.STATUS_LOCK) {
			return new UserLoginResult(ErrorCode.ACCOUNT_STATUS_LOCK);
		}

		// 此账号不是这个平台的...
		if (!platform.equals(user.getLoginPlatformId())) {
			return new UserLoginResult(ErrorCode.ACCOUNT_DOES_NOT_EXIST);
		}

		// 白名单
		if (!accessRestrictionService.checkLoginIP(user.getLoginPlatformId(), IpUtils.getRemoteHost(request))) {
			return new UserLoginResult(ErrorCode.IP_ACCESS_RESTRICTION);
		}

		user.setErrorCount(0);
		user.setStatus(UserConstant.STATUS_NORMAL);

		user.setLastLoginTime(user.getLoginTime());
		user.setLastLoginCity(user.getLoginCity());
		user.setLastLoginIp(user.getLoginIp());

		user.setLoginTime(new Date());
		user.setLoginIp(IpUtils.getRemoteHost(request));
		user.setLoginCity(IpUtils.getCity(user.getLoginIp()));

		userRepository.save(user);

		SessionUser suser = new SessionUser(user);
		suser.setAuth(authCacheManager.getAuthCache(user.getAuthGroupId()));
		return new UserLoginResult(ErrorCode.OK, suser);
	}

}