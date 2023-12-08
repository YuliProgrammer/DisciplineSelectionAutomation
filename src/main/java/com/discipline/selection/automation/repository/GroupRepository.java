package com.discipline.selection.automation.repository;

import com.discipline.selection.automation.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    Set<Group> findAllByGroupCodeIn(Collection<String> groupCodes);

}
