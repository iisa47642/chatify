<script>
import axios from "axios";
    export default {
        data() {
            return {
                username: "",
                password: "",
                error: false
            }
        },
        methods: {
            async register(evt) {
                evt.preventDefault();
                try {
                    let response = await axios.post("api/register", {
                        username: this.username,
                        password: this.password,
                        }, 
                        {
                        headers: {
                            'Content-Type': 'application/json',
                        }}
                );
                console.log(response.data);
                this.$router.push("/login", {});
                } catch(e) {
                    this.error = true;
                    console.log(e);
                }
            },
        }
    }
</script>
<template>
 <div class="container">
    <div class="login">
        <div class="login__title">Регистрация</div>
        <form action="#" class="login__form">
            <input v-model="username" type="text" name="username" placeholder="имя">
            <input v-model="password" type="password" name="password" placeholder="пароль">
            <div  v-if="error"class="form__error">такой пользователь уже есть</div>
            <button @click="register" type="submit">Зарегестрироваться</button>
        </form>
    </div>
 </div>
</template>
<style scoped>
.container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.login {
    border-radius: 20px;    
    padding: 40px;
    flex: 0 1 40%;
    background: #fff;
}

.login__title {
    font-size: 40px;
    text-transform: uppercase;
    text-align: center;
    color: #000;
    margin-bottom: 20px;
}

.login__form {
    text-align: center;
    margin-bottom: 20px;
}

.login__form input {
    display: block;
    width: 100%;
    margin-bottom: 20px;
    font-size: 20px;
    line-height: 2;
    padding-left: 10px;
    border: 2px solid #000;
}

.login__form input:focus {
    outline: 0;
}

.login__form button {
    background: #387f92;
    padding: 0 70px;
    font-size: 26px;
    line-height: 2;
    border: none;
    color: #fff;
    cursor: pointer;
    transition-duration: 100ms;
}

.login__form button:hover {
    background: #fff;
    color: #387f92;
}

.form__error {
    font-size: 18px;
    margin-bottom: 20px;
    color: red;

}

.login {
    text-align: center;
}

.login a {
    text-decoration: none;
}

</style>