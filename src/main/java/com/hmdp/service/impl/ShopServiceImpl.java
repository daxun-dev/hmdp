package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @Override
    public Result queryById(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        //从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        //判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            //存在，返回数据
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

        //不存在，查询数据库
        Shop shop = getById(id);

        //判断查询结果
        if (shop != null) {
            //存在，将数据载入缓存并返回
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop),30L, TimeUnit.MINUTES);
            //返回
            return Result.ok(shop);
        }

        //不存在，返回错误
        return Result.fail("店铺不存在");
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if(id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 1.更新数据库
        updateById(shop);

        // 2.删除缓存
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);

        return Result.ok();
    }
}
