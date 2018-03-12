package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TockenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by zhaoxin on 2018/3/7.
 */
@Service("iUserService")
@Transactional
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        Integer resultCout=userMapper.checkUsername(username);
        if (resultCout==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码md5加密
        password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,password);
        if (user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //密码设置为null
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //校验用户名
        Integer resultCount=userMapper.checkUsername(user.getUsername());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        //校验邮箱
        Integer emailResult=userMapper.checkEmail(user.getEmail());
        if (emailResult>0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //密码md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)){
            Integer resultCount;
             switch (type){
                 case Const.USERNAME:
                     resultCount=userMapper.checkUsername(str);
                     if (resultCount>0){
                         return ServerResponse.createByErrorMessage("用户名已存在");
                     }
                 break;
                 case Const.EMAIL:
                     //校验邮箱
                     resultCount=userMapper.checkEmail(str);
                     if (resultCount>0){
                        return ServerResponse.createByErrorMessage("邮箱已存在");
                     }
                 break;
                default:
                    return ServerResponse.createByErrorMessage("参数错误");
             }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        Integer resultCount=userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }else {
            return ServerResponse.createByErrorMessage("找回密码的问题是空");
        }
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        Integer resultCount=userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            //说明问题及问题答案是正确的
            String forgetToken= UUID.randomUUID().toString();
            TockenCache.setKey(TockenCache.TOCKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetRestPasswor(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误 tocken需要传递");
        }
        Integer resultCount=userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String tocken=TockenCache.getByKey(TockenCache.TOCKEN_PREFIX+username);
        if (StringUtils.isBlank(tocken)){
            return ServerResponse.createByErrorMessage("tocken无效或者过期");
        }

        if (StringUtils.equals(forgetToken,tocken)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePassword(username,md5Password);
            if (rowCount>0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("tocken错误 请重新获取充值密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> restPasswod(String passwordOld, String passwordNew, User user) {
        //防止用户的横向越权 要校验一下这个用户旧密码一定指向这个用户
        passwordOld=MD5Util.MD5EncodeUtf8(passwordOld);
        int  resultCout=userMapper.checkPassword(passwordOld,user.getId());
        if (resultCout==0){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCout=userMapper.updateByPrimaryKeySelective(user);
        if (resultCout>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username不能更被更新
        //email也要进行校验校验email是不是已经存在同时也不能是当前的用户email
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        //跟新特定字段
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setUsername(user.getUsername());
        resultCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount>0){
            return ServerResponse.createBySuccess("用户更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("用户更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user=userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user){
        //校验用户的权限
        if (user!=null && user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("权限不够");
        }else{
            return ServerResponse.createBySuccess();
        }
    }
}
