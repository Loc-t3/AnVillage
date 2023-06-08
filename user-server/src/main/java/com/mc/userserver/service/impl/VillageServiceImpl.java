package com.mc.userserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationContants;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.entity.UserAddressTable;
import com.mc.userserver.entity.VillageTable;
import com.mc.userserver.entity.VillageUserTable;
import com.mc.userserver.mapper.VillageMapper;
import com.mc.userserver.mapper.VillageUserMapper;
import com.mc.userserver.service.UserAddressService;
import com.mc.userserver.service.VillageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;

import static com.mc.common.utils.AllStringCtant.*;
import static com.mc.common.utils.UserCommon.setUUId;
import static com.mc.common.utils.VillageOrAppellationContants.*;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-05 10:01
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class VillageServiceImpl extends ServiceImpl<VillageMapper, VillageTable> implements VillageService {
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private VillageUserMapper villageUserMapper;
    @Override
    public VillageTable initVillage(Map<Object,String> data) {

        VillageTable villageTable = new VillageTable();
        //随机获取enum值作为初始化村庄的名称
        VillageOrAppellationEnum villageOrAppellationEnum = VillageOrAppellationEnum.getByCode(Integer.toString(RandomUtil.randomInt(1, 12) - 1));
        if (data.get("count").equals(COMMON_NUMBER_THREE)) {
            UserAddressTable address = userAddressService.getById(data.get("addressId"));
            villageTable.setVillageId(setUUId());
            villageTable.setVillageCode(RandomUtil.randomString(5));
            villageTable.setVillageName(villageOrAppellationEnum.getName());
            villageTable.setVillagePosition(address.getAddressInfo());
            villageTable.setVillageCreateUser(data.get("userId"));
            villageTable.setVillageUserNumber("1");
            this.save(villageTable);
        }


        return villageTable;
    }

    @Override
    @Transactional
    public R<VillageUserTable> addIntoVillage(Map<Object,String> data) {
        /**
         * 用户加入村庄,对应村庄人数增加
         * 在用户同意加入后执行
         * 村庄id，用户id，是否加入
         *
         */
        LambdaQueryWrapper<VillageUserTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VillageUserTable::getVillageId,data.get("villageId"))
                .eq(VillageUserTable::getUserId,data.get("userId"));
        VillageUserTable villageUserT = villageUserMapper.selectOne(queryWrapper);
        //健壮性判断
        if (!data.get("isAdd").equals(COMMON_NUMBER_ONE)){
            return R.error("用户拒绝加入");
        }
        //健壮性判断
        if (villageUserT!= null && villageUserT.getIsAdd().equals(COMMON_NUMBER_ONE)){
            return R.error("用户已加入，请勿重复加入");
        }
        VillageUserTable villageUser = new VillageUserTable();
        villageUser.setId(setUUId());
        villageUser.setUserId(data.get("userId"));
        villageUser.setVillageId(data.get("villageId"));
        villageUser.setIsAdd(data.get("isAdd"));
        villageUser.setVillageUserAppellation(APPELATION_NAME_1);
        villageUser.setVillageUserAppellationDesc(APPELATION_DESC_1);
        villageUserMapper.insert(villageUser);
        LambdaUpdateWrapper<VillageTable> UpdateWrapper = new LambdaUpdateWrapper<>();
        UpdateWrapper.setSql("village_user_Number = village_user_Number + 1").eq(VillageTable::getVillageId,data.get("villageId"));
        this.update(UpdateWrapper);

        return R.success(villageUser);
    }


}
