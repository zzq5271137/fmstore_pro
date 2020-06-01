new Vue({
    el: '#app',
    data: {
        /*
         * 分页相关实体
         */
        specList: [],  // 存放所有规格信息的数组
        maxPage: 9,  // 最大的页码数
        pageSize: 10,  // 一页的大小
        page: 1,  // 当前页
        total: 0,  // 总记录数

        /*
         * 搜索框相关实体
         */
        specSearch: {  // 搜索框中的信息
            specName: '',
        },

        /*
         * 添加/修改浮窗相关实体
         */
        specEntity: {  // 包含了添加/修改的规格信息以及其对应的规格选项信息
            spec: {},  // 规格信息
            options: [],  // 规格选项信息(多个)
        },

        /*
         * 删除复选框相关实体
         */
        specSelected: [],  // 记录规格是否被选中
        selectStates: {},
    },

    /*
     * Vue对象生成之后(数据加载到页面之前)自动调用的钩子方法
     */
    created: function () {
        this.getSpecPage(1);
    },

    methods: {
        getSpecPage: function (page) {
            this.page = page;
            let _this = this;
            axios.post('/spec/getSpecPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.specSearch).then(function (response) {
                _this.specList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getSpecById: function (id) {
            let _this = this;
            axios.get('/spec/getSpecById.do', {
                params: {
                    id: id,
                }
            }).then(function (response) {
                _this.specEntity = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveSpec: function () {
            let _this = this;
            let url;
            if (this.specEntity.spec.id != null) {
                url = '/spec/updateSpec.do';
            } else {
                url = '/spec/addSpecification.do';
            }
            axios.post(url, this.specEntity).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getSpecPage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        deleteSpec: function () {
            if (this.specSelected.length === 0) {
                return;
            }
            let _this = this;
            axios.post('/spec/deleteSpec.do', Qs.stringify({targetIds: _this.specSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getSpecPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.specSelected = [];
            this.selectStates = {};
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getSpecPage(1);
            }
        },

        closeModal: function () {
            this.specEntity = {
                spec: {},
                options: [],
            }
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.specSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.specSelected.splice(this.specSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },

        addRow: function () {
            this.specEntity.options.push({});
        },

        deleteRow: function (index) {
            this.specEntity.options.splice(index, 1);
        },
    },
});
