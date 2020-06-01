new Vue({
    el: '#app',
    data: {
        contentList: [],
        maxPage: 9,
        pageSize: 10,
        page: 1,
        total: 0,
        contentSearch: {
            title: '',
            url: '',
        },
        allContentCatList: [],
        allContentCat: {},
        status: {
            "0": "无效",
            "1": "有效",
        },
        contentSelected: [],
        selectStates: {},
        content: {
            categoryId: -1,
            pic: '',
        }
    },

    created: function () {
        this.loadData();
    },

    methods: {
        loadData: function () {
            let _this = this;
            axios.post('/contentCat/getAllContentCat.do').then(function (response) {
                _this.allContentCatList = response.data;
                _this.allContentCatList.forEach(contentCat => {
                    _this.allContentCat[contentCat.id] = contentCat.name;
                });
                _this.getContentPage(1);
            }).catch(function (reason) {
                console.log(reason);
            })
        },

        getContentPage: function (page) {
            this.page = page;
            let _this = this;
            axios.post('/content/getContentPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.contentSearch).then(function (response) {
                _this.contentList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        updateContentStatus: function (status) {
            if (this.contentSelected.length === 0) {
                return;
            }
            let targetIds = Qs.stringify({targetIds: this.contentSelected}, {indices: false});
            let _this = this;
            axios.post('/content/updateContentStatus.do?' + targetIds + '&status=' + status)
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getContentPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.contentSelected = [];
            this.selectStates = {};
        },

        getContentById: function (id) {
            let _this = this;
            axios.get('/content/getContentById.do?id=' + id).then(function (response) {
                _this.content = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveContent: function () {
            if (this.content.categoryId === null || this.content.categoryId === -1) {
                alert('请选择广告类别');
                return;
            }
            let url;
            if (this.content.id != null) {
                url = '/content/updateContent.do';
            } else {
                url = '/content/addContent.do';
            }
            let _this = this;
            axios.post(url, this.content).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getContentPage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        deleteContent: function () {
            if (this.contentSelected.length === 0) {
                return;
            }
            let _this = this;
            axios.post('/content/deleteContent.do',
                Qs.stringify({targetIds: _this.contentSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getContentPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.contentSelected = [];
            this.selectStates = {};
        },

        closeModal: function () {
            this.content = {
                categoryId: -1,
                pic: '',
            };
            fileUploader.value = '';
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getContentPage(1);
            }
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.contentSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.contentSelected.splice(this.contentSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },

        uploadFile: function () {
            let formData = new FormData();
            formData.append('file', fileUploader.files[0]);
            let instance = axios.create({
                withCredentials: true
            });
            let _this = this;
            instance.post('/file/uploadFile.do', formData).then(function (response) {
                if (response.data.success) {
                    _this.content.pic = response.data.message;
                } else {
                    alert(response.data.message);
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },
    },
});
