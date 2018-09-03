package com.pyg.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.SellerService;
import com.pyg.pojo.TbSeller;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){			
		return sellerService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return sellerService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbSeller seller){
		try {
			//创建bcrypt加密对象
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String newPwd = passwordEncoder.encode(seller.getPassword());
			seller.setPassword(newPwd);
			sellerService.add(seller);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbSeller seller){
		try {
			sellerService.update(seller);
			return new PygResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeller findOne(){
		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		return sellerService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(String [] ids){
		try {
			sellerService.delete(ids);
			return new PygResult(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param seller
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeller seller, int page, int rows  ){
		return sellerService.findPage(seller, page, rows);		
	}

	/**
	 * 修改密码
	 */
	@RequestMapping("/updatePassword/{oldPwd}/{newPwd}/{reNewPwd}")
	public PygResult updatePassword(@PathVariable String oldPwd,@PathVariable String newPwd,@PathVariable String reNewPwd){
		try {
			//获取商家id
			String id = SecurityContextHolder.getContext().getAuthentication().getName();
			PygResult pygResult = sellerService.updatePassword(id, oldPwd, newPwd, reNewPwd);
			return pygResult;
		}catch (Exception e){
			e.printStackTrace();
			return new PygResult(false,"密码修改失败");
		}

	}
	
}
