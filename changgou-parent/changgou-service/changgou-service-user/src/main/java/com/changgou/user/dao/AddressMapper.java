package com.changgou.user.dao;
import com.changgou.user.pojo.Address;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:AddressDao
 * @Date 2019/6/14 0:12
 *****/
public interface AddressMapper extends Mapper<Address> {
    @Select(value = "select * from tb_address where username =#{username} order by is_default desc")
    List<Address> list(String username);
}
