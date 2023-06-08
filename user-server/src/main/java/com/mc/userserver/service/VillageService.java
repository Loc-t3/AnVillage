package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.common.utils.R;
import com.mc.userserver.entity.VillageTable;
import com.mc.userserver.entity.VillageUserTable;
import com.mc.userserver.mapper.VillageMapper;

import java.util.Map;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-05 10:01
 * @类说明：填写类说明
 * @修改记录：
 */
public interface VillageService extends IService<VillageTable> {

    VillageTable initVillage(Map<Object,String> data);

    R<VillageUserTable> addIntoVillage(Map<Object,String> data);
}
