# miaosha

## 页面的优化 
##  对象缓存
对象缓存一般用于常用的用户token信息等存放在redis中，但是需要注意到，如果用户更新了密码，要及时把redis内容更新，而且getByid这个常用的对象缓存也要删除，或者更新
```java

    public MiaoshaUser getById(long id) {

        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getByid, ""+id, MiaoshaUser.class);
        if (user != null){
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        if (user != null){
            redisService.set(MiaoshaUserKey.getByid, ""+id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass){
        //取user
        MiaoshaUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toUpdateUser = new MiaoshaUser();
        toUpdateUser.setId(id);
        toUpdateUser.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toUpdateUser);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getByid, ""+id);
        user.setPassword(toUpdateUser.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }
```
缓存模式
***
**失效**：应用程序先从cache取数据，没有得到，从数据库中取数据，成功后，放到缓存中
**命中**：应用程序先从cache取数据，取到后返回
**更新**：先把数据存到数据库中，成功之后，再让缓存失效



## service 调用别的 service

service想要调用别人的dao时候，不能直接调用，需要调用别人的的service，因为别人的service
中可能有缓存，导致数据不一致


## 避免商品卖超
1、数据库中加判断
```sql
@Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
```
2、秒杀表中增加唯一索引
```sql
名字：u_uid_gid
栏位：user_id, goods_id
索引类型：unique 
索引方式：btree
```

## 静态资源优化
1、JS/CSS压缩，减少流量  
2、多个JS/CSS组合，减少连接数  
3、CDN就近访问  

## 接口的优化

1、redis 预先减少库存减少数据库访问  
2、内存标记减少Redis访问  
3、请求先放入队列缓冲，异步下单，增强用户体验  
4、nginx水平扩展  

### 思路
1、系统初始化，把商品库存数量加载到Redis  
2、受到请求，Redis预减库存，直接返回，否则进入3  
3、请求入队，立即返回排队  
4、请求出队，生成订单，减少库存  
5、客户端轮询，是否秒杀成功  


### 安装RabbitMQ
1、安装ncurses-devel  
2、./configure --prefix=/usr/local/erlang --without-javac  
3、make  
4、make install   
5、安装simplejson  
6、install xmlto  
7、install python-simplejson  

