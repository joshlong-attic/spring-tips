package com.example.liquibasedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue
    private Long id;
    private Date authored ;
    private String title;
    private Date published;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    public Article(String title, Date published, Collection<Comment> comments) {
        this.title = title;
        this.published = published;
        this.comments = new HashSet<>(comments);
    }
}
