package com.mc.userserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.VillageOrAppellationContants;
import com.mc.common.utils.VillageOrAppellationEnum;
import com.mc.userserver.entity.UserAddressTable;
import com.mc.userserver.entity.VillageTable;
import com.mc.userserver.mapper.VillageMapper;
import com.mc.userserver.service.UserAddressService;
import com.mc.userserver.service.VillageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

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
    @Override
    public VillageTable initVillage(Map<Object,String> data) {

        VillageTable villageTable = new VillageTable();
        //随机获取enum值作为初始化村庄的名称
        VillageOrAppellationEnum villageOrAppellationEnum = VillageOrAppellationEnum.getByCode(Integer.toString(RandomUtil.randomInt(1, 12) - 1));
        if (data.get("count").equals("3")) {
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
}
