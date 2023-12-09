package com.mashang.useraccount.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alicp.jetcache.anno.Cached;
import com.mashang.common.valid.ParamValid;
import com.mashang.constants.Constant;
import com.mashang.useraccount.domain.AccountMenu;
import com.mashang.useraccount.domain.AccountRole;
import com.mashang.useraccount.domain.Menu;
import com.mashang.useraccount.domain.Role;
import com.mashang.useraccount.domain.RoleMenu;
import com.mashang.useraccount.param.AssignMenuParam;
import com.mashang.useraccount.param.AssignRoleParam;
import com.mashang.useraccount.param.MenuParam;
import com.mashang.useraccount.param.RoleParam;
import com.mashang.useraccount.repo.AccountMenuRepo;
import com.mashang.useraccount.repo.AccountRoleRepo;
import com.mashang.useraccount.repo.MenuRepo;
import com.mashang.useraccount.repo.RoleMenuRepo;
import com.mashang.useraccount.repo.RoleRepo;
import com.mashang.useraccount.vo.MenuVO;
import com.mashang.useraccount.vo.RoleVO;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class RbacService {

	@Autowired
	private MenuRepo menuRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private RoleMenuRepo roleMenuRepo;

	@Autowired
	private AccountRoleRepo accountRoleRepo;

	@Autowired
	private AccountMenuRepo accountMenuRepo;

	@Cached(name = "findMenuTreeByUserAccountId_", key = "args[0]", expire = 3600)
	@Transactional(readOnly = true)
	public List<MenuVO> findMenuTreeByUserAccountId(String userAccountId) {
		List<AccountRole> accountRoles = accountRoleRepo.findByUserAccountIdAndRoleDeletedFlagFalse(userAccountId);
		if (CollectionUtil.isEmpty(accountRoles)) {
			return findMenuTree();
		}
		List<MenuVO> menuVOs = new ArrayList<>();
		List<AccountMenu> accountMenus = accountMenuRepo
				.findByUserAccountIdAndMenuDeletedFlagFalseOrderByMenuOrderNo(userAccountId);
		for (AccountMenu accountMenu : accountMenus) {
			menuVOs.add(MenuVO.convertFor(accountMenu.getMenu()));
		}
		return buildMenuTree(menuVOs);
	}

	@ParamValid
	@Transactional
	public void assignRole(AssignRoleParam param) {
		List<AccountRole> assignRoles = accountRoleRepo.findByUserAccountId(param.getUserAccountId());
		accountRoleRepo.deleteAll(assignRoles);
		for (String roleId : param.getRoleIds()) {
			accountRoleRepo.save(AccountRole.build(param.getUserAccountId(), roleId));
		}
	}

	@Transactional(readOnly = true)
	public List<RoleVO> findRoleByUserAccountId(String userAccountId) {
		List<RoleVO> roleVOs = new ArrayList<>();
		List<AccountRole> accountRoles = accountRoleRepo.findByUserAccountId(userAccountId);
		for (AccountRole accountRole : accountRoles) {
			roleVOs.add(RoleVO.convertFor(accountRole.getRole()));
		}
		return roleVOs;
	}

	@ParamValid
	@Transactional
	public void assignMenu(AssignMenuParam param) {
		List<RoleMenu> roleMenus = roleMenuRepo.findByRoleId(param.getRoleId());
		roleMenuRepo.deleteAll(roleMenus);
		for (String menuId : param.getMenuIds()) {
			roleMenuRepo.save(RoleMenu.build(param.getRoleId(), menuId));
		}
	}

	@Transactional(readOnly = true)
	public List<MenuVO> findMenuByRoleId(String roleId) {
		List<MenuVO> menuVOs = new ArrayList<>();
		List<RoleMenu> roleMenus = roleMenuRepo.findByRoleId(roleId);
		for (RoleMenu roleMenu : roleMenus) {
			menuVOs.add(MenuVO.convertFor(roleMenu.getMenu()));
		}
		return menuVOs;
	}

	@Transactional(readOnly = true)
	public List<RoleVO> findAllRole() {
		return RoleVO.convertFor(roleRepo.findByDeletedFlagFalse());
	}

	@Transactional(readOnly = true)
	public RoleVO findRoleById(@NotBlank String id) {
		return RoleVO.convertFor(roleRepo.getOne(id));
	}

	@Transactional
	public void delRole(@NotBlank String id) {
		Role role = roleRepo.getOne(id);
		role.deleted();
		roleRepo.save(role);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateRole(RoleParam param) {
		if (StrUtil.isBlank(param.getId())) {
			Role role = param.convertToPo();
			roleRepo.save(role);
		} else {
			Role role = roleRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, role);
			roleRepo.save(role);
		}
	}

	@Transactional(readOnly = true)
	public MenuVO findMenuById(@NotBlank String id) {
		return MenuVO.convertFor(menuRepo.getOne(id));
	}

	@Transactional
	public void delMenu(@NotBlank String id) {
		List<Menu> subMenus = menuRepo.findByParentIdAndDeletedFlagFalse(id);
		for (Menu subMenu : subMenus) {
			List<Menu> btns = menuRepo.findByParentIdAndDeletedFlagFalse(subMenu.getId());
			for (Menu btn : btns) {
				btn.deleted();
				menuRepo.save(btn);
			}
		}
		if (CollectionUtil.isNotEmpty(subMenus)) {
			for (Menu subMenu : subMenus) {
				subMenu.deleted();
				menuRepo.save(subMenu);
			}
		}
		Menu menu = menuRepo.getOne(id);
		menu.deleted();
		menuRepo.save(menu);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateMenu(MenuParam param) {
		if (StrUtil.isBlank(param.getId())) {
			Menu menu = param.convertToPo();
			menuRepo.save(menu);
		} else {
			Menu menu = menuRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, menu);
			menuRepo.save(menu);
		}
	}

	@Transactional(readOnly = true)
	public List<MenuVO> findMenuTree() {
		List<Menu> menus = menuRepo.findByDeletedFlagFalseOrderByOrderNo();
		List<MenuVO> menuVOs = MenuVO.convertFor(menus);
		return buildMenuTree(menuVOs);
	}

	public List<MenuVO> buildMenuTree(List<MenuVO> menuVOs) {
		List<MenuVO> menu1s = new ArrayList<>();
		List<MenuVO> menu2s = new ArrayList<>();
		List<MenuVO> btns = new ArrayList<>();
		for (MenuVO m : menuVOs) {
			if (Constant.菜单类型_一级菜单.equals(m.getType())) {
				menu1s.add(m);
			}
			if (Constant.菜单类型_二级菜单.equals(m.getType())) {
				menu2s.add(m);
			}
			if (Constant.菜单类型_按钮.equals(m.getType())) {
				btns.add(m);
			}
		}
		for (MenuVO menu2 : menu2s) {
			for (MenuVO btn : btns) {
				if (menu2.getId().equals(btn.getParentId())) {
					menu2.getSubMenus().add(btn);
				}
			}
		}
		for (MenuVO menu1 : menu1s) {
			for (MenuVO menu2 : menu2s) {
				if (menu1.getId().equals(menu2.getParentId())) {
					menu1.getSubMenus().add(menu2);
				}
			}
		}
		for (MenuVO menu1 : menu1s) {
			for (MenuVO btn : btns) {
				if (menu1.getId().equals(btn.getParentId())) {
					menu1.getSubMenus().add(btn);
				}
			}
		}
		return menu1s;

	}

}
