new Vue({
    el: "#app",
    data: {
        userEntity: {
            username: '',
            password: '',
            phone: '',
            smscode: ''
        },
        password2: ''
    },
    methods: {
        sendCode: function () {
            if (this.userEntity.phone === null || this.userEntity.phone === "") {
                alert("手机号不能为空...");
                return;
            }
            axios.get('/user/sendCode.do?phone=' + this.userEntity.phone)
                .then(function (response) {
                    alert(response.data.message);
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        regist: function () {
            if (this.userEntity.phone === null || this.userEntity.phone === "") {
                alert("请填写手机号码");
                return;
            }
            // 比较两次输入的密码是否一致
            if (this.userEntity.password !== this.password2) {
                alert("两次输入密码不一致，请重新输入");
                return;
            }
            axios.post('/user/addUser.do?smscode=' + this.userEntity.smscode, this.userEntity)
                .then(function (response) {
                    // 获取服务端响应的结果
                    if (response.data.success) {
                        location.href = "login.html";
                    } else {
                        alert(response.data.message);
                    }
                }).catch(function (reason) {
                console.log(reason);
            });
        }
    },
});
