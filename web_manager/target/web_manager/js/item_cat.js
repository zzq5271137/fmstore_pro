new Vue({
    el: '#app',
    data: {
        maxPage: 9,
        pageSize: 10,
        page: 1,
        total: 0,
        itemCatList: [],
        level: 1,
        highLevelEntity: {},
        middleLevelEntity: {},
        showHigh: false,
        showMiddle: true,
    },

    created: function () {
        this.getItemCatPageByParentId(1, 0);
    },

    methods: {
        // parentId为0代表顶级类目
        getItemCatPageByParentId: function (page, parentId) {
            this.page = page;
            let _this = this;
            axios.post('/itemCat/getItemCatPageByParentId.do?page=' + this.page + '&pageSize=' + this.pageSize + '&parentId=' + parentId)
                .then(function (response) {
                    _this.itemCatList = response.data.rows;
                    _this.total = response.data.total;
                }).catch(function (reason) {
                console.log(reason);
            });
        },

        getPage: function (page) {
            this.getItemCatPageByParentId(page, 0);
        },

        getChildrenCat: function (item) {
            if (this.level === 1) {
                this.highLevelEntity = {};
                this.middleLevelEntity = {};
                this.showHigh = false;
                this.showMiddle = false;
            }
            if (this.level === 2) {
                this.highLevelEntity = item;
                this.middleLevelEntity = {};
                this.showHigh = true;
                this.showMiddle = false;
            }
            if (this.level === 3) {
                this.middleLevelEntity = item;
                this.showHigh = true;
                this.showMiddle = true;
            }
            this.getItemCatPageByParentId(1, item.id);
        },

        setLevel: function (level) {
            this.level = level;
        },
    },
})
;
