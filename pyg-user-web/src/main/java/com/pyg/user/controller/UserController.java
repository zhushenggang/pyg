package com.pyg.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.AddressService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import com.pyg.utils.PageResult;
import com.pyg.utils.PhoneFormatCheckUtils;
import com.pyg.utils.PygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 10000000)
	private UserService userService;

	@Reference(timeout = 10000000)
	private AddressService addressService;



	/**
	 * 查询 用户 订单
	 */
	@RequestMapping("/findOrderSe")
	public List<TbOrder> findOrderList(){

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return userService.findOrderList(name);
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add/{smsCode}")
	public PygResult add(@RequestBody TbUser user,@PathVariable String smsCode){
		try {
			//验证验证码是否正确
			boolean flag = userService.checkCode(user.getPhone(),smsCode);
			if(!flag){
				return  new PygResult(false,"验证码错误");
			}
			userService.add(user);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new PygResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(Long [] ids){
		try {
			userService.delete(ids);
			return new PygResult(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	/**
	 * 需求：获取短信验证码
	 * 参数：手机号
	 * 返回值：pygResult
	 */
	@RequestMapping("getSmsCode/{phone}")
	public PygResult getSmsCode(@PathVariable String phone){
		try {
			//验证手机号格式是否正确
			if(!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
				return new PygResult(false,"手机号格式不正确");
			}
			//调用服务层
			userService.getSmsCode(phone);
			//
			return new PygResult(true,"发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false,"发送失败");
		}
	}

    /**
     * 修改密码
     * @param oldpwd
     * @param newpwd
     * @param request
     * @return
     */
	@RequestMapping("/changepwd/{oldpwd}/{newpwd}")
    public PygResult changepwd(@PathVariable String oldpwd, @PathVariable String newpwd, HttpServletRequest request){
        PygResult pygResult = null;
        try {
            String userName = request.getRemoteUser();
            pygResult = userService.changepwd(userName, oldpwd, newpwd);
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false,"修改失败");
        }
        return pygResult;

    }

    /**
     * 修改密码
     * @param mobileNum
     * @param request
     * @return
     */
    @RequestMapping("/changeMobile/{mobileNum}")
    public PygResult changeMobile(@PathVariable String mobileNum,HttpServletRequest request){
        String userName = request.getRemoteUser();
        return userService.changeMobile(userName,mobileNum);
    }

    @RequestMapping("/checkCode/{phoneCode}/{mobileNum}")
    public PygResult checkCode(@PathVariable String phoneCode,@PathVariable String mobileNum){
        try {
            //验证验证码是否正确
            boolean flag = userService.checkMobileCode(mobileNum,phoneCode);
            if(!flag){
                return  new PygResult(false,"验证码错误");
            }
            return new PygResult(true, "验证码正确");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "系统出错");
        }
    }

    @RequestMapping("/sendCodeToNewPhone/{newPhoneNum}")
	public PygResult sendCodeToNewPhone(@PathVariable String newPhoneNum){
        return userService.sendCodeToNewPhone(newPhoneNum);
	}

	@RequestMapping("/newPhoneCodeCheck/{newPhoneCode}/{newPhoneNum}")
    public PygResult newPhoneCodeCheck(@PathVariable String newPhoneCode,
                                       @PathVariable String newPhoneNum,
                                       HttpServletRequest request){
        String userName = request.getRemoteUser();
        return userService.newPhoneCodeCheck(newPhoneCode,newPhoneNum,userName);
    }

}
