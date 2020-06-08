new Vue({
    el: "#app",
    data: {
        searchMap: {
            keywords: '',
            page: 1,
            pageSize: 5,
            category: '',
            brand: '',
            price: '',
            spec: {}
        },
        resultMap: {
            itemList: [],
            total: 0,
            totalPages: 0,
            itemCatList: [],
            brandList: [],
            specList: [],
        },
        pageLabel: [],
        firstDot: false,
        lastDot: false,
    },
    mounted: function () {
        this.searchMap.keywords = this.getQueryString("sc");
        this.searchItems();
    },
    methods: {
        getQueryString: function (name) {
            let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            let r = window.location.search.substr(1).match(reg);
            if (r != null) {
                return (decodeURI(r[2]));
            }
            return null;
        },
        searchItems: function () {
            this.searchMap.page = parseInt(this.searchMap.page);
            let _this = this;
            axios.post('/itemsearch/search.do', this.searchMap).then(function (response) {
                _this.resultMap = response.data;
                _this.buildPages(_this);
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        searchItemsByKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.searchItems();
                window.event.returnValue = false;
            }
        },
        buildPages: function (obj) {
            obj.pageLabel = [];
            obj.firstDot = true;
            obj.lastDot = true;
            let firstPage = 1;
            let lastPage = obj.resultMap.totalPages;
            if (obj.resultMap.totalPages > 5) {  // 最多一共只显示5页页码
                if (obj.searchMap.page <= 3) {
                    lastPage = 5;
                    obj.firstDot = false;
                } else if (obj.searchMap.page >= obj.resultMap.totalPages - 2) {
                    firstPage = obj.resultMap.totalPages - 4;
                    obj.lastDot = false;
                } else {
                    firstPage = obj.searchMap.page - 2;
                    lastPage = obj.searchMap.page + 2;
                }
            } else {
                obj.firstDot = false;
                obj.lastDot = false;
            }
            for (let i = firstPage; i <= lastPage; i++) {
                obj.pageLabel.push(i);
            }
        },
        queryByPage: function (pageNum) {
            if (pageNum < 1 || pageNum > this.resultMap.totalPages) {
                this.searchMap.page = 1;
            } else {
                this.searchMap.page = pageNum;
            }
            this.searchItems();
        },
        isFirstPage: function () {
            return this.searchMap.page === 1;
        },
        isLastPage: function () {
            return this.searchMap.page === this.resultMap.totalPages;
        },
        addSearchCondition: function (key, value) {
            if (key === 'category') {
                this.searchMap.category = value;
                this.searchMap.brand = '';
                this.searchMap.price = '';
                this.searchMap.spec = {};
            } else if (key === 'brand' || key === 'price') {
                this.searchMap[key] = value;
            } else {
                Vue.set(this.searchMap.spec, key, value);
            }
            this.searchItems();
        },
        removeSearchCondition: function (key) {
            if (key === 'category') {
                this.searchMap.category = '';
                this.searchMap.brand = '';
                this.searchMap.price = '';
                this.searchMap.spec = {};
            } else if (key === 'brand' || key === 'price') {
                this.searchMap[key] = "";
            } else {
                Vue.delete(this.searchMap.spec, key);
            }
            this.searchItems();
        },
        openDetailPage: function (goodsId) {
            window.open("http://localhost:8086/" + goodsId + ".html");
        }
    },
});