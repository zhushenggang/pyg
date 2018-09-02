package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable int page, @PathVariable int rows) {
        return seckillOrderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/add")
    public PygResult add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return new PygResult(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/update")
    public PygResult update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbSeckillOrder findOne(@PathVariable Long id) {
        return seckillOrderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete/{ids}")
    public PygResult delete(@PathVariable Long[] ids) {
        try {
            seckillOrderService.delete(ids);
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int rows) {
        return seckillOrderService.findPage(seckillOrder, page, rows);
    }


    /**
     * 秒杀下单实现
     * 判断商品入库商品是否存在，如果不存在，表示已售罄
     * 2）判断用户是否在排队，如果在排序，获取用户订单，如果有订单，表示有支付订单，不能下单
     * 3）用户在排队，那就是排队中
     * 4）否则，没有排队，排序秒杀人数是否超限
     * 5）如果秒杀人数没有超限，那么就可以参与排队，参与秒杀。
     * 6）参与排队： 存储排队记录，存储排队用户，存储排序人数+1
     */
    @RequestMapping("saveOrder/{seckillId}")
    public PygResult saveOrder(@PathVariable Long seckillId, HttpServletRequest request) {
        try {
            //获取用户登录信息
            String userId = request.getRemoteUser();
            //调用远程秒杀订单服务，实现秒杀下单
            seckillOrderService.createOrder(seckillId, userId);
            return new PygResult(true, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "下单失败");
        }
    }


    /**
     * 需求：查询订单状态
     * 请求：checkSeckillOrderStatus
     * 参数：无
     * 返回值：PygResult
     */
    @RequestMapping("checkSeckillOrderStatus")
    public PygResult checkSeckillOrderStatus(HttpServletRequest request) {
        try {
            //获取用户名
            String userId = request.getRemoteUser();
            //调用服务成方法
            TbSeckillOrder seckillOrder = seckillOrderService.checkSeckillOrderStatus(userId);
            //判断此时用户是否秒杀成功
            if (seckillOrder != null) {
                return new PygResult(true, "秒杀成功");
            }
            //查询当前排队人数
            Long users = seckillOrderService.findSeckillUsers();
            return new PygResult(false, "前面还有" + (users - 1) + "人排队");

        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "404");
        }
    }


}


