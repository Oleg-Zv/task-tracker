package com.zhvavyy.backend.repository;


import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByStatus(Status status, Pageable pageable);
    Page<Task> findAllByUserId(Long userId, Pageable pageable);

}
