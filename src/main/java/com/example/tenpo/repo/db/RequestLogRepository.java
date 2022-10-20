package com.example.tenpo.repo.db;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestLogRepository extends PagingAndSortingRepository<RequestLog, Long>{







}
