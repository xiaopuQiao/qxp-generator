package com.yupi.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.web.model.dto.generator.GeneratorQueryRequest;
import com.yupi.web.model.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子服务测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class GeneratorServiceTest {

    @Resource
    private GeneratorService generatorService;

    @Test
    void searchFromEs() {
        GeneratorQueryRequest generatorQueryRequest = new GeneratorQueryRequest();
        generatorQueryRequest.setUserId(1L);
        Page<Post> postPage = generatorService.searchFromEs(generatorQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}