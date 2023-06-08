package com.mc.userserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.PasswordEncoder;
import com.mc.common.utils.R;
import com.mc.common.utils.RegexUtils;
import com.mc.userserver.dto.UserDTO;
import com.mc.userserver.dto.loginDTO;
import com.mc.userserver.entity.UserTable;
import com.mc.userserver.mapper.UserMapper;
import com.mc.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mc.common.utils.AllStringCtant.*;
import static com.mc.common.utils.PasswordEncoder.matches;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 18:51
 * @类说明：填写类说明
 * @修改记录：
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserTable> implements UserService {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 通过注册方式判断该注册账号是否已被注册
     * @param type
     * @param user
     * @return
     */
    @Override
    public Boolean CheckPhoneIsOrNot(String type,UserTable user) {
        LambdaQueryWrapper<UserTable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
if (type.equals(COMMON_NUMBER_ZERO)){
    lambdaQueryWrapper.eq(UserTable::getPhone,user.getPhone());
}
        if (type.equals(COMMON_NUMBER_ONE)){
            lambdaQueryWrapper.eq(UserTable::getEmail,user.getEmail());
        }

        List<UserTable> list = userService.list(lambdaQueryWrapper);
        //当查询结果大于等于1时,表明该注册方式已被注册
        if (list.size()>=COMMON_NUMBER_INT_ONE) {
            return true;
        }
        return false;


    }

    /**
     * 用户登录
     *
     * @return
     */
    @Override
    public R<String> login(UserTable user, HttpServletRequest request) {
/*放在Controller层中
        UserTable user = null;
        //对密码进行加密
        String encodePwd = PasswordEncoder.encode(login.getPassword());
        //判断是密码登录还是短信验证登录 0 - 密码登录，  //密码验证正确则对账号进行核对
        if (type.equals(COMMON_NUMBER_ZERO)&&matches(encodePwd, login.getPassword())){

                //1.对帐号进行正则判断 判断账号形式为手机号还是邮箱
                if (!RegexUtils.isEmailInvalid(login.getAcount())){
                    user = query().eq("email", login.getAcount())
                            .one();
                }else{
                    user = query().eq("phone", login.getAcount())
                            .one();
                }
            }

        //1-短信验证登录 账号必然为手机号
        if (type.equals(COMMON_NUMBER_ONE)&&matches(encodePwd,login.getPassword())){
            //判断验证码 从reids中获取
            String CasheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + login.getAcount());
            String code = login.getCode();
            if (CasheCode == null || !CasheCode.equals(code)){
                //不一致 报错
                 return R.error("验证码错误");
            }
            user = query().eq("phone", login.getAcount()).one();
        }

        //判断用户是否存在
        if (user==null){
           return R.error("该用户账号不存在！");
        }*/
        //用户信息无误
        //7.保存用户信息到redis中
        //7.1随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        //7.2将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> usermap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((filedName,filedValue) -> filedValue.toString()));

        // 7.3存储
        String tokenkey =  LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenkey,usermap);
        //7.4 设置token有效时长
        stringRedisTemplate.expire(tokenkey,LOGIN_USER_TTL, TimeUnit.DAYS);



        // 8返回token
        return R.success(tokenkey);

    }

    @Override
    public R<String> sendCode(String phone, HttpSession session) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)){
            //2. 如果不符合 返回错误信息
            return R.error("手机号格式错误");
        }

        //3.符合 生成验证码
        String code = RandomUtil.randomNumbers(6);
        /*//4.保存验证码到session
        session.setAttribute("code",code);*/
        //4.保存至redis中  设置两分钟的过期时间
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);



        //5.发送验证码

        log.info("发送短信验证码成功，验证码:{}",code);
        //返回ok
        return R.success("验证码发送成功");
    }

}
