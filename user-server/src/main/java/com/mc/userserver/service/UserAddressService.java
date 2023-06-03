package com.mc.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.userserver.entity.UserAddressTable;
import org.springframework.stereotype.Service;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-01 15:16
 * @类说明：填写类说明
 * @修改记录：
 */

public interface UserAddressService extends IService<UserAddressTable> {
    /**
     *新增地址
     * @param userAddressTable
     * @return
     */
    Boolean addAddress(UserAddressTable userAddressTable);

    /**
     * 修改地址
     * @param userAddressTable
     * @return
     */
    Boolean alterAddress(UserAddressTable userAddressTable);
}
