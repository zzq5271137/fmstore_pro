new Vue({
    el: '#app',
    data: {
        oldPwd: '',
        newPwd: '',
        newPwdRepeat: '',
    },
    methods: {
        changePwd: function () {
            let _this = this;
            axios.get('/seller/checkPwd.do?password=' + this.oldPwd).then(function (response) {
                if (response.data.success) {
                    if (_this.newPwd !== _this.newPwdRepeat) {
                        alert('两次新密码输入不一致...');
                    } else {
                        axios.get('/seller/changePwd.do?password=' + _this.newPwd).then(function (response) {
                            alert(response.data.message);
                            location.reload();
                        }).catch(function (reason) {
                            console.log(reason);
                        });
                    }
                } else {
                    alert('原密码输入错误...');
                }
            }).catch(function (reason) {
                console.log(reason);
            });
        },
    }
});