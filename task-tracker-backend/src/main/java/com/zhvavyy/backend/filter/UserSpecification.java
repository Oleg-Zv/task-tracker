package com.zhvavyy.backend.filter;

import com.zhvavyy.backend.dto.UserFilterDto;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.zhvavyy.backend.model.QUser.*;

@Component
public class UserSpecification {

    public Specification<User> build(UserFilterDto filterDto){
        return (root, query, cb) -> {
            List<Predicate>predicates= new ArrayList<>();

            addPredicateIfNotNull(predicates,cb,root.get(user.role.getMetadata().getName()), filterDto.role());
            addPredicateIfNotNull(predicates,cb,root.get(user.email.getMetadata().getName()), filterDto.email());
            predicates.add(cb.notEqual(root.get(user.role.getMetadata().getName()), Role.ADMIN));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private <T> void addPredicateIfNotNull(List<Predicate>predicates,
                                           CriteriaBuilder cb, Path<T>field,T value){
        if(value !=null){
            predicates.add(cb.equal(field,value));
        }
    }

}
