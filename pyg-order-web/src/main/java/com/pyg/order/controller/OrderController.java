package com.pyg.order.controller;
import java.util.List;

import com.pyg.cart.service.CartService;
import com.pyg.vo.Cart;
import com.pyg.vo.OrderInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbOrder;
import com.pyg.order.service.OrderService;

import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;

import javax.servlet.http.HttpServletRequest;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference(timeout = 1000000)
	private OrderService orderService;


	//注入购物车服务
	@Reference(timeout = 1000000)
	private CartService cartService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){			
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public PygResult add(@RequestBody TbOrder order){
		try {
			orderService.add(order);
			return new PygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public PygResult update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
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
	public TbOrder findOne(Long id){
		return orderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public PygResult delete(Long [] ids){
		try {
			orderService.delete(ids);
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
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}

	/**
	 * 需求：查询购物车送货清单
	 * 参数：request
	 * 返回值：List<Cart>
	 */
	@RequestMapping("findOrderCartList")
	public List<Cart> findOrderCartList(HttpServletRequest request){
		//获取用户登录名
		String username = request.getRemoteUser();
		//根据用户名查询用户购物车数据
		List<Cart> cartList = cartService.findRedisCartList(username);

		return cartList;
	}

	/**
	 * 需求：提交订单
	 * 参数：OrderInfo
	 * 返回值：pygResult
	 */
	@RequestMapping("submitOrder")
	public PygResult submitOrder(@RequestBody OrderInfo orderInfo,HttpServletRequest request){
		try {
			//获取当前用户登录信息
			String username = request.getRemoteUser();

			//给订单设置用户
			orderInfo.getOrders().setUserId(username);

			//获取购物车购物清单
			List<Cart> redisCartList = cartService.findRedisCartList(username);
			//调用服务方法，实现订单提交
			orderService.submitOrder(orderInfo,redisCartList);
			return new PygResult(true,"提交成功");
		} catch (Exception e) {
			e.printStackTrace();
			return  new PygResult(false,"提交失败");
		}
	}
}
