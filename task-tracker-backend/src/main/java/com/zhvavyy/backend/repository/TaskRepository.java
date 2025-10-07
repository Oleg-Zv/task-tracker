package com.zhvavyy.backend.repository;


import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByStatus(Status status, Pageable pageable);
    List<Task> findAllByUserId(Long userId);

}
