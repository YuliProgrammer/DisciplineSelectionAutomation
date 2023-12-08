package com.discipline.selection.automation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachers")
@EqualsAndHashCode(of = "name")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teachers_disciplines",
            joinColumns = {@JoinColumn(name = "teacher_id")},
            inverseJoinColumns = {@JoinColumn(name = "discipline_id")})
    private Set<Discipline> disciplines = new HashSet<>();

    public Teacher(String name) {
        this.name = name;
    }

    public Teacher(Teacher teacher) {
        this.id = teacher.id;
        this.name = teacher.name;
        this.disciplines = Objects.isNull(teacher.disciplines) ? new HashSet<>()
                : teacher.disciplines.stream().map(Discipline::new).collect(Collectors.toSet());
    }

}
