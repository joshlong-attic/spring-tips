package com.example.liquibasedemo;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureDataJpa
public class ArticleTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    public void should_store_article_comments() throws Throwable {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        int ctr = 0;
        Article article = articleRepository.save(new Article("An article", new Date(), new ArrayList<>()));
        Comment a = commentRepository.save(new Comment(article, "comment " + (++ctr)));
        Comment b = commentRepository.save(new Comment(article, "comment " + (++ctr)));
        Article one = articleRepository.findOne(article.getId());
        BDDAssertions.then(a.getArticle()).isEqualTo(article);
        BDDAssertions.then(b.getArticle()).isEqualTo(article);
        BDDAssertions.then(one.getComments().size()).isEqualTo(2);
    }
}