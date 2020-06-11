package com.mycomp.core.service.goodstemplate.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mycomp.core.dao.good.BrandDao;
import com.mycomp.core.dao.good.GoodsDao;
import com.mycomp.core.dao.good.GoodsDescDao;
import com.mycomp.core.dao.item.ItemCatDao;
import com.mycomp.core.dao.item.ItemDao;
import com.mycomp.core.dao.seller.SellerDao;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.good.GoodsDesc;
import com.mycomp.core.pojo.good.GoodsDescQuery;
import com.mycomp.core.pojo.good.GoodsQuery;
import com.mycomp.core.pojo.item.Item;
import com.mycomp.core.pojo.item.ItemQuery;
import com.mycomp.core.pojo.queryentity.GoodsEntity;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.exceptions.DeleteGoodsFromDBException;
import com.mycomp.core.service.goodstemplate.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private SellerDao sellerDao;

    /*
     * @Reference
     * private SolrManagementService solrManagementService;
     *
     * @Reference
     * private CmsService cmsService;
     *
     * 不需要注入这两个服务了, 因为使用了ActiveMQ, 向消息服务器发送消息, 以异步的形式处理相关任务,
     * 而不是在这里处理完任务;
     */
    // Spring提供的JMS工具类, 它可以进行消息发送、接收等
    @Autowired
    private JmsTemplate jmsTemplate;

    // 发布订阅模式, 自定义Topic, 用于存放将商品导入索引库和生成静态页面的消息
    @Resource(name = "topicPageAndSolrDestination")
    private ActiveMQTopic topicPageAndSolrDestination;

    // 点对点模式, 自定义Queue, 用于存放删除索引库的消息
    @Resource(name = "queueSolrDeleteDestination")
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    public void addGoods(GoodsEntity goodsEntity) {
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());
        insertItem(goodsEntity);
    }

    private void insertItem(GoodsEntity goodsEntity) {
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            if (goodsEntity.getItemList() != null) {
                goodsEntity.getItemList().forEach(item -> {
                    StringBuilder title = new StringBuilder(goodsEntity.getGoods().getGoodsName());
                    String specStr = item.getSpec();
                    Map specMap = JSON.parseObject(specStr);
                    for (Object value : specMap.values()) {
                        title.append(" ").append(value);
                    }
                    item.setTitle(title.toString());
                    setItemValues(item, goodsEntity);
                    itemDao.insertSelective(item);
                });
            }
        } else {
            Item item = new Item();
            item.setPrice(new BigDecimal("99999999999"));
            item.setNum(0);
            item.setSpec("{}");
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            setItemValues(item, goodsEntity);
            itemDao.insertSelective(item);
        }
    }

    private void setItemValues(Item item, GoodsEntity goodsEntity) {
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        item.setGoodsId(goodsEntity.getGoods().getId());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setStatus("0");
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id()).getName());
        item.setBrand(brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId()).getName());
        item.setSeller(sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId()).getName());
        List<Map> itemImages = JSON.parseArray(goodsEntity.getGoodsDesc().getItemImages(), Map.class);
        if (itemImages != null && itemImages.size() > 0) {
            String url = String.valueOf(itemImages.get(0).get("url"));
            item.setImage(url);
        }
    }

    @Override
    public PageResult<Goods> getGoodsPage(Integer page, Integer pageSize, Goods goodsSearch) {
        PageHelper.startPage(page, pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.setOrderByClause("id desc");
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goodsSearch != null) {
            if (goodsSearch.getGoodsName() != null && !goodsSearch.getGoodsName().equals("")) {
                criteria.andGoodsNameLike("%" + goodsSearch.getGoodsName() + "%");
            }
            if (goodsSearch.getAuditStatus() != null && !goodsSearch.getAuditStatus().equals("")
                    && !goodsSearch.getAuditStatus().equals("-1")) {
                criteria.andAuditStatusEqualTo(goodsSearch.getAuditStatus());
            }
            if (goodsSearch.getSellerId() != null && !goodsSearch.getSellerId().equals("")
                    && !goodsSearch.getSellerId().equals("admin")) {
                criteria.andSellerIdEqualTo(goodsSearch.getSellerId());
            }
        }
        if (goodsSearch != null && goodsSearch.getSellerId() != null && !goodsSearch.getSellerId().equals("")) {
            if (!goodsSearch.getSellerId().equals("admin")) {
                criteria.andIsDeleteIsNull();
            } else {
                if (goodsSearch.getIsDelete() != null && goodsSearch.getIsDelete().equals("1")) {
                    criteria.andIsDeleteEqualTo(goodsSearch.getIsDelete());
                }
            }
        }
        Page<Goods> pageRes = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult<Goods>(pageRes.getTotal(), pageRes.getResult());
    }

    @Override
    public GoodsEntity getGoodsEntity(Long id) {
        // 查询goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        // 查询goodsDesc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        // 查询itemList
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        // 封装成GoodsEntity并返回
        return new GoodsEntity(goods, goodsDesc, itemList);
    }

    @Override
    public void updateGoods(GoodsEntity goodsEntity) {
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        insertItem(goodsEntity);
    }

    @Override
    public void deleteGoods(Long[] targetIds) {
        if (targetIds != null && targetIds.length > 0) {
            Arrays.asList(targetIds).forEach(id -> {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsDelete("1");  // 并不是真正的从数据库中删掉数据
                goodsDao.updateByPrimaryKeySelective(goods);
            });

            /*
             * // 删除Solr搜索服务器中的相关数据
             * solrManagementService.deleteItemsFromSolr(Arrays.asList(targetIds));
             *
             * 同样, 删除Solr索引库中商品的相关数据这个任务也是通过消息中间件, 以异步的形式处理的;
             */
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(targetIds);
                }
            });
        }

    }

    @Override
    public void updateStatus(Long[] targetIds, String status) {
        if (targetIds != null && targetIds.length > 0) {
            Arrays.asList(targetIds).forEach(targetId -> {
                Goods goods = new Goods();
                goods.setId(targetId);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);
                Item item = new Item();
                item.setStatus(status);
                ItemQuery itemQuery = new ItemQuery();
                ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
                itemQueryCriteria.andGoodsIdEqualTo(targetId);
                itemDao.updateByExampleSelective(item, itemQuery);
            });

            if ("2".equals(status)) {  // 2: 审核通过
                /*
                 * 审核通过的商品, 我们需要将其数据保存到Solr索引库中、以及生成相应的html静态页面;
                 * 但是, 无论是将数据保存到Solr索引库, 还是生成静态页面, 对于用户(admin)来说,
                 * 都是不关心的, 因为这些是你服务器内部需要做的工作, 而用户(admin)关心的只是商品的状态是否修改完成;
                 * 如果将这两步操作直接写在这里, 他们就会以串行的形式执行(因为他们是以Dubbo接口的形式进行远程调用的,
                 * 而Dubbo是同步的技术), 也就是说, 用户(admin)必须等待这两步耗时操作的完成, 才能得到返回结果,
                 * 这是很差的用户体验; 并且, Tomcat服务器以及MySql数据库所支持的并发量并不高, 如果在高并发的应用场景下,
                 * 用户可能需要更长时间的等待, 严重的话, Tomcat服务器和MySql数据库可能会被拖垮宕机;
                 *     // 审核通过的商品, 需要保存到Solr搜索服务器中
                 *     List<Item> items = getItemsByGoodsIds(targetIds, "2");
                 *     solrManagementService.saveItemsToSolr(items);
                 *     // 审核通过的商品, 需要生成相应的静态页面
                 *     Arrays.asList(targetIds).forEach(id -> {
                 *         try {
                 *             cmsService.createStaticPage(id);
                 *         } catch (Exception e) {
                 *             e.printStackTrace();
                 *         }
                 *     });
                 * 所以我们需要使用消息中间件技术, 它是一种异步技术(对比来说, Dubbo是一种同步的技术,
                 * 因为调用Dubbo远程接口, 需要等待该接口执行完成并返回, 才能继续之后的操作),
                 * 这就意味着, 我们在这里只需要向消息服务器发送两个消息(即将数据保存到Solr索引库的消息,
                 * 以及生成静态页面的消息, 当然, 还要传给消息服务器相应的数据, 比如商品id等),
                 * 这次请求就会立刻完成并返回, 不会等待这两个操作的完成; 至于这两个操作(消息)什么时候被执行(消费),
                 * 这取决于消息服务器的状态、以及消息消费者(具体执行这些操作的服务器)的状态,
                 * 但这并不是当前用户(admin)所关心的;
                 *
                 * 我们使用JMS + ActiveMQ实现消息服务(JMS是JavaEE定义的技术规范, 全称为Java Message Service,
                 * 即Java消息服务), JMS提供统一接口, ActiveMQ提供具体服务; 这样做的好处是, 更换消息中间件不需要修改业务代码;
                 * 即当我们想要更换提供具体服务的消息中间件时(比如想要换成RabbitMQ、Kafka等), 调用消息服务的业务代码是不需要修改的,
                 * 因为由JMS提供的消息服务的接口并没有改变, 我们只需要改变配置, 让JMS使用新的消息中间件即可;
                 */
                // 由于这里是使用Topic发布订阅模式, 所以只需要发送一个消息, Solr工程以及Page工程就都会接收到
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(targetIds);
                    }
                });
            }
        }
    }

    @Override
    public List<Item> getItemsByGoodsIds(Long[] goodsIds, String status) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemDao.selectByExample(query);
    }

    @Override
    public void deleteGoodsFromDB(Long[] targetIds) throws DeleteGoodsFromDBException {
        for (Long targetId : targetIds) {
            Goods goods = goodsDao.selectByPrimaryKey(targetId);
            if (goods.getIsDelete() == null || goods.getIsDelete().equals("")
                    || !goods.getIsDelete().equals("1")) {
                throw new DeleteGoodsFromDBException("选择的待删除商品中存在还未被商家删除的商品, 您不能删除...");
            }
        }
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria itemQueryCriteria = itemQuery.createCriteria();
        itemQueryCriteria.andGoodsIdIn(Arrays.asList(targetIds));
        itemDao.deleteByExample(itemQuery);
        GoodsDescQuery goodsDescQuery = new GoodsDescQuery();
        GoodsDescQuery.Criteria goodsDescQueryCriteria = goodsDescQuery.createCriteria();
        goodsDescQueryCriteria.andGoodsIdIn(Arrays.asList(targetIds));
        goodsDescDao.deleteByExample(goodsDescQuery);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria goodsQueryCriteria = goodsQuery.createCriteria();
        goodsQueryCriteria.andIdIn(Arrays.asList(targetIds));
        goodsDao.deleteByExample(goodsQuery);
    }

}
