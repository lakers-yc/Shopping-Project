package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.CategoryReport;
import com.changgou.order.service.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/categoryReport")
@CrossOrigin
public class CategoryReportController extends AbstractCoreController<CategoryReport>{

    private CategoryReportService  categoryReportService;

    @Autowired
    public CategoryReportController(CategoryReportService  categoryReportService) {
        super(categoryReportService, CategoryReport.class);
        this.categoryReportService = categoryReportService;
    }
}
