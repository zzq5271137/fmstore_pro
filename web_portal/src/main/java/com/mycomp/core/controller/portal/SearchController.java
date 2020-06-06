package com.mycomp.core.controller.portal;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.service.itemsearch.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {

    @Reference
    private SearchService searchService;

    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map searchMap) {
        return searchService.search(searchMap);
    }

}
