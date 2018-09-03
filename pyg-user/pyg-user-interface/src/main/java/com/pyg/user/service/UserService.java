package com.pyg.user.service;
import java.util.List;

import com.pyg.pojo.TbOrder;

import com.pyg.pojo.TbUser;

import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum, int pageSize);
	/**
	 * 需求：获取短信验证码
	 * 参数：手机号
	 * 返回值：void
	 */
	public void getSmsCode(String phone);
	/**
	 * 需求：验证验证码是否匹配
	 */
	public boolean checkCode(String phone,String smsCode);


    void saveUserInfo(TbUser user);

    PygResult changepwd(String username,String oldpwd, String newpwd);

	List<TbOrder> findOrderList(String name);
}
