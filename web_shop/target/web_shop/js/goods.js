new Vue({
    el: '#app',
    data: {
        goodsList: [],
        maxPage: 9,
        pageSize: 10,
        page: 1,
        total: 0,
        goodsSearch: {},
        allItemCat: {},
        auditStatuses: {
            "0": "未申请",
            "1": "申请中",
            "2": "审核通过",
            "3": "已驳回",
        },
        goodsSelected: [],
        selectStates: {},
    },
    created: function () {
        this.loadData();
    },
    methods: {
        loadData: function () {
            let _this = this;
            axios.post('/itemCat/getAllItemCat.do').then(function (response) {
                let allItemCatList = response.data;
                allItemCatList.forEach(itemCat => {
                    _this.allItemCat[itemCat.id] = itemCat.name;
                });
                _this.getGoodsPage(1);
            }).catch(function (reason) {
                console.log(reason);
            })
        },

        getGoodsPage: function (page) {
            this.page = page;
            let _this = this;
            axios.post('/goods/getGoodsPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.goodsSearch).then(function (response) {
                _this.goodsList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        deleteGoods: function () {
            if (this.goodsSelected.length === 0) {
                return;
            }
            let _this = this;
            axios.post('/goods/deleteGoods.do', Qs.stringify({targetIds: _this.goodsSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getGoodsPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.goodsSelected = [];
            this.selectStates = {};
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getGoodsPage(1);
            }
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.goodsSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.goodsSelected.splice(this.goodsSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },
    },
});