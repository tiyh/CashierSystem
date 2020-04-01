package com.kupanet.cashiersystem.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kupanet.cashiersystem.DAO.ProductMapper;
import com.kupanet.cashiersystem.model.Product;
import com.kupanet.cashiersystem.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductMapper productMapper;


    @Override
    public int create(/*ProductParam*/ Product productParam) {
        int count;
        //创建商品
        Product product = productParam;
        product.setId(null);
        productMapper.insert(product);
        //根据促销类型设置价格：、阶梯价格、满减价格
        Long productId = product.getId();
        //会员价格
        //阶梯价格
        //满减价格
        //处理sku的编码
        //添加sku库存信息
        //添加商品参数,添加自定义商品规格
        //关联专题
        //关联优选
        count = 1;
        return count;
    }

    @Override
    public Product getUpdateInfo(Long id) {
        return productMapper.getUpdateInfo(id);
    }

    @Override
    public int update(Long id, Product productParam) {
        int count;
        //更新商品信息
        Product product = productParam;
        product.setId(id);
        productMapper.updateById(product);
         count = 1;
        return count;
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        return 0;
    }


    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        Product record = new Product();
        record.setRecommandStatus(recommendStatus);

        return productMapper.update(record, new QueryWrapper<Product>().eq("id",ids));
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        Product record = new Product();
        record.setNewStatus(newStatus);

        return productMapper.update(record, new QueryWrapper<Product>().eq("id",ids));
    }

    @Override
    public List<Product> list(String keyword) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("delete_status",0);

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("name",keyword);

        }
        return productMapper.selectList(queryWrapper);
    }

    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.delBool(productMapper.deleteById(id));
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return SqlHelper.delBool(productMapper.deleteBatchIds(idList));
    }

    @Override
    public Product getById(Serializable id) {
        return productMapper.selectById(id);
    }



    /**
     * 建立和插入关系表操作
     *
     * @param dao       可以操作的dao
     * @param dataList  要插入的数据
     * @param productId 建立关系的id
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            Method insertList = dao.getClass().getMethod("saveBatch", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            logger.warn("创建产品出错:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public IPage<Product> page(IPage<Product> page, Wrapper<Product> queryWrapper) {
        queryWrapper = (Wrapper<Product>) SqlHelper.fillWrapper(page, queryWrapper);
        return productMapper.selectPage(page, queryWrapper);
    }

}



