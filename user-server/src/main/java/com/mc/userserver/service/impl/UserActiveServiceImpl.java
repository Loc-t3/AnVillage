package com.mc.userserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.UserCommon;
import com.mc.userserver.entity.UserActiveTable;
import com.mc.userserver.entity.UserFansTable;
import com.mc.userserver.entity.UserProfileTable;
import com.mc.userserver.mapper.UserActiveMapper;
import com.mc.userserver.service.UserActiveService;
import com.mc.userserver.service.UserFansService;
import com.mc.userserver.service.UserProService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.mc.common.utils.AllStringCtant.common_number_one;
import static com.mc.common.utils.AllStringCtant.common_number_zero;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-04 7:43
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class UserActiveServiceImpl extends ServiceImpl<UserActiveMapper, UserActiveTable> implements UserActiveService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserFansService userFansService;
    @Autowired
    private UserProService userProService;


    /**
     * 实现关注用户
     * @param activedid
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean toActiveUser(String activedid, HttpServletRequest request) {

        Boolean flag = false;
        String authorization = request.getHeader("Authorization");
        //约定好 的有前缀的 bearer token
        String realToken = authorization.replaceFirst("bearer ","");
        //通过token获取点击关注用户（成为粉丝用户）的id
        String fansuserId = (String) stringRedisTemplate.opsForHash().get(realToken, "userId");


        //对关注表添加相应关系数据
        LambdaQueryWrapper<UserActiveTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActiveTable::getUserId,fansuserId)
                .eq(UserActiveTable::getUserActiveId,activedid).eq(UserActiveTable::getActiveRelationCode,common_number_one);
        UserActiveTable active = this.getOne(queryWrapper);
        //健壮性判断
        if (active!=null){
            log.info("用户：{} - 已经关注 用户 {}",fansuserId,activedid);
            return false;
        }
        UserActiveTable userActiveTable = new UserActiveTable();
        userActiveTable.setActiveId(UserCommon.setUUId());
        userActiveTable.setUserId(fansuserId);
        userActiveTable.setUserActiveId(activedid);
        userActiveTable.setActiveRelationCode(common_number_one);

       /**此处对粉丝关系就不必再进行健壮性判断 因为在上方如果不符合 A用户已经对B用户进行了关注 不再向下执行

        LambdaQueryWrapper<UserFansTable> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(UserFansTable::getUserId,fansuserId)
                .eq(UserFansTable::getUserFansId,activedid).eq(UserFansTable::getFansRelationCode,common_number_one);
        UserFansTable fans = userFansService.getOne(queryWrapper2);
        //健壮性判断
        if (fans!=null){
            log.info("用户：{} - 已经成为了 用户 {} 的粉丝",fansuserId,activedid);
            return false;
        }*/

        //对粉丝表添加相应关系数据
        UserFansTable userFansTable = new UserFansTable();
        userFansTable.setFansId(UserCommon.setUUId());
        userFansTable.setUserId(activedid);
        userFansTable.setUserFansId(fansuserId);
        userFansTable.setFansRelationCode(common_number_one);

        //对个人用户资料关注者和粉丝数量进行增加
        //关注者(userId) 关注数量+1
        LambdaUpdateWrapper<UserProfileTable> activeWrapper = new LambdaUpdateWrapper<>();
        activeWrapper.in(UserProfileTable::getUserId,fansuserId).setSql("profile_active_num = profile_active_num +1");

        //被关注者(activedid)  粉丝数量+1
        LambdaUpdateWrapper<UserProfileTable> activedWrapper = new LambdaUpdateWrapper<>();
        activedWrapper.in(UserProfileTable::getUserId,activedid).setSql("profile_fans_num = profile_fans_num +1");


        try {
            //保存关注表操作数据
            this.save(userActiveTable);
            userFansService.save(userFansTable);
            userProService.update(activeWrapper);
            userProService.update(activedWrapper);
            flag = true;
            log.info("用户：{} 与 用户 {} 的粉丝<->关注关系已成立",fansuserId,activedid);
        }catch (Exception e){
            log.error("发生错误："+e);

        }

        return flag;

    }


    /**
     * 实现取消关注
     * @param activedid 当前用户粉丝id
     * @param request  获取当前用户id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delActiveUser(String activedid, HttpServletRequest request) {
        Boolean flag = false;

        String authorization = request.getHeader("Authorization");
        //约定好 的有前缀的 bearer token
        String realToken = authorization.replaceFirst("bearer ","");
        //通过token获取点击关注用户（成为粉丝用户）的id
        String fansuserId = (String) stringRedisTemplate.opsForHash().get(realToken, "userId");

        //对关注信息进行修改
        //获取原有数据
        LambdaQueryWrapper<UserActiveTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActiveTable::getUserId,fansuserId)
                .eq(UserActiveTable::getUserActiveId,activedid).eq(UserActiveTable::getActiveRelationCode,common_number_one);
        UserActiveTable one = this.getOne(queryWrapper);
        /*//对数据进行修改
        LambdaUpdateWrapper<UserActiveTable> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserActiveTable::getActiveRelationCode,common_number_zero)
                .in(UserActiveTable::getActiveId,one.getActiveId());*/

        //对粉丝信息进行修改
        //获取原有数据
        LambdaQueryWrapper<UserFansTable> fansqueryWrapper = new LambdaQueryWrapper<>();
        fansqueryWrapper.eq(UserFansTable::getUserId,activedid)
                .eq(UserFansTable::getUserFansId,fansuserId).eq(UserFansTable::getFansRelationCode,common_number_one);
        UserFansTable fansTable = userFansService.getOne(fansqueryWrapper);
        /*//对数据进行修改
        LambdaUpdateWrapper<UserFansTable> fansupdateWrapper = new LambdaUpdateWrapper<>();
        fansupdateWrapper.set(UserFansTable::getFansRelationCode,common_number_zero)
                .in(UserFansTable::getFansId,fansTable.getFansId());*/



        //对用户资料表的关注人数-1
        LambdaUpdateWrapper<UserProfileTable> delActiveWrapper = new LambdaUpdateWrapper<>();
        delActiveWrapper.in(UserProfileTable::getUserId,fansuserId).setSql("profile_active_num = profile_active_num - 1");

        //被关注者(activedid)  粉丝数量-1
        LambdaUpdateWrapper<UserProfileTable> fansWrapper = new LambdaUpdateWrapper<>();
        fansWrapper.in(UserProfileTable::getUserId,activedid).setSql("profile_fans_num = profile_fans_num -1");

        try {
            //保存关注表操作数据
            this.remove(queryWrapper);
            userFansService.remove(fansqueryWrapper);
            userProService.update(delActiveWrapper);
            userProService.update(fansWrapper);
            flag = true;
            log.info("用户：{} 与 用户 {} 的粉丝<->关注关系已取消",fansuserId,activedid);


        }catch (Exception e){
            log.error("发生错误："+e);
        }

        return flag;
    }




}
