new Vue({
    el: '#app',
    data: {
        contentList: [],
    },
    created: function () {
        this.getContentByCategoryId(1);  // 1: 首页轮播广告(数据库中)
    },
    methods: {
        getContentByCategoryId: function (categoryId) {
            let _this = this;
            axios.post('/content/getContentByCategoryId.do?categoryId=' + categoryId).then(function (response) {
                _this.contentList[categoryId] = response.data;

                /*
                 * 由于我们是将从服务器获取到的数据放入contentList的下标为1的位置, contentList[1]原本是不存在的,
                 * 相当于我们给contentList动态地加载了一个属性(而不是修改属性的值), 这个新添加的属性是不会更新到页面上的,
                 * 我们需要主动地告诉页面我们添加的属性以及它的值, 页面才会更新; 下面这句代码就是在做这个;
                 * Vue.set()用法参考: https://blog.csdn.net/qq_30455841/article/details/78666571
                 */
                Vue.set(_this.contentList, categoryId, _this.contentList[categoryId]);
            }).catch(function (reason) {
                console.log(reason);
            });
        },
    }
});