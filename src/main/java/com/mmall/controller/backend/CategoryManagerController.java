package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.RespondeCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by zhaoxin on 2018/3/11.
 */
@Controller
@RequestMapping(value = "/manage/category/")
public class CategoryManagerController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue ="0") Integer parentId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(RespondeCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        ServerResponse result=userService.checkAdminRole(user);
        if (result.isSuccess()){
            return categoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作 需要管理员权限");
        }

    }

    @RequestMapping("update_category_name.do")
    @ResponseBody
    public ServerResponse<String> updateCategory(HttpSession session,Integer categoryId,String categoryName ){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(RespondeCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        ServerResponse result=userService.checkAdminRole(user);
        if (result.isSuccess()){
            //更新categoryName
            return  categoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作 需要管理员权限");
        }
    }
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(RespondeCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        ServerResponse result=userService.checkAdminRole(user);
        if (result.isSuccess()){
            //获取平级子分类
            return categoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作 需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(RespondeCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        ServerResponse result=userService.checkAdminRole(user);
        if (result.isSuccess()){
            //查询当前节点id和递归获取子节点id
            return categoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作 需要管理员权限");
        }
    }

}
