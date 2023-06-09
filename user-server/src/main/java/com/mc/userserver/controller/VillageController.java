package com.mc.userserver.controller;

import com.mc.common.utils.R;
import com.mc.userserver.config.SysLog;
import com.mc.userserver.entity.VillageTable;
import com.mc.userserver.entity.VillageUserTable;
import com.mc.userserver.mapper.VillageUserMapper;
import com.mc.userserver.service.VillageService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-05 10:06
 * @类说明：填写类说明
 * @修改记录：
 */
@RestController
@RequestMapping("/Village")
public class VillageController {
    @Autowired
    private VillageService villageService;
    @Autowired
    private VillageUserMapper villageUserMapper;

    /**
     * 生成村庄(由当前地址的第三人触发创建)
     * @param data
     * @return
     */
    @PostMapping("/save")
    @SysLog(value = "#{'用户-操作-生成村庄成功'}",level = "info",printResult = 0)
    public R<HashMap<String, Object>> saveVillage(@RequestBody Map<Object,String> data){
        HashMap<String, Object> map = new HashMap<>();
        VillageTable villageTable = villageService.initVillage(data);
        map.put("villageTable",villageTable);

        return R.success(map);
    }

    /**
     * 发送信息至用户，用户同意后加入村庄
     * @param
     * @return
     */
    @PostMapping("add")
    @SysLog(value = "#{'用户-操作-加入村庄成功'}",level = "info",printResult = 0)
    public R<VillageUserTable> addIntoVillage(@RequestBody Map<Object,String> data){

        return  villageService.addIntoVillage(data);


    }





}
