package cn.qeng.gm.module.backstage.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.auth.Auth;
import cn.qeng.gm.core.auth.AuthResource;
import cn.qeng.gm.core.auth.AuthTemplate;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.core.log.RecordLog;
import cn.qeng.gm.core.token.Token;
import cn.qeng.gm.module.backstage.domain.AuthGroup;
import cn.qeng.gm.module.backstage.service.AuthService;

/**
 * 权限组访问接口类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Controller
@RequestMapping("/backstage/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private AuthTemplate authTemplate;

	/**
	 * 权限组管理，左边导航.
	 */
	@RequestMapping("/manage")
	@Auth(AuthResource.AUTH_GROUP_LIST)
	@RecordLog(OperationType.BACKSTAGE_AUTH_MANAGE)
	public ModelAndView manage() {
		return this.list(0, PageConstant.MAX_SIZE);
	}

	/**
	 * 查看权限组.(这里用于分页查询功能，但不需要记录日志)
	 */
	@RequestMapping("/list")
	@Auth(AuthResource.AUTH_GROUP_LIST)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "15") int size) {
		ModelAndView view = new ModelAndView("backstage/auth/list");
		view.addObject("page", authService.getAuths(page, size));
		return view;
	}

	/**
	 * 添加界面
	 */
	@Token
	@RequestMapping("/addUI")
	@Auth(AuthResource.AUTH_GROUP_ADD)
	@RecordLog(OperationType.BACKSTAGE_AUTH_ADD_UI)
	public ModelAndView addUI() {
		ModelAndView view = new ModelAndView("backstage/auth/edit");
		view.addObject("template", authTemplate);
		return view;
	}

	/**
	 * 添加权限组
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/add/")
	@Auth(AuthResource.AUTH_GROUP_ADD)
	@RecordLog(value = OperationType.BACKSTAGE_AUTH_ADD, args = { "authname" })
	public String add(String authname, @RequestParam(value = "auths[]", required = false) String[] auths, @RequestParam(value = "platforms[]", required = false) String[] platforms) {
		authService.add(authname, auths, platforms);
		return "OK";
	}

	/**
	 * 编辑界面
	 */
	@Token
	@RequestMapping("/editUI/{id}/")
	@Auth(AuthResource.AUTH_GROUP_EDIT)
	@RecordLog(OperationType.BACKSTAGE_AUTH_EDIT_UI)
	public ModelAndView editUI(@PathVariable("id") int id, @RequestParam(required = false, defaultValue = "0") int page) {
		ModelAndView view = this.addUI();

		AuthGroup auth = authService.getAuthor(id);
		view.addObject("auth", auth);

		Map<Object, Boolean> selectedSet = new HashMap<>();
		for (String a : JSON.parseArray(auth.getAuth(), String.class)) {
			selectedSet.put(a, Boolean.TRUE);
		}

		view.addObject("selectedSet", selectedSet);
		return view;
	}

	/**
	 * 编辑权限组
	 */
	@ResponseBody
	@Token(check = true)
	@RequestMapping("/edit/")
	@Auth(AuthResource.AUTH_GROUP_EDIT)
	@RecordLog(OperationType.BACKSTAGE_AUTH_EDIT)
	public String edit(@RequestParam(required = false, defaultValue = "0") int id, String authname, @RequestParam(value = "auths[]", required = false) String[] auths, @RequestParam(value = "platforms[]", required = false) String[] platforms) {
		if (id > 0) {
			authService.edit(id, authname, auths, platforms);
		} else {
			authService.add(authname, auths, platforms);
		}
		return "OK";
	}

	/**
	 * 删除权限组
	 */
	@RequestMapping("/delete/{id}/")
	@Auth(AuthResource.AUTH_GROUP_DELETE)
	@RecordLog(OperationType.BACKSTAGE_AUTH_DELETE)
	public ModelAndView delete(@PathVariable("id") int id) {
		authService.delete(id);
		return this.list(0, PageConstant.MAX_SIZE);
	}
}