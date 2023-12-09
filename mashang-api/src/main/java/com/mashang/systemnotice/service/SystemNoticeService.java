package com.mashang.systemnotice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.systemnotice.domain.SystemNotice;
import com.mashang.systemnotice.domain.SystemNoticeMarkReadRecord;
import com.mashang.systemnotice.param.AddOrUpdateSystemNoticeParam;
import com.mashang.systemnotice.param.SystemNoticeQueryCondParam;
import com.mashang.systemnotice.repo.SystemNoticeMarkReadRecordRepo;
import com.mashang.systemnotice.repo.SystemNoticeRepo;
import com.mashang.systemnotice.vo.SystemNoticeVO;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class SystemNoticeService {

	@Autowired
	private SystemNoticeRepo systemNoticeRepo;

	@Autowired
	private SystemNoticeMarkReadRecordRepo markReadRecordRepo;
	
	@Transactional(readOnly = true)
	public List<SystemNoticeVO> findTop10PublishedNotice() {
		return SystemNoticeVO
				.convertFor(systemNoticeRepo.findTop10ByCreateTimeIsLessThanEqualOrderByCreateTimeDesc(new Date()));
	}

	@Transactional
	public void markRead(@NotBlank String id, @NotBlank String userAccountId) {
		SystemNoticeMarkReadRecord markReadRecord = SystemNoticeMarkReadRecord.build(id, userAccountId);
		markReadRecordRepo.save(markReadRecord);
	}

	@Transactional(readOnly = true)
	public SystemNoticeVO getLatestNotice(@NotBlank String userAccountId) {
		SystemNoticeVO vo = SystemNoticeVO
				.convertFor(systemNoticeRepo.findTopByCreateTimeIsLessThanEqualOrderByCreateTimeDesc(new Date()));
		if (vo == null) {
			return null;
		}
		List<SystemNoticeMarkReadRecord> markReadRecords = markReadRecordRepo
				.findBySystemNoticeIdAndUserAccountId(vo.getId(), userAccountId);
		if (CollectionUtil.isNotEmpty(markReadRecords)) {
			return null;
		}
		return vo;
	}

	@ParamValid
	@Transactional
	public void addOrUpdateSystemNotice(AddOrUpdateSystemNoticeParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			SystemNotice systemNotice = param.convertToPo();
			systemNoticeRepo.save(systemNotice);
		}
		// 修改
		else {
			SystemNotice systemNotice = systemNoticeRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, systemNotice);
			systemNoticeRepo.save(systemNotice);
		}
	}

	@Transactional
	public void delSystemNoticeById(@NotBlank String id) {
		systemNoticeRepo.deleteById(id);
	}

	@Transactional(readOnly = true)
	public SystemNoticeVO findSystemNoticeById(String id) {
		return SystemNoticeVO.convertFor(systemNoticeRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public PageResult<SystemNoticeVO> findSystemNoticeByPage(SystemNoticeQueryCondParam param) {
		Specification<SystemNotice> spec = new Specification<SystemNotice>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<SystemNotice> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getTitle())) {
					predicates.add(builder.like(root.get("title"), "%" + param.getTitle() + "%"));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<SystemNotice> result = systemNoticeRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<SystemNoticeVO> pageResult = new PageResult<>(SystemNoticeVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

}
