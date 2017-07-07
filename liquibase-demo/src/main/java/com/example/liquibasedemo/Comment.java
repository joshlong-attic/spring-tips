package com.example.liquibasedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "article")
@NoArgsConstructor
public class Comment {

    public Comment(Article article, String txt) {
        this.comment = txt;
        article.getComments().add(this);
        this.article = article;
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Article article;

    private String comment;
}
