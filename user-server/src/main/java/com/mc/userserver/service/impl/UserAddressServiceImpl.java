package com.mc.userserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.R;
import com.mc.common.utils.ResultsCode;
import com.mc.userserver.entity.UserAddressTable;
import com.mc.userserver.mapper.UserAddressMapper;
import com.mc.userserver.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mc.common.utils.UserCommon.setUUId;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-01 15:17
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddressTable> implements UserAddressService {
    /**
     *新增用户地址
     * @param userAddressTable
     * @return
     */
    @Override
    public Map<String,String> addAddress(UserAddressTable userAddressTable) {
        HashMap<String, String> map = new HashMap<>();
        userAddressTable.setAddressId(setUUId());
        //获取需要处理的地址详情下标
        Integer index = reIndex(userAddressTable);
        if (index>0){
            String substring = userAddressTable.getAddressInfo().substring(index+1, userAddressTable.getAddressInfo().length());
            userAddressTable.setAddressInfo(substring);
        }
        try {
            boolean save = this.save(userAddressTable);
        }catch (Exception e){

        }
        //查询当前用户新增地址被使用的记录数
        LambdaQueryWrapper<UserAddressTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.groupBy(UserAddressTable::getAddressInfo).having("address_info = {0}",userAddressTable.getAddressInfo());
        int count = this.count(queryWrapper);
        map.put("addressId",userAddressTable.getAddressId());
        map.put("userId",userAddressTable.getUserId());
        map.put("count",Integer.toString(count));



        /*//保存数据成功 返回当前地址id和操作用户id
        if (save){
            map.put("addressId",userAddressTable.getAddressId());
            map.put("userId",userAddressTable.getUserId());

        }*/

        return map;

    }

    /**
     * 修改用户地址
     * @param userAddressTable
     * @return
     */
    @Override
    public Boolean alterAddress(UserAddressTable userAddressTable) {
        Integer index = reIndex(userAddressTable);
        if (index>0){
            String substring = userAddressTable.getAddressInfo().substring(index+1, userAddressTable.getAddressInfo().length());
            userAddressTable.setAddressInfo(substring);
        }

       boolean update = this.updateById(userAddressTable);
        return update;
    }

    /**
     * 处理用户编辑地址信息地级市重复
     * @param userAddressTable
     * @return
     */
    private Integer reIndex(UserAddressTable userAddressTable){
        Integer index = 0;
        HashSet<Integer> indexSet = new HashSet<>();
        String addressInfo = userAddressTable.getAddressInfo();
        ArrayList<String> lists = new ArrayList<String>(
                Arrays.asList("县","自治县","区","自治旗","旗","市","林区","特区")
        );
        for (String list:lists) {
            indexSet.add(addressInfo.lastIndexOf(list));
        }
        //获取set集合中的最大值并判断是否存在重复地级市
        index = indexSet.stream().max(Integer::compare).get();
        return index;
    }
}
