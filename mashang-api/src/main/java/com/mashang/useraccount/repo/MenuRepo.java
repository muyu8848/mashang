package com.mashang.useraccount.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.Menu;

public interface MenuRepo extends JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {
	
	List<Menu> findByDeletedFlagFalseOrderByOrderNo();
	
	List<Menu> findByParentIdAndDeletedFlagFalse(String parentId);

}
