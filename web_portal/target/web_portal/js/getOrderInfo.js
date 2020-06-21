new Vue({
    el: "#app",
    data: {
        addressList: [],
        address: {},
        cartList: [],
        totalValue: {
            totalNum: 0,
            totalMoney: 0
        },
        order: {
            paymentType: 1,
            address: '',
            mobile: '',
            contact: ''
        }
    },
    created: function () {
        this.loadAddress();
        this.loadCartData();
    },
    methods: {
        loadAddress: function () {
            let _this = this;
            axios.post("/address/getAddressListByLoginUser.do")
                .then(function (response) {
                    _this.addressList = response.data;
                    for (let i = 0; i < _this.addressList.length; i++) {
                        if (_this.addressList[i].isDefault === '1') {
                            _this.address = _this.addressList[i];
                            break;
                        }
                    }
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        isSeletedAddress: function (address) {
            return address === this.address;
        },
        selectAddress: function (address) {
            this.address = address;
        },
        selectPayType: function (type) {
            this.order.paymentType = type;
        },
        loadCartData: function () {
            let _this = this;
            axios.post("/cart/getCartList.do")
                .then(function (response) {
                    _this.cartList = response.data;
                    _this.totalValue = _this.sum(_this.cartList);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        sum: function (cartList) {
            let totalValue = {
                totalNum: 0,
                totalMoney: 0
            };
            for (let i = 0; i < cartList.length; i++) {
                let cart = cartList[i];
                for (let j = 0; j < cart.orderItemList.length; j++) {
                    let orderItem = cart.orderItemList[j];
                    totalValue.totalNum += orderItem.num;
                    totalValue.totalMoney += orderItem.price * orderItem.num;

                }
            }
            return totalValue;
        },
    }
});
