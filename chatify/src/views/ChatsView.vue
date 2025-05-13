<script>
import axios from "axios";
import HeaderView from "../views/HeaderView.vue";
    export default {
        data() {
            return {
                users: [],
              username: ""
            }
        },
        methods: {
            async addChat(evt) {
              evt.preventDefault();
              let response = await axios.get("api/users", {
                params: {
                  search: this.username
                },
                withCredentials: true
              });
              this.users = response.data.users;
            },
            goChat(Userid) {
                this.$router.push({ name: 'chat', params: { id: Userid } });

            },
            async loadData() {
                try {
                    let response = await axios.get("/api/users", {
                        withCredentials: true
                    });
                    console.log(response);
                    this.users = response.data.users;
                    console.log(this.users)

                } catch(e) {
                    //this.$router.push({name: "login"});
                    console.log(e);
                }
            }
        },
        components: {
            HeaderView,
        },
        mounted() {
            this.loadData();
        }
    }       
</script>
<template>
<header-view></header-view>
<div class="content">
    <div class="container">
        <form class="chats__form">
            <input v-model="username" type="text" name="search" placeholder="кому написать?">
            <button @click="addChat">Найти</button>
        </form>
        <div class="chats__title">Чаты</div>
        <ul class="chats__list">
            <li v-for="(item, index) in users"class="chat">
                <a @click="goChat(item.id)" href="#">
                    <div class="chat__img">
                        <img src="../assets/profile.png" alt="">
                    </div>
                    <div class="chat__name">{{item.username}}</div>
                </a>
            </li>
        </ul>
    </div>
</div>
</template>
<style scoped>
.content {
    height: 93vh;
    padding: 1.5vh 0;
}
.container {
    max-width: 60%;
    margin: 0 auto;
    height: 90vh;
    background: rgb(239,239,239);
background: radial-gradient(circle, rgba(239,239,239,1) 0%, rgba(255,255,255,1) 50%, rgba(246,246,246,1) 100%);
    border-radius: 50px;
    padding: 50px 0 20px;
    display: flex;
    flex-direction: column;
}

.chats__form {
    display: flex;
    border: 2px solid #387f92;
    margin-bottom: 20px;
}

.chats__form input {
    flex: 1 1 auto;
    font-size: 24px;
    line-height: 2;
    padding-left: 20px;
    color: #000;
    border: 0;
}

.chats__form input:focus {
    outline: none;
}

.chats__form button {
    border: 0;
    padding: 0 40px;
    font-size: 24px;
    line-height: 2;
    color: #fff;
    background: #387f92;
    transition-duration: 100ms;
}

.chats__form button:hover {
    color: #387f92;
    background: #fff;
}

.chats__title {
    font-size: 40px;
    text-align: center;
    margin-bottom: 40px;
}

.chats__list {
    list-style-type: none;
    padding: 0 20px;
    overflow-y: scroll;
}

.chats__list::-webkit-scrollbar {
    background: transparent;
}

.chats__list::-webkit-scrollbar-thumb {
    background: rgba(56,127,146,0.2);
    width: 20px;
    border-radius: 10px;
}

.chat a{
    display: flex;
    background: #ccc;
    align-items: center;
    border-radius: 20px;
    padding: 10px 20px; 
    text-decoration: none;
    margin-bottom: 20px;
    transition-duration: 100ms;
}

.chat a:hover {
    background: #387f92;
}

.chat a:hover .chat__name {
    color: #fff;
}

.chat__img {
    width: 60px;
    height: 60px;
    margin-right: 20px;
}

.chat__img img {
    width: 100%;
    height: 100%;
}

.chat__name {
    font-size: 24px;
    line-height: 2;
    text-decoration: none;
    color: #000;
}
</style>