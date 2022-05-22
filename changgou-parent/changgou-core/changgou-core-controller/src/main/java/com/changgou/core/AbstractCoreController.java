package com.changgou.core;

import com.changgou.core.service.CoreService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core
 * @version 1.0
 * @date 2020/8/10
 */
public abstract class AbstractCoreController<T> implements ICoreController<T> {

    //调用方的service
    protected CoreService<T> coreService;
    //调用方的类型
    protected Class<T> clazz;

    public AbstractCoreController(CoreService<T> coreService, Class<T> clazz) {
        this.coreService = coreService;
        this.clazz = clazz;
    }

    /**
     * 删除记录
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Override
    public Result deleteById(@PathVariable(name = "id") Object id) {
        coreService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 添加记录
     *
     * @param record
     * @return
     */
    @PostMapping
    @Override
    public Result insert(@RequestBody T record) {
        coreService.insert(record);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /**
     * 分页查询记录
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    @Override
    public Result<PageInfo<T>> findByPage(@PathVariable(name = "page") Integer pageNo,
                                          @PathVariable(name = "size") Integer pageSize) {
        PageInfo<T> pageInfo = coreService.findByPage(pageNo, pageSize);
        return new Result<PageInfo<T>>(true, StatusCode.OK, "分页查询成功", pageInfo);
    }

    @PostMapping(value = "/search/{page}/{size}")
    @Override
    public Result<PageInfo<T>> findByPage(@PathVariable(name = "page") Integer pageNo,
                                          @PathVariable(name = "size") Integer pageSize,
                                          @RequestBody T record) {
        PageInfo<T> pageInfo = coreService.findByPage(pageNo, pageSize, record);
        return new Result<PageInfo<T>>(true, StatusCode.OK, "条件分页查询成功", pageInfo);
    }

    @Override
    @GetMapping("/{id}")
    public Result<T> findById(@PathVariable(name = "id") Object id) {
        T t = coreService.selectByPrimaryKey(id);
        return new Result<T>(true, StatusCode.OK, "查询单个数据成功", t);
    }

    @Override
    @GetMapping
    public Result<List<T>> findAll() {
        List<T> list = coreService.selectAll();
        return new Result<List<T>>(true, StatusCode.OK, "查询所有数据成功", list);
    }

    //更新数据
    @Override
    @PutMapping
    public Result updateByPrimaryKey(@RequestBody T record) {
        coreService.updateByPrimaryKey(record);
        return new Result(true, StatusCode.OK, "更新成功");
    }
}
