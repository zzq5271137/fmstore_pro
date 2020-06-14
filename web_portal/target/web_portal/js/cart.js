new Vue({
    el: "#app",
    data: {
        cartList: [],
        totalValue: {
            totalNum: 0,
            totalMoney: 0
        }
    },
    created: function() {
        this.loadCartData();
    },
    methods: {
        loadCartData: function () {
            let _this = this;
            axios.post("/cart/getCartList.do").then(function (response) {
                _this.cartList = response.data;
                _this.totalValue = _this.sum(_this.cartList);
                console.log(_this.cartList);
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
        }
    }
});