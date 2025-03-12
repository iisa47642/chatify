<script>
import HeaderView from "../views/HeaderView.vue";
import axios from "axios";
    export default {
        data() {
            return {
                recieverId: this.$route.params.id,
                user: {},
                messages: [],
                message: "",
            }
        },
        methods: {
            async deleteChat() {
                try {
                    let response = await axios.delete("/api/chat", {
                        params: {
                             receiverId: this.recieverId,
                        }
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        withCredentials: true});
                    this.$router.push({name: "home"});

                } catch(e) {

                }
            },
            scrollToBottom() {
            this.$nextTick(() => {
                const messagesDiv = document.querySelector('.messages');
                console.log(messagesDiv.scrollTop);
                messagesDiv.scrollTop = 1000;
                console.log(messagesDiv.scrollTop);
                console.log("done");    
            });
            },
            async sendMessage(evt) {
                evt.preventDefault();
                if (this.message.trim()) {
                  try {
                    let response = await axios.post("/api/chat", {
                        receiverId: this.recieverId,
                        content: this.message.trim()
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        withCredentials: true});
                    this.message = "";
                    this.loadData();
                    this.nextTick();
                } catch(e) {

                }  
                }
                
            },
            checkSender(senderId) {
                if (this.recieverId != senderId) {
                    return true;
                } else {
                    return false;
                }
            },
            goChats() {
                this.$router.push("/", {});
            },
            async loadData() {
                try {
                    console.log(this.recieverId);
                    let response = await axios.get("/api/chat", {
                        params: {
                            receiverId: this.recieverId
                        },
                        withCredentials: true
                    });
                    console.log(response.data);
                    this.user = response.data.receiver;
                    this.messages = response.data.messages;
                    if (response.status == 401) {
                        this.$router.push({name: "login"});
                    }
                } catch(e) {    
                    
                    console.log(e);
                }
            }
        },
        components: {
            HeaderView,
        },
        mounted() {
            this.loadData();
            this.scrollToBottom();
        }
    }
</script>
<template>
<header-view></header-view>
<div class="content">
    <div class="container">
        <div class="top">
            <div class="top__menu">
                <div class="back-btn btn">
                    <div class="btn-icon">
                        <img src="../assets/back.png" alt="">
                    </div>
                    <div class="btn-link">
                        <a @click="goChats" href="#">Назад к чатам</a>
                    </div>
                </div>
                <div class="inter">
                    <div class="inter__name">{{user.username }}</div>
                </div>
                <div class="delete-btn btn">
                    <div class="btn-link">
                        <a @click="deleteChat" href="#">удалить чат</a>
                    </div>
                    <div class="btn-icon">
                        <img src="../assets/delete.png" alt="">
                    </div>
                </div>
            </div>
        </div>
        <div class="messages" ref="messagesContainer">
            <div v-for="(item,index) in messages" :class="{'message': true, 'your__message': checkSender(item.senderId), 'inter__message': !checkSender(item.senderId)}">
                <div class="message_body">
                    <div class="message__text">{{ item.content }}</div>
                    <div class="mesasge__time">{{ item.timestamp }}</div>
                </div>
                
            </div>
        </div>
        <div class="bottom">
            <form action="#" class="send-message">
                <div class="message-input-container">
                    <textarea v-model="message" class="auto-resize-textarea" placeholder="Напишите сообщение..."></textarea>
                </div>
                <button @click="sendMessage" type="submit">
                    ➤
                </button>
            </form>
        </div>
    
 </div>
</div>
</template>
<style scoped>
.content {
    height: 93vh;
    padding: 1.5vh 0;
}
.container {
    height: 90vh;
    max-width: 60%;
    margin: 0 auto;
    background: rgb(239,239,239);
background: radial-gradient(circle, rgba(239,239,239,1) 0%, rgba(255,255,255,1) 50%, rgba(246,246,246,1) 100%);
    border-radius: 50px;
    padding: 50px 0 20px;
    display: flex;
    flex-direction: column;
}



.top__menu {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    padding: 0 50px;
}

.inter {
    font-size: 30px;

}


.back-btn {
    border: 1px solid red;
}

.btn {
    display: flex;
    align-items: center;
    padding: 10px 20px;
    border: 2px solid #387f92;
    border-radius: 20px;
}
.btn-link a {
    text-decoration: none;
    font-size: 20px;
    font-family: "Noto Sans", sans-serif;
    color: #387f92;
}
.btn-icon {
    width: 30px;
    height: 30px;
}

.back-btn {
    padding-left: 8px;
    transition-duration: 100ms;
}

.btn:hover {
    background: #387f92;
    color: #fff;
}

.btn:hover a {
    color: #fff;
}

.btn-icon img {
    width: 100%;
    height: 100%;
}

.delete-btn {
    padding-right: 10px;
    border: 2px solid red;
}

.delete-btn a {
    color: red;
}

.delete-btn:hover {
    background: red;
}
.delete-btn .btn-link {
    padding-right: 10px;
}

.messages {
    padding: 40px 0;
    overflow-y: scroll;
    padding: 0 33px 0 50px;
    flex: 0 1 80%;
    margin-bottom: 20px;
}

.messages::-webkit-scrollbar {
    background: transparent;
}

.messages::-webkit-scrollbar-thumb {
    background: rgba(56,127,146,0.2);
    width: 20px;
    border-radius: 10px;
}

.message {
    display: flex;
    margin-bottom: 40px;
}

.message_body {
    max-width: 60%;
    padding: 15px;
    color: #fff;
}

.your__message {
    justify-content: flex-end;
    border-radius: 20px 20px 0 20px;
}

.your__message .message_body{
    border-radius: 20px 20px 0 20px;
    background: #387f92;
}

.inter__message {
    justify-content: flex-start;
    
}

.inter__message .message_body {
    border-radius: 20px 20px 20px 0;
    background: #ccc;
    color: #000;
}

.send-message {
    padding: 0 0;
}

.message-input-container {
  flex: 1 1 auto;
  display: flex;
}

.auto-resize-textarea {
  width: 100%;
  background: transparent;
  color: #000;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  border: 0;
  padding: 10px 0;
}

.auto-resize-textarea:focus {
    outline: none;
}
.auto-resize-textarea::-webkit-scrollbar {
    background: transparent;
}

.send-message {
    display: flex;
    align-items: center;
    background: #ccc;
    max-width: 60%;
    margin: 0 auto;
    padding: 0 20px;
    border-radius: 20px;
}

.send-message button {
    background: #387f92;
    width: 40px;
    line-height: 40px;
    font-size: 20px;
    padding: 0;
    border: 0;
    color: #fff;
    border-radius: 20px;
}

.send-message button:hover {
    background: #fff;
    color: #387f92;
}

</style>