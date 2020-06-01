new Vue({
    el: '#app',
    data: {
        contentCatList: [],
        maxPage: 9,
        pageSize: 10,
        page: 1,
        total: 0,
        contentCatSearch: {
            name: '',
        },
        contentCat: {
            name: '',
        },
        contentCatSelected: [],
        selectStates: {},
    },

    created: function () {
        this.getContentCatPage(1);
    },

    methods: {
        getContentCatPage: function (page) {
            this.page = page;
            let _this = this;
            axios.post('/contentCat/getContentCatPage.do?page=' + this.page + '&pageSize=' + this.pageSize,
                this.contentCatSearch).then(function (response) {
                _this.contentCatList = response.data.rows;
                _this.total = response.data.total;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        getContentCatById: function (id) {
            let _this = this;
            axios.get('/contentCat/getContentCatById.do', {
                params: {
                    id: id,
                }
            }).then(function (response) {
                _this.contentCat = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        saveContentCat: function () {
            if (this.contentCat.name === null || this.contentCat.name === '') {
                alert('请输入广告分类名称');
                return;
            }
            let _this = this;
            let url;
            if (this.contentCat.id != null) {
                url = '/contentCat/updateContentCat.do';
            } else {
                url = '/contentCat/addContentCat.do';
            }
            axios.post(url, this.contentCat).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.getContentCatPage(1);
                } else {
                    alert(response.data.message);
                }
            });
            this.closeModal();
        },

        deleteContentCat: function () {
            if (this.contentCatSelected.length === 0) {
                return;
            }
            let _this = this;
            axios.post('/contentCat/deleteContentCat.do',
                Qs.stringify({targetIds: _this.contentCatSelected}, {indices: false}))
                .then(function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                        _this.getContentCatPage(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            this.contentCatSelected = [];
            this.selectStates = {};
        },

        closeModal: function () {
            this.contentCat = {
                name: '',
            };
        },

        onSearchKeydown: function (event) {
            if (event.keyCode === 13 || event.keyCode === 108) {
                this.getContentCatPage(1);
            }
        },

        clickCheckBox: function (event, id) {
            if (event.target.checked) {
                this.contentCatSelected.push(id);
                this.selectStates[id] = true;
            } else {
                this.contentCatSelected.splice(this.contentCatSelected.indexOf(id), 1);
                this.selectStates[id] = false;
            }
        },

    },
});
