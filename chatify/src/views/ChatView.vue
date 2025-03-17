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
      file: "",
      socket: "",
      fileType: ""
    }
  },
  methods: {
    fileChange(evt) {
      this.file = evt.target.files[0];
      if (this.file) {
        this.fileType = this.file.type;
      } else {
        this.fileType="";
      }
    },
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
        console.log(response.data);
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
    checkType(Itemtype, type) {
      if (Itemtype === type) {
        console.log(Itemtype, type, 1);
        return true;
      } else {
        console.log(Itemtype, type, 0);
        return false;
      }
    },
    async sendMessage(evt) {
      evt.preventDefault();
      if (this.message.trim() || this.file) {
        try {
          let formData = new FormData();
          formData.append("receiverId", this.recieverId);
          if (this.fileType.includes("image")) {
            formData.append("type", "photo");
          }
          if (this.fileType.includes("audio")) {
            formData.append("type", "audio");
          }

          if (this.file) {
            formData.append("file", this.file);


          }
          if (this.message.trim()) {
            formData.append("content", this.message.trim());
          }
          const response = await axios.post('/api/chat', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
            withCredentials: true,
          });
          console.log(response.data);
          this.message = "";
          this.file = "";
          document.querySelector("#file").value = "";
          this.message = "";
          this.loadData();
        } catch(e) {
          console.log(e);
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
        let response = await axios.get("/api/chat", {
          params: {
            receiverId: this.recieverId
          },
          withCredentials: true
        });
        this.user = response.data.receiver;
        this.messages = response.data.messages;
        console.log(this.user.id);
        const currentUserId = this.user.id; // или как у вас хранится ID
         // Устанавливаем WebSocket соединение
        this.socket = new WebSocket(`ws://localhost:8080/chatify2_0/websocket/chat/${currentUserId}`);
        this.socket.onopen = (event) => {
          console.log("Websocket connection opened");
        }
        this.socket.onmessage = (event) => {
          console.log(event.data);
          const message = JSON.parse(event.data);
          this.messages.push(message);

        };
        // Обрабатываем закрытие соединения
        this.socket.onclose = () => {
          console.log('WebSocket connection closed');
          // Можно добавить логику для переподключения

        };

      } catch(e) {

        console.log(e);
      }
    },
    socketSendMessage() {

    }
  },
  components: {
    HeaderView,
  },
  mounted() {
    this.loadData();
  },
  beforeDestroy() {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.close();
    }
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
            <div v-if="checkType(item.fileType,'photo')" class="message__img">
              <img  :src="item.fileUrl" alt="">
            </div>
            <div v-if="checkType(item.fileType,'audio')" class="audio">
              <audio controls :src="item.fileUrl"></audio>
            </div>
            <div class="mesasge__time">{{ item.timestamp[2] }}:{{item.timestamp[3]}}</div>
          </div>
        </div>
      </div>
      <div class="bottom">
        <form action="#" class="send-message">
          <div class="input-container">
            <div class="message-input-container">
              <textarea v-model="message" class="auto-resize-textarea" placeholder="Напишите сообщение..."></textarea>
            </div>
            <input @change = "fileChange" id="file" class="file-input" name="file" type="file">
            <label class="file" for="file">
              <img v-if="!file" src="../assets/file.png" alt="">
              <img v-if="file" src="../assets/img.png" alt="">
            </label>
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

.message__img {
  max-width: 400px;
}

.message__img img {
  width: 100%;
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

.input-container {
  flex: 1 1 auto;
  display: flex;
  align-items: center;
  padding-right: 10px;
}

.message-input-container {
  flex: 1 1 auto;
}

.file-input {
  display: none;
}

.file {
  width: 40px;
  height: 40px;
}

.file img {
  width: 100%;
  height: 100%;
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