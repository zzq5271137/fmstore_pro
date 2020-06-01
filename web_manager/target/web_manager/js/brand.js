new Vue({
    el: '#app',
    data: {
        /*
         * 分页相关实体
         */
        brandList: [],  // 存放所有品牌信息的数组
        maxPage: 9,  // 最大的页码数
        pageSize: 10,  // 一页的大小
        page: 1,  // 当前页
        total: 0,  // 总记录数

        /*
         * 搜索框相关实体
         */
        brandSearch: {  // 搜索框中的信息
            name: '',
            firstChar: '',
        },

        /*
         * 添加/修改浮窗相关实体
         */
        brand: {  // 添加/修改的品牌信息
            name: '',
            firstChar: '',
        },

        /*
         * 删除复选框相关实体
         */
        brandsSelected: [],  // 记录品牌是否被选中
        selectStates: {},
    },

    /*
     * Vue对象生成之后(数据加载到页面之前)自动调用的钩子方法
     */
    created: function () {
        this.getBrandPage(1);
    },

    methods: {
        getBrandPage: function (page) {
            this.page = page;
            let _this = this;
            // 这里是可以直接使用this的, 因为还没有进入axios对象的方法体里面, 这里只是给get()传入的参数
            axios.post('/brand/getBrandPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.brandSearch).then(function (response) {
                // 如果直接使用this, 则代表着当前的axios对象
                _this.brandList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getBrandById: function (id) {
            let _this = this;
            axios.get('/brand/getBrandById.do', {
                params: {
                    id: id,
                }
            }).then(function (response) {
                _this.brand = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveBrand: function () {
            if (this.brand.firstChar.length > 1) {
                alert('品牌首字母只能为一个字符！');
                return;
            }
            let _this = this;
            let url;
            if (this.brand.id != null) {
                url = '/brand/updateBrand.do';
            } else {
                url = '/brand/addBrand.do';
            }
            /*
             * 这里的请求数据是一个对象, 在发送请求时, 会自动地转成JSON字符串,
             * 请求参数类型为"Content-Type: application/json", 所以Controller必须用@RequestBody接收
             */
            axios.post(url, this.brand).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getBrandPage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        deleteBrand: function () {
            if (this.brandsSelected.length === 0) {
                return;
            }
            let _this = this;
            /*
             * 不能直接发送一个数组给服务器, 需要进行转换;
             * Qs和JSON的区别:
             * 1. JSON.parse()
             *    用于从一个字符串中解析出json对象;
             *    待解析的字符串必须满足一定格式, 即单引号写在{}外, 每个属性都必须双引号, 否则会抛出异常,
             *    例如: '[{"f1":"v1","f2":"v2"}, {"f1":"v1","f2":"v2"}]'
             * 2. JSON.stringify()
             *    用于从一个对象解析出字符串(解析出的字符串满足以上的形式);
             * 3. Qs.parse()
             *    将URL解析成对象(即将一个字符串解析成对象, 字符串满足key=value并以&进行拼接的形式);
             * 4. Qs.stringify()
             *    将对象解析成URL的形式(即将一个对象解析成字符串, 解析出的字符串满足key=value并以&进行拼接的形式);
             * 参考: https://www.cnblogs.com/LSSSunshine/p/7340573.html
             *
             * 直接使用Qs.stringify(targetIds), 会将数组转成一个带有数组下标的URL, 例如: 0=19&1=20&2=21
             * 使用Qs.stringify({targetIds: targetIds}, {indices: false}这种形式,
             * 会将数组拼成一个不带数组下标的URL, 例如: targetIds=19&targetIds=20&targetIds=21,
             * 就像是直接从一个form表单中提交checkbox一样, 所以可以直接在Controller中以数组的形式接收,
             * (直接使用形参名称匹配的方式或者使用@RequestParam就可以接收, 它不是一个JSON数据, 不需要@RequestBody);
             */
            axios.post('/brand/deleteBrand.do', Qs.stringify({targetIds: _this.brandsSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getBrandPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.brandsSelected = [];
            this.selectStates = {};
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getBrandPage(1);
            }
        },

        closeModal: function () {
            this.brand = {
                name: '',
                firstChar: '',
            };
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.brandsSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.brandsSelected.splice(this.brandsSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },
    },
});
