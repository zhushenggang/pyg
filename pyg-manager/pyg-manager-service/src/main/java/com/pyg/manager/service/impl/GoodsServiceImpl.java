package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.GoodsService;
import com.pyg.mapper.*;
import com.pyg.pojo.*;
import com.pyg.utils.PageResult;
import com.pyg.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;


    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    //注入分类mapper接口代理对象
    @Autowired
    private TbItemCatMapper itemCatMapper;


    //注入品牌mapper接口代理对象
    @Autowired
    private BrandMapper brandMapper;

    //注入sellerMapper接口代理对象
    @Autowired
    private TbSellerMapper sellerMapper;

    //注入sku mepper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //创建example对象
        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("1");

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    public void add(Goods goods) {

        //保存spu货品表数据
        //获取spu货品对象
        TbGoods tbGoods = goods.getGoods();
        goodsMapper.insertSelective(tbGoods);
        //保存spu 货品描述表信息
        //获取描述表对象
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        //保存
        goodsDescMapper.insertSelective(goodsDesc);
        //保存sku商品表数据
        //获取sku列表集合对象
        List<TbItem> itemList = goods.getItemList();

        //判断是否启用规格
        if ("1".equals(tbGoods.getIsEnableSpec())) {
            //循环sku列表，保存商品数据
            for (TbItem tbItem : itemList) {

                //获取前台传递spec规格属性参数
                //{"网络":"联通4G","内存":"16G"}
                String spec = tbItem.getSpec();
                //把json字符串转换层map对象
                Map<String, String> specMap = (Map) JSON.parse(spec);

                //定义字符变量，拼接规格值
                String specStr = "";

                //循环map
                //联通4G 16G
                for (String key : specMap.keySet()) {
                    specStr += " " + specMap.get(key);
                }
                //设置标题
                //sku+spu
                tbItem.setTitle(tbGoods.getGoodsName() + specStr);

                this.saveItem(tbGoods, tbItem, goodsDesc);
                //实现保存操作
                itemMapper.insertSelective(tbItem);

            }
        } else {
            //没有启用规格
            //自己创建一个商品对象进行保存
            TbItem tbItem = new TbItem();
            //设置标题
            //spu
            tbItem.setTitle(tbGoods.getGoodsName());
            //抽取公共代码
            this.saveItem(tbGoods, tbItem, goodsDesc);
            //实现保存操作
            itemMapper.insertSelective(tbItem);

        }


    }

    /**
     * 添加商品属性
     */
    private void saveItem(TbGoods tbGoods, TbItem tbItem, TbGoodsDesc goodsDesc) {


        //买点
        tbItem.setSellPoint(tbGoods.getCaption());
        //图片地址
        //从描述对象中图片地址
        //[{"color":"蓝色","url":"http://192.168.66.67/group1/M00/00/02/wKhCQ1qzBHCABs3xAA1rIuRd3Es100.jpg"},
        // {"color":"黑色","url":"http://192.168.66.67/group1/M00/00/02/wKhCQ1qzBHmAVteGAAvea_OGt2M066.jpg"}]
        String itemImages = goodsDesc.getItemImages();
        //判断图片地址是否为空
        if (itemImages != null && !"".equals(itemImages)) {
            List<Map> imageList = JSON.parseArray(itemImages, Map.class);
            tbItem.setImage((String) imageList.get(0).get("url"));
        }

        //设置三级分类节点id，表示此商品属于哪个分类
        tbItem.setCategoryid(tbGoods.getCategory3Id());

        //创建时间
        Date date = new Date();
        tbItem.setCreateTime(date);
        tbItem.setUpdateTime(date);

        //设置货品id,sku id
        tbItem.setGoodsId(tbGoods.getId());
        tbItem.setSellerId(tbGoods.getSellerId());

        //查询分类名称，根据节点id
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        tbItem.setCategory(itemCat.getName());

        //查询品牌名称
        TbBrand brand = brandMapper.findOne(tbGoods.getBrandId());
        tbItem.setBrand(brand.getName());

        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
        tbItem.setSeller(seller.getNickName());

    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //先根据id查询出商品对象
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //修改删除状态，逻辑删除
            tbGoods.setIsDelete("0");

            //修改
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();

        if (goods != null) {

            if (goods.getSellerId() != null && !"".equals(goods.getSellerId())) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            //状态
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            //商品名称
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }

            criteria.andIsDeleteEqualTo("1");

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //运营商系统审核商品
    public void updateStatus(String status, Long[] ids) {

        //循环ids
        for (Long id : ids) {
            //根据id查询商品对象
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //设置状态
            tbGoods.setAuditStatus(status);

            //更新
            goodsMapper.updateByPrimaryKey(tbGoods);

        }

    }

    @Override
    public void isMarketable(String status, Long[] ids) {

        //循环ids
        for (Long id : ids) {
            //根据id查询商品对象
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //设置状态
            tbGoods.setIsMarketable(status);

            //更新
            goodsMapper.updateByPrimaryKey(tbGoods);

        }
    }

    /**
     * 需求：查询所有审核通过sku商品
     */
    public List<TbItem> findItemList(Long[] ids) {

        //创建集合封装审核通过sku商品
        List<TbItem> itemList = new ArrayList<>();

        //遍历查询商品
        for (Long id : ids) {
            //创建商品example对象
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            //设置外键
            criteria.andGoodsIdEqualTo(id);

            //查询
            List<TbItem> list = itemMapper.selectByExample(example);
            //添加
            itemList.addAll(list);

        }

        return itemList;
    }

}
