package com.pm.project;

import com.pm.common.BaseEntity;
import com.pm.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor
public class Project extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "project_key", nullable = false, unique = true, length = 10)
    private String key;

    @Column(columnDefinition = "TEXT DEFAULT ''")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
