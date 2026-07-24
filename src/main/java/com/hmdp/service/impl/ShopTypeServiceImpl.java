package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询类型列表
     * @return
     */
    @Override
    public Result queryTypeList() {
        //查询redis缓存商户类型
        String typeListJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOPTYPE_KEY);

        //存放返回数据
        List<ShopType> shopTypeList ;

        //判断查询结果
        if(StrUtil.isNotBlank(typeListJson)){
            //存在返回数据
            shopTypeList = JSONUtil.toList(typeListJson, ShopType.class);
            return Result.ok(shopTypeList);
        }

        //不存在查询数据库
        shopTypeList = query().orderByAsc("sort").list();

        //判断查询结果，为空返回错误
        if(shopTypeList == null){
            return Result.fail("商铺总类型查询失败");
        }

        //存在，载入缓存并返回
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOPTYPE_KEY, JSONUtil.toJsonStr(shopTypeList));

        return Result.ok(shopTypeList);
    }
}
