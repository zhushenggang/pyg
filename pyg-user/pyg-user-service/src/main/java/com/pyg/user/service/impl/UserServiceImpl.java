package com.pyg.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.pojo.*;
import com.pyg.user.service.UserService;
import com.pyg.mapper.TbUserMapper;
import com.pyg.pojo.TbUserExample.Criteria;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		//使用spring工具类加密
		String newPwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(newPwd);
		//创建时间
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
		userMapper.insertSelective(user);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}
			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//注入redis模板对象
	@Autowired
	private RedisTemplate redisTemplate;

	//注入jms模板对象
	@Autowired
	private JmsTemplate jmsTemplate;


	@Value("${sign_name}")
	private String sign_name;

	@Value("${template_code}")
	private String template_code;

	/**
	 * 需求：获取短信验证码
	 * 参数：手机号
	 * 返回值：void
	 * 获取短信验证码流程：
	 * 1,生成6位数验证码
	 * 2,把验证码存储到redis服务器
	 * 3，设置过期时间 5分钟
	 * 4，发送消息
	 */
	@Override
	public void getSmsCode(String phone) {

		try {
			//1,生成6位数验证码
			//0.2343243243243
			long code = (long)(Math.random()*1000000);
			// 2,把验证码存储到redis服务器
			redisTemplate.boundHashOps("smsCode").put(phone,code);
			//3，设置过期时间 5分钟
			redisTemplate.boundHashOps("smsCode").expire(5, TimeUnit.MINUTES);

			//创建map,封装发送数据
			Map<String,String> maps = new HashMap<>();
			//手机号
			maps.put("phone",phone);
			//处理编码
			String sname = new String(sign_name.getBytes("ISO-8859-1"),"UTF-8");

			//签名
			maps.put("sign_name",sname);
			//模板code
			maps.put("template_code",template_code);
			//验证码
			maps.put("code",code+"");

			//4,发送消息
			jmsTemplate.convertAndSend("sms",maps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 需求：验证验证码是否匹配
	 */
	public boolean checkCode(String phone, String smsCode) {
		//获取redis服务器验证码
		Long code = (Long) redisTemplate.boundHashOps("smsCode").get(phone);
		if(smsCode.equals(code+"")){
			return  true;
		}
		return false;
	}

	@Autowired
	private TbOrderMapper tbOrderMapper;

	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	/**
	 * 查询 用户 订单
	 */
	@Override
	public List<TbOrder> findOrderList(String name) {

		TbOrderExample example = new TbOrderExample();
		TbOrderExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(name);
		List<TbOrder> orderList = tbOrderMapper.selectByExample(example);

		TbOrderItemExample example1 = new TbOrderItemExample();
		for (TbOrder tbOrder : orderList) {
			TbOrderItemExample.Criteria criteria1 = example1.createCriteria();
			criteria1.andOrderIdEqualTo(tbOrder.getOrderId());
			List<TbOrderItem> orderItems = tbOrderItemMapper.selectByExample(example1);
			tbOrder.setTbOrderItemList(orderItems);
		}

		return orderList;
	}

	@Override
	public void saveUserInfo(TbUser user) {

        TbUserExample example = new TbUserExample();
        Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());

        List<TbUser> tbUsers = userMapper.selectByExample(example);

        for (TbUser tbUser : tbUsers) {
            user.setId(tbUser.getId());
            userMapper.updateByPrimaryKeySelective(user);
        }


        //userMapper.updateByExampleSelective(user,example);

    }

	@Override
	public PygResult changepwd(String userName,String oldpwd, String newpwd) {
		TbUserExample example = new TbUserExample();
        Criteria criteria = example.createCriteria();
        oldpwd = DigestUtils.md5DigestAsHex(oldpwd.getBytes());
        criteria.andPasswordEqualTo(oldpwd);
        criteria.andUsernameEqualTo(userName);
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        if(tbUsers == null || tbUsers.size() == 0){
            return new PygResult(true,"原密码错误");
        }
        TbUserExample example2 = new TbUserExample();
        Criteria criteria1 = example2.createCriteria();
        criteria1.andUsernameEqualTo(userName);
        TbUser user = new TbUser();
        user.setPassword(DigestUtils.md5DigestAsHex(newpwd.getBytes()));
        userMapper.updateByExampleSelective(user,example2);
        return new PygResult(true,"修改成功");
	}


}
