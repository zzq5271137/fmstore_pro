new Vue({
    el: '#app',
    data: {
        seller: {
            name: '',
            mobile: '',
            telephone: '',
            addressDetail: '',
            linkmanName: '',
            linkmanQq: '',
            linkmanMobile: '',
            linkmanEmail: '',
            licenseNumber: '',
            taxNumber: '',
            orgNumber: '',
            legalPerson: '',
            legalPersonCardId: '',
            bankName: '',
        }
    },

    created: function () {
        this.loadData();
    },

    methods: {
        loadData: function () {
            let _this = this;
            axios.get('/seller/getSeller.do').then(function (response) {
                _this.seller = response.data;
            }).catch(function (reason) {
                console.log(reason);
            });
        },

        editOrSave: function () {
            if (add_save_btn.innerText === '编辑') {
                this.activateEdit();
                add_save_btn.innerText = '保存';
                add_save_btn.className = "btn btn-danger";
            } else {
                this.deactivateEdit();
                add_save_btn.innerText = '编辑';
                add_save_btn.className = "btn btn-primary";
            }
        },

        activateEdit: function () {
            seller_name.disabled = null;
            seller_mobile.disabled = null;
            seller_telephone.disabled = null;
            seller_addressDetail.disabled = null;
            seller_linkmanName.disabled = null;
            seller_linkmanQq.disabled = null;
            seller_linkmanMobile.disabled = null;
            seller_linkmanEmail.disabled = null;
            seller_licenseNumber.disabled = null;
            seller_taxNumber.disabled = null;
            seller_orgNumber.disabled = null;
            seller_legalPerson.disabled = null;
            seller_legalPersonCardId.disabled = null;
            seller_bankName.disabled = null;
            seller_bankUser.disabled = null;
        },

        deactivateEdit: function () {
            seller_name.disabled = "disabled";
            seller_mobile.disabled = "disabled";
            seller_telephone.disabled = "disabled";
            seller_addressDetail.disabled = "disabled";
            seller_linkmanName.disabled = "disabled";
            seller_linkmanQq.disabled = "disabled";
            seller_linkmanMobile.disabled = "disabled";
            seller_linkmanEmail.disabled = "disabled";
            seller_licenseNumber.disabled = "disabled";
            seller_taxNumber.disabled = "disabled";
            seller_orgNumber.disabled = "disabled";
            seller_legalPerson.disabled = "disabled";
            seller_legalPersonCardId.disabled = "disabled";
            seller_bankName.disabled = "disabled";
            seller_bankUser.disabled = "disabled";
        },

        updateSeller: function () {
            let _this = this;
            axios.post('/seller/updateSeller.do', this.seller).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);
                    _this.loadData();
                } else {
                    alert(response.data.message);
                }
            }).catch(function (reason) {
                console.log(reason);
            })
        }
    }
});