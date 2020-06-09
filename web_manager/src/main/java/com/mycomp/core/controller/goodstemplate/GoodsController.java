package com.mycomp.core.controller.goodstemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mycomp.core.pojo.good.Goods;
import com.mycomp.core.pojo.queryentity.PageResult;
import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.core.service.cmsservice.CmsService;
import com.mycomp.core.service.exceptions.DeleteGoodsFromDBException;
import com.mycomp.core.service.goodstemplate.GoodsService;
import com.mycomp.core.service.itemsearch.SolrManagementService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private SolrManagementService solrManagementService;

    @Reference
    private CmsService cmsService;

    @RequestMapping("/getGoodsPage")
    public PageResult<Goods> getGoodsPage(@RequestParam("page") Integer page,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestBody Goods goodsSearch) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsSearch.setSellerId(sellerId);
        return goodsService.getGoodsPage(page, pageSize, goodsSearch);
    }

    @RequestMapping("/updateStatus")
    public RestResult updateStatus(Long[] targetIds, String status) {
        try {
            goodsService.updateStatus(targetIds, status);
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
                 *     List<Item> items = goodsService.getItemsByGoodsIds(targetIds, "2");
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
            }
            return new RestResult(true, "商品状态修改成功！");
        } catch (Exception e) {
            return new RestResult(false, "商品状态修改失败...");
        }
    }

    @RequestMapping("/deleteGoodsFromDB")
    public RestResult deleteGoodsFromDB(Long[] targetIds) {
        try {
            goodsService.deleteGoodsFromDB(targetIds);
            return new RestResult(true, "商品删除成功！");
        } catch (DeleteGoodsFromDBException e) {
            return new RestResult(false, "选择的待删除商品中存在还未被商家删除的商品, 您不能删除...");
        } catch (Exception e) {
            return new RestResult(false, "商品删除失败...");
        }
    }

    /**
     * 测试生成静态页面的接口
     */
    @RequestMapping("/testPage")
    public Boolean testPage(Long goodsId) {
        try {
            cmsService.createStaticPage(goodsId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
