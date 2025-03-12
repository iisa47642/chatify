<script>
import axios from "axios";
    export default {
        data() {
            return {

            }
        },
        methods: {
            logout() {
                try {
                    let response = axios.get("api/logout",{
                        withCredentials: true});
                    console.log(response.data);
                    this.$router.push({name: "login"});
                } catch(e) {
                    console.log(e);
                }
            }
        }
    }
</script>
<template>
    <div class="header__menu">
        <div class="header__logo">
            <img src="../assets/logo.png" alt="">
        </div>
        <a @click="logout" href="# "class="header__logout">
            <div  class="logout__text">выйти</div>
            <div class="logout__img">
                <img src="../assets/logout.png" alt="">
            </div>
        </a>
    </div>
</template>
<style scoped>
    .header__menu {
        position: sticky;
        top: 0;
        background: #ccc;
        height: 7vh;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 20px;
    }

    .header__logo {
        width: 75px;
        height: 50px;
    }

    .header__logo img {
        width: 100%;
        height: 100%;
    }

    .header__logout {
        display: flex;
        align-items: center;
        text-decoration: none;
        color: #000;
        transition-duration: 100ms;
    }

    .header__logout:hover {
        text-decoration: underline;
    }

    .logout__text {
        color: #000;
        font-size: 20px;
        line-height: 2;
        padding-right: 10px;
    }
    .logout__img {
        width: 50px;
        height: 50px;
    }

    .logout__img img {
        width: 100%;
        height: 100%;
    }

</style>