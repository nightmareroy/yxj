package cn.qeng.gm.module.backstage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.core.auth.AuthCacheManager;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.module.backstage.domain.AuthGroup;
import cn.qeng.gm.module.backstage.domain.AuthGroupRepository;

/**
 * 权限组业务逻辑处理类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class AuthService {
	@Autowired
	private AuthCacheManager authCacheManager;
	@Autowired
	private AuthGroupRepository authGroupRepository;

	// 启动加载权限组
	@PostConstruct
	public void init() {
		authGroupRepository.findAll().forEach(group -> authCacheManager.update(group));
	}

	/**
	 * 初始化默认权限组
	 */
	public int initDefaultAdminAuth() {
		Page<AuthGroup> authGroup = authGroupRepository.findAll(new PageRequest(1, 1, new Sort(Sort.Direction.ASC, "id")));
		if (!authGroup.getContent().isEmpty()) {
			return authGroup.getContent().get(0).getId();
		}

		AuthGroup group = new AuthGroup();
		List<String> auths = new ArrayList<>();
		for (AuthResource a : AuthResource.values()) {
			auths.add(a.name());
		}
		this.set(group, "超级管理员", auths.toArray(new String[auths.size()]), new String[] {});
		group.setName("超级管理员");
		group.setSuperman(true);
		group.setAuth("[]");// 标识为超级管理员就可以了不需要一个一个设计权限
		group.setCreateTime(new Date());
		group.setModifyTime(group.getCreateTime());
		authGroupRepository.save(group);

		authCacheManager.update(group);// 一定要通知缓存管理类来修改内存数据.
		return group.getId();
	}

	/**
	 * 权限配置.
	 */
	private void set(AuthGroup authGroup, String authname, String[] auths, String[] platfroms) {
		authGroup.setName(authname);
		authGroup.setAuth(JSON.toJSONString(auths));
		authGroup.setPlatforms(JSON.toJSONString(platfroms));
	}

	/**
	 * 查看全部权限组
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<AuthGroup> getAuths(int page, int size) {
		return authGroupRepository.findAll(new PageRequest(page, size));
	}

	/**
	 * 获取全部的权限组信息(账号管理需要展示的相关权限组)
	 */
	public Map<Integer, AuthGroup> getAllAuthers() {
		return authGroupRepository.findAll().stream().collect(Collectors.toMap(AuthGroup::getId, Function.identity()));
	}

	/**
	 * 获取权限组名称
	 */
	public List<AuthGroup> getAllAutheres() {
		return authGroupRepository.findAll();
	}

	/**
	 * 获得某个权限的信息
	 * 
	 * @param id
	 * @return
	 */
	public AuthGroup getAuthor(int id) {
		return authGroupRepository.findOne(id);
	}

	/**
	 * 添加权限组
	 * 
	 * @param authname
	 * @param auths
	 * @param platfroms
	 */
	public void add(String authname, String[] auths, String[] platfroms) {
		AuthGroup authGroup = new AuthGroup();
		this.set(authGroup, authname, auths, platfroms);
		authGroup.setCreateTime(new Date());
		authGroup.setModifyTime(authGroup.getCreateTime());
		authGroupRepository.save(authGroup);
		authCacheManager.update(authGroup);
	}

	/**
	 * 编辑权限组
	 * 
	 * @param id
	 * @param authname
	 * @param auths
	 * @param platfroms
	 */
	public void edit(int id, String authname, String[] auths, String[] platfroms) {
		AuthGroup authGroup = this.getAuthor(id);
		if (authGroup == null) {
			this.add(authname, auths, platfroms);
		} else {
			this.set(authGroup, authname, auths, platfroms);
			authGroup.setModifyTime(new Date());
			authGroupRepository.save(authGroup);
			authCacheManager.update(authGroup);
		}
	}

	public void delete(int id) {
		authGroupRepository.delete(id);
	}
}
