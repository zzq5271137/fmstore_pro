new Vue({
    el: '#app',
    data: {
        /*
         * 分页相关实体
         */
        sellerList: [],  // 存放所有商家信息的数组
        maxPage: 9,  // 最大的页码数
        pageSize: 10,  // 一页的大小
        page: 1,  // 当前页
        total: 0,  // 总记录数

        /*
         * 搜索框相关实体
         */
        sellerSearch: {  // 搜索框中的信息
            name: '',
            nickName: '',
        },

        /*
         * 搜索条件, 显示已审核的商家、未审核的商家、还是全部'
         * -1: 全部商家
         * 0: 未审核的商家
         * 1: 已审核的商家
         * 2: 已驳回的商家
         * 3: 已关闭的商家
         */
        searchScale: -1,

        /*
         * 添加/修改浮窗相关实体
         */
        seller: {  // 添加/修改的商家信息
        },
    },

    /*
     * Vue对象生成之后(数据加载到页面之前)自动调用的钩子方法
     */
    created: function () {
        this.getSellerPage(1);
    },

    methods: {
        getSellerPage: function (page) {
            this.page = page;
            this.sellerSearch.status = this.searchScale;
            let _this = this;
            axios.post('/seller/getSellerPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.sellerSearch).then(function (response) {
                _this.sellerList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getSellerById: function (sellerId) {
            let _this = this;
            axios.get('/seller/getSellerById.do', {
                params: {
                    sellerId: sellerId,
                }
            }).then(function (response) {
                _this.seller = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        auditSeller: function (goal) {
            this.seller.status = goal;
            let _this = this;
            axios.post('/seller/updateSeller.do', this.seller).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getSellerPage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getSellerPage(1);
            }
        },

        closeModal: function () {
            this.seller = {};
        },

        getAll: function () {
            this.searchScale = -1;
            this.getSellerPage(1);
        },
        getUnAudited: function () {
            this.searchScale = 0;
            this.getSellerPage(1);
        },
        getAudited: function () {
            this.searchScale = 1;
            this.getSellerPage(1);
        },
        getRejected: function () {
            this.searchScale = 2;
            this.getSellerPage(1);
        },
        getClosed: function () {
            this.searchScale = 3;
            this.getSellerPage(1);
        },

        getStatusStr: function (status) {
            if (status === "0") {
                return '未审核'
            }
            if (status === "1") {
                return '已审核'
            }
            if (status === "2") {
                return '已驳回'
            }
            if (status === "3") {
                return '已关闭'
            }
        },

        show: function (label, status) {
            return label === status;
        },
    },
});
