new Vue({
    el: "#app",
    data: {
        highLevelCatList: [],
        middleLevelCatList: [],
        lowLevelCatList: [],
        level: 1,
        highLevelSelectedId: -1,
        middleLevelSelectedId: -1,
        lowLevelSelectedId: -1,
        typeId: -1,
        brandList: [],
        selectedBrandId: -1,
        currentImageObj: {
            color: '',
            url: '',
        },
        imgList: [],
        isEnableSpec: "1",
        specList: [],
        selectedSpecList: [],
        rowList: [],
        goodsEntity: {
            goods: {},
            goodsDesc: {},
            itemList: {},
        }
    },

    watch: {
        /**
         * 监听typeId的变化, 用以根据typeId, 加载Brand和Specification
         */
        typeId: function (newValue, oldValue) {
            // 加载Brand
            this.brandList = [];
            this.selectedBrandId = -1;
            let _this = this;
            if (newValue !== -1 && this.level === 3) {
                axios.post('/template/getTemplateById.do?id=' + newValue).then(function (response) {
                    _this.brandList = JSON.parse(response.data.brandIds);
                    if (_this.goodsEntity.goods.brandId !== null) {
                        _this.selectedBrandId = _this.goodsEntity.goods.brandId;
                    }
                }).catch(function (reason) {
                    console.log(reason);
                });
            }

            // 加载Specification和相应的SpecificationOption
            this.specList = [];
            if (newValue !== -1 && this.level === 3) {
                axios.post('/template/getSpecByTemplateId.do?id=' + newValue).then(function (response) {
                    _this.specList = response.data;
                }).catch(function (reason) {
                    console.log(reason);
                });
            }
        }
    },

    created: function () {
        this.getItemCatListByParentId(0);
    },

    mounted: function () {
        let id = this.getQueryString("id");
        if (id != null) {
            let _this = this;
            axios.get('/goods/getGoodsEntity.do?id=' + id).then(function (response) {
                _this.goodsEntity.goods = response.data.goods;
                _this.goodsEntity.goodsDesc = response.data.goodsDesc;
                UE.getEditor('editor').ready(function () {
                    UE.getEditor('editor').setContent(response.data.goodsDesc.introduction);
                });
                _this.highLevelSelectedId = response.data.goods.category1Id;
                if (response.data.goods.category1Id >= 0) {
                    _this.level = 2;
                    axios.post('/itemCat/getItemCatListByParentId.do?parentId='
                        + response.data.goods.category1Id).then(function (response2) {
                        _this.middleLevelCatList = response2.data;
                    }).catch(function (reason2) {
                        console.log(reason2);
                    });
                    _this.middleLevelSelectedId = response.data.goods.category2Id;
                }
                if (response.data.goods.category2Id >= 0) {
                    _this.level = 3;
                    axios.post('/itemCat/getItemCatListByParentId.do?parentId='
                        + response.data.goods.category2Id).then(function (response2) {
                        _this.lowLevelCatList = response2.data;
                    }).catch(function (reason2) {
                        console.log(reason2);
                    });
                    _this.lowLevelSelectedId = response.data.goods.category3Id;
                }
                _this.typeId = response.data.goods.typeTemplateId;
                _this.imgList = JSON.parse(response.data.goodsDesc.itemImages);
                _this.selectedSpecList = JSON.parse(response.data.goodsDesc.specificationItems);
                _this.rowList = response.data.itemList;
                _this.rowList.forEach(row => {
                    row.spec = JSON.parse(row.spec);
                });
                _this.isEnableSpec = response.data.goods.isEnableSpec;
            }).catch(function (reason) {
                console.log(reason);
            });
        }
    },

    methods: {
        // parentId为0代表顶级类目
        getItemCatListByParentId: function (parentId) {
            let _this = this;
            axios.post('/itemCat/getItemCatListByParentId.do?parentId=' + parentId).then(function (response) {
                if (_this.level === 1) {
                    _this.highLevelCatList = response.data;
                }
                if (_this.level === 2) {
                    _this.middleLevelCatList = response.data;
                }
                if (_this.level === 3) {
                    _this.lowLevelCatList = response.data;
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getChildCatList: function (level) {
            if (level === 1) {
                this.middleLevelSelectedId = -1;
                this.middleLevelCatList = [];
                this.lowLevelSelectedId = -1;
                this.lowLevelCatList = [];
                this.selectedBrandId = -1;
                this.brandList = [];
                this.typeId = -1;
                this.level = level + 1;
                this.getItemCatListByParentId(this.highLevelSelectedId);
            }
            if (level === 2) {
                this.lowLevelSelectedId = -1;
                this.lowLevelCatList = [];
                this.selectedBrandId = -1;
                this.brandList = [];
                this.typeId = -1;
                this.level = level + 1;
                this.getItemCatListByParentId(this.middleLevelSelectedId);
            }
            if (level === 3) {
                let _this = this;
                axios.post('/itemCat/getItemCatById.do?id=' + this.lowLevelSelectedId).then(function (response) {
                    _this.typeId = response.data.typeId;
                }).catch(function (reason) {
                    console.log(reason);
                });
            }
        },

        uploadFile: function () {
            let formData = new FormData();
            /*
             * 此处的"fileUploader"是上传文件的input框的id, 在Vue中, 可以直接通过"id."的形式获取dom元素的属性,
             * 比如, <input type="text" id="test">, 可以通过"test.value"获取到输入框的值;
             * "files"也是input标签的一个属性(type="file"), 它是个数组, 存储着用户上传的文件;
             */
            formData.append("file", fileUploader.files[0]);
            let instance = axios.create({
                withCredentials: true
            });
            let _this = this;
            instance.post('/file/uploadFile.do', formData).then(function (response) {
                if (response.data.success) {
                    _this.currentImageObj.url = response.data.message;
                } else {
                    alert(response.data.message);
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveImg: function () {
            if (this.currentImageObj.color === '' || this.currentImageObj.url === '') {
                alert('未输入颜色或未上传图片！');
                return;
            }
            this.imgList.push({
                color: this.currentImageObj.color,
                url: this.currentImageObj.url
            });
            this.closeModal();
        },

        closeModal: function () {
            this.currentImageObj = {
                color: '',
                url: '',
            };
            fileUploader.value = '';
        },

        deleteFile: function (url, index) {
            let instance = axios.create({
                withCredentials: true
            });
            let _this = this;
            instance.post('/file/deleteFile.do?url=' + url).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.imgList.splice(index, 1);
                } else {
                    alert(response.data.message);
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        updateSelectedSpecList: function (event, spec_name, optionName) {
            let searched = this.searchValue(this.selectedSpecList, "spec_name", spec_name);
            if (searched !== null) {
                if (event.target.checked) {  // 选中
                    searched["options"].push(optionName);
                } else {  // 取消选中
                    let index = searched["options"].indexOf(optionName);
                    searched["options"].splice(index, 1);
                    if (searched["options"].length === 0) {
                        let idx = this.selectedSpecList.indexOf(searched);
                        this.selectedSpecList.splice(idx, 1);
                    }
                }
            } else {
                this.selectedSpecList.push({
                    "spec_name": spec_name,
                    "options": [optionName],
                });
            }
            this.updateRowList();
        },

        searchValue: function (list, key, value) {
            let foundItem = null;
            list.forEach(item => {
                if (item[key] === value) {
                    foundItem = item;
                }
            });
            return foundItem;
        },

        updateRowList: function () {
            if (this.selectedSpecList.length === 0) {
                this.rowList = [];
                return;
            }
            let rowList = [
                {spec: {}, price: 0, num: 999, status: '0', isDefault: '0'},
            ];
            let newRowList = [];
            this.selectedSpecList.forEach(item => {
                let spec_name = item.spec_name;
                let options = item.options;
                rowList.forEach(oldRow => {
                    options.forEach(opt => {
                        let newRow = JSON.parse(JSON.stringify(oldRow));
                        newRow.spec[spec_name] = opt;
                        newRowList.push(newRow);
                    });
                });
                rowList = newRowList;
                newRowList = [];
            });
            this.rowList = rowList;
        },

        saveGoods: function () {
            if (this.lowLevelSelectedId === null || this.lowLevelSelectedId === -1) {
                alert('尚未选择三级类目！');
                return;
            }
            if (this.selectedBrandId === null || this.selectedBrandId === -1) {
                alert('尚未选择品牌！');
                return;
            }
            if (this.goodsEntity.goods.goodsName === null || this.goodsEntity.goods.goodsName === '') {
                alert('尚未输入产品名称！');
                return;
            }
            this.goodsEntity.goods.category1Id = this.highLevelSelectedId;
            this.goodsEntity.goods.category2Id = this.middleLevelSelectedId;
            this.goodsEntity.goods.category3Id = this.lowLevelSelectedId;
            this.goodsEntity.goods.typeTemplateId = this.typeId;
            this.goodsEntity.goods.brandId = this.selectedBrandId;
            this.goodsEntity.goods.isEnableSpec = this.isEnableSpec;
            this.goodsEntity.goodsDesc.itemImages = this.imgList;
            this.goodsEntity.goodsDesc.specificationItems = this.selectedSpecList;
            this.goodsEntity.goodsDesc.introduction = UE.getEditor('editor').getContent();
            this.goodsEntity.itemList = this.rowList;

            let url;
            if (this.getQueryString("id") === null) {
                url = '/goods/addGoods.do';
            } else {
                url = '/goods/updateGoods.do';
            }

            axios.post(url, this.goodsEntity).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    location.href = '/admin/goods.html';
                } else {
                    alert(response.data.message);
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getQueryString: function (name) {
            let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            // window的loaction对象的search属性得到的是url中的query部分(即"?"后面的部分)
            let r = window.location.search.substr(1).match(reg);
            if (r != null) {
                return (r[2]);
            }
            return null;
        },

        checkSelectState: function (spec_name, optionName) {
            let found = false;
            let searched = this.searchValue(this.selectedSpecList, "spec_name", spec_name);
            if (searched != null) {
                searched["options"].forEach(opt => {
                    if (opt === optionName) {
                        found = true;
                    }
                });
            }
            return found;
        },
    },
});
