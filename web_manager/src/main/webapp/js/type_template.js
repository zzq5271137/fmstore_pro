// 初始化下拉列表组件
Vue.component('v-select', VueSelect.VueSelect);

new Vue({
    el: '#app',
    data: {
        /*
         * 分页相关实体
         */
        templateList: [],  // 存放所有模板信息的数组
        maxPage: 9,  // 最大的页码数
        pageSize: 10,  // 一页的大小
        page: 1,  // 当前页
        total: 0,  // 总记录数

        /*
         * 搜索框相关实体
         */
        templateSearch: {  // 搜索框中的信息
            name: '',
        },

        /*
         * 添加/修改浮窗相关实体
         */
        template: {  // 添加/修改的模板信息
            name: '',  // 模板名称/商品类型
            brandIds: '',  // 关联品牌
            specIds: '',  // 关联规格
            customAttributeItems: '',  // 扩展属性
        },
        customAttributeItemsList: [],  // template中customAttributeItems转成的数组的形式
        /* 下拉列表相关实体 */
        placeholder: '可以进行多选',
        brandsOptions: [],  // 品牌选择下拉列表内容
        selectedBrandsObjList: [],  // 选中的品牌
        // selectedBrandsIdList: [],
        specOptions: [],  // 规格选择下拉列表内容
        selectedSpecObjList: [],  // 选中的规格

        /*
         * 删除复选框相关实体
         */
        templateSelected: [],  // 记录模板是否被选中
        selectStates: {},
    },

    /*
     * Vue对象生成之后(数据加载到页面之前)自动调用的钩子方法
     */
    created: function () {
        this.getTemplatePage(1);
        this.loadSelectionListData();
    },

    methods: {
        /*
         * 加载下拉列表数据
         */
        loadSelectionListData: function () {
            let _this = this;
            axios.get('/brand/getSelectionListData.do').then(function (response) {
                _this.brandsOptions = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
            axios.get('/spec/getSelectionListData.do').then(function (response) {
                _this.specOptions = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getTemplatePage: function (page) {
            this.page = page;
            let _this = this;
            axios.post('/template/getTemplatePage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.templateSearch).then(function (response) {
                _this.templateList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getTemplateById: function (id) {
            let _this = this;
            axios.get('/template/getTemplateById.do', {
                params: {
                    id: id,
                }
            }).then(function (response) {
                _this.template = response.data;
                _this.selectedBrandsObjList = JSON.parse(_this.template.brandIds);
                _this.selectedSpecObjList = JSON.parse(_this.template.specIds);
                _this.customAttributeItemsList = JSON.parse(_this.template.customAttributeItems).map(
                    function (str) {
                        return {name: str};
                    }
                );
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveTemplate: function () {
            this.template.customAttributeItems = this.customAttributeItemsList.map(
                function (obj) {
                    return obj.name;
                }
            );
            this.template.brandIds = this.selectedBrandsObjList;
            this.template.specIds = this.selectedSpecObjList;
            let _this = this;
            let url;
            if (this.template.id != null) {
                url = '/template/updateTemplate.do';
            } else {
                url = '/template/addTemplate.do';
            }
            axios.post(url, this.template).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getTemplatePage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        deleteTemplate: function () {
            if (this.templateSelected.length === 0) {
                return;
            }
            let _this = this;
            axios.post('/template/deleteTemplate.do', Qs.stringify({targetIds: _this.templateSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getTemplatePage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.templateSelected = [];
            this.selectStates = {};
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getTemplatePage(1);
            }
        },

        closeModal: function () {
            this.template = {
                name: '',
                brandIds: '',
                specIds: '',
                customAttributeItems: '',
            };
            this.customAttributeItemsList = [];
            this.selectedBrandsObjList = [];
            this.selectedSpecObjList = [];
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.templateSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.templateSelected.splice(this.templateSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },

        /*
         * 处理json字符串的方法, 返回key对应的value
         */
        jsonToStr: function (jsonStr, key) {
            let jsonObjList = JSON.parse(jsonStr);
            let value = '';
            for (let i = 0; i < jsonObjList.length; i++) {
                value += jsonObjList[i][key];
                if (i < jsonObjList.length - 1) {
                    value += ', ';
                }
            }
            return value;
        },

        strListToStr: function (str) {
            let strList = JSON.parse(str);
            let value = '';
            for (let i = 0; i < strList.length; i++) {
                value += strList[i];
                if (i < strList.length - 1) {
                    value += ', ';
                }
            }
            return value;
        },

        /*
         * 点击下拉列表选项时调用的方法
         */
        brandSelected: function (values) {
            /*
             * 根据业务需求, 可能只需要选择项的某一个属性:
             * this.selectedBrandsIdList = values.map(function (obj) {
             *     return obj.id;
             * });
             * console.log(this.selectedBrandsIdList);
             */
            console.log(this.selectedBrandsObjList);
        },
        specSelected: function (values) {
            console.log(this.selectedSpecObjList);
        },

        addRow: function () {
            this.customAttributeItemsList.push({});
        },

        deleteRow: function (index) {
            this.customAttributeItemsList.splice(index, 1);
        },
    },
});
