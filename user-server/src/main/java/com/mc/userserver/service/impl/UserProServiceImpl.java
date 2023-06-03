package com.mc.userserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.entity.UserProfileTable;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.mapper.UserProMapper;
import com.mc.userserver.service.UserProService;
import com.mc.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-05-03 5:17
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class UserProServiceImpl extends ServiceImpl<UserProMapper, UserProfileTable> implements UserProService {
    @Autowired
    private UserProService userProService;
    @Autowired
    private UserService userService;
    /**
     * 修改用户信息
     * @param userProfileTable
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> AlterInfo(UserProfileTable userProfileTable) {
        //从线程获取用户id并与接收到的数据进行对比
        String userId = BaseContext.getUser().getUserId();
        String prouserId = userProfileTable.getUserId();
        if (userId.equals(prouserId)){

            /*UserDTO userDTO = userProfileTable.getUserDTO();
            UserTable userTable = new UserTable();
            userTable.setUserId(userId);
            userTable.setAvatar(userDTO.getAvatar());
            userTable.setNickname(userDTO.getNickname());*/
            //用户表 user_table
            LambdaUpdateWrapper<UserTable> updateWrapper1 = new LambdaUpdateWrapper<>();
            updateWrapper1.set(UserTable::getAvatar,userProfileTable.getUserDTO().getAvatar())
                    .set(UserTable::getNickname,userProfileTable.getUserDTO().getNickname())
                    .in(UserTable::getUserId,userId);

            //通过构造器 用户id获取详细信息 user_profile_table

            LambdaUpdateWrapper<UserProfileTable> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserProfileTable::getGender,userProfileTable.getGender())
                    .set(UserProfileTable::getBirthday,userProfileTable.getBirthday())
                    .set(UserProfileTable::getSchool,userProfileTable.getSchool())
                    .in(UserProfileTable::getUserId,prouserId);


            try {
                //更新数据
                this.update(updateWrapper);
                userService.update(updateWrapper1);
            }catch (Exception e){
                log.error("发生错误："+e);
                return R.error("发生错误，请稍后重试");
            }

            //返回数据
            return R.success("操作成功");

        }
        else {
            return R.error("发生错误，请稍后重试");
        }







    }
}
