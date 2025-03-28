<script>
import HeaderView from "../views/HeaderView.vue";
import axios from "axios";
export default {
  data() {
    return {
      recieverId: this.$route.params.id,
      receiver: {},
      user: {},
      messages: [

      ],
      message: "",
      file: "",
      socket: "",
      fileType: "",
      mediaRecorder: null,
      audioChunks: [],
      audioMessage: "",
      stream: null,
      isRecording: false,
      mp3voice: "",
    }
  },
  methods: {
    audio_ended(index, item) {
      let audio = this.$refs[`audio-${index}`][0];
      item.isPlaying = false;
    },
    updateTime(index) {
      let audio = this.$refs[`audio-${index}`][0];
      let input = this.$refs[`input-${index}`][0];
      let time = this.$refs[`current-time-${index}`][0];
      time.textContent = this.formatTime(audio.currentTime);
      input.value = Math.floor((audio.currentTime * 100)/audio.duration);
      const value = (input.value - input.min) / (input.max - input.min) * 100;
      input.style.background = `linear-gradient(to right, #ffffff ${value}%, #ddd ${value}%)`;
    },
    formatTime(time) {
      const minutes = Math.floor(time / 60);
      const seconds = Math.floor(time % 60);
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    },
    setDuration(index) {
      let audio = this.$refs[`audio-${index}`][0];
      let duration = this.$refs[`duration-${index}`][0];
      let input = this.$refs[`input-${index}`][0];
      duration.textContent = this.formatTime(audio.duration);
    },
    AudioSkip(index) {
      let audio = this.$refs[`audio-${index}`][0];
      let input = this.$refs[`input-${index}`][0];
      const value = (input.value - input.min) / (input.max - input.min) * 100;
      input.style.background = `linear-gradient(to right, #ffffff ${value}%, #ddd ${value}%)`;
      audio.currentTime = (input.value*audio.duration)/100;
    },
            playAudio(index, item) {
                let audio = this.$refs[`audio-${index}`][0];
                if (audio.paused) {
                    item.isPlaying = true;
                    audio.play();
                } else {
                    item.isPlaying = false;
                    audio.pause();
                }
            },
            async startRecording() {
                try {
                this.fileType = "audio";
                this.isRecording = true;
                this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
                this.mediaRecorder = new MediaRecorder(this.stream);
                this.audioChunks = [];
                
                this.mediaRecorder.addEventListener('dataavailable', event => {
                    this.audioChunks.push(event.data);
                });
                
                this.mediaRecorder.start();
                } catch (error) {
                console.error('Ошибка при записи аудио:', error);
                }
            },
            
            stopRecording() {
                    if (!this.mediaRecorder) return;
                    this.isRecording =false;
                    this.mediaRecorder.addEventListener('stop', () => {
                    const audioBlob = new Blob(this.audioChunks, { type: 'audio/mp3' });
                    const audioUrl = URL.createObjectURL(audioBlob);
                    this.audioMessage = audioUrl;
                    this.mp3voice = new File([audioBlob], "voice-message");
                    // Здесь можно отправить audioBlob на сервер
                    // this.sendAudioMessage(audioBlob);
                      console.log(this.mp3voice)
                      this.sendAudio();
                      this.fileType = "";
                    // Останавливаем все треки после записи
                    this.stream.getTracks().forEach(track => track.stop());
                    });
                    this.mediaRecorder.stop();

            
                },
            async sendAudio() {
                const formData = new FormData();
                formData.append("receiverId", this.recieverId);
                formData.append("type", "audio");
                if (this.mp3voice) {
                  console.log("voice")
                  console.log(this.mp3voice)
                    formData.append("file", this.mp3voice);
                }
                const response = await axios.post('/api/chat', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                        withCredentials: true,
                    });
                console.log(response.data);
              this.loadData();
              this.mp3voice = "";
                this.audioChunks = [];
            },
    zoomImg() {
      const img = document.querySelector("#popup .popup__img");
      if (img.style.transform === 'scale(1.5)') {
        img.style.transform = 'scale(1)';
      } else {
        img.style.transform = 'scale(1.5)';
      }
    },
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
          },
          headers: {
            'Content-Type': 'application/json',
          },
          withCredentials: true
        });
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
        return true;
      } else {
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
    popup(src) {
      const popup = document.querySelector("#popup");
      popup.classList.add("visible");
      const img = document.querySelector("#popup img");
      img.src = src;
    },
    disablepopup(event) {
      if (!event.target.closest('.popup__img')) {
        document.querySelector("#popup").classList.remove('visible');
      }
    },
    async loadData() {
      try {
        let response = await axios.get("/api/chat", {
          params: {
            receiverId: this.recieverId
          },
          withCredentials: true
        });
        this.receiver = response.data.receiver;
        this.user = response.data.currentUser;
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
          this.$router.push({name: "home"});
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
  <div @click="disablepopup" id="popup">
    <div @click="zoomImg" class="popup__img">
      <img src="" alt="">
    </div>
  </div>
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
            <div class="inter__name">{{receiver.username }}</div>
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
            <div @click="popup(item.fileUrl)"v-if="checkType(item.fileType,'photo')" class="message__img">
              <img  :src="item.fileUrl" alt="">
            </div>
            <div v-if="checkType(item.fileType,'audio')" class="audio-player">
                <audio @ended="audio_ended(index,item)" @timeupdate="updateTime(index)"  @loadedmetadata="setDuration(index)" class="audio" :ref="`audio-${index}`" :src="item.fileUrl">
                        </audio>
                        <div class="controls">
                        <div class="controls__button">
                        <button @click="playAudio(index, item)" class="play-pause">
                            <img v-if="!item.isPlaying" src="../assets/play.png" alt="">
                            <img v-if="item.isPlaying" src="../assets/pause.png" alt="">
                        </button>
                        </div>
                        
                        <input :ref="`input-${index}`" @change="AudioSkip(index)" type="range" class="progress" value="0" max="100">
                        <span :ref="`current-time-${index}`" class="current-time">0:00</span> / <span :ref="`duration-${index}`" class="duration">0:00</span>
                        </div>
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
                <label  class="file" for="file">
                    <img v-if="!file" src="../assets/file.png" alt="">
                    <img v-if="file" src="../assets/img.png" alt="">
                </label>
            </div>
                
                <button v-if="message || file" class="send-btn" @click="sendMessage" type="submit">
                    ➤
                </button>
                <button v-if="!file && !message" class="voice-message" @mousedown="startRecording" @mouseup="stopRecording" :class="{recording: isRecording}">
                    <img v-if="!isRecording" src="../assets/micro.png" alt="">
                    <img v-if="isRecording" src="../assets/micro_recording.png" alt="">
                </button>
            </form>
        </div>

    </div>
  </div>
</template>
<style scoped>
.controls {
    display: flex;
    align-items: center;
    gap: 10px; 
}

.controls input[type=range] {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 8px;
  background: #ddd;
  border-radius: 5px;
  outline: none;    
}

.controls input[type=range]::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  background: #fff;
  background-color: #fff;
  border: 1px solid #ccc;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 50%;
  cursor: pointer;
}

.controls input[type=range]::-moz-range-thumb {
  width: 20px;
  height: 20px;
  background: #fff;
  background-color: #fff;
  border: 1px solid #ccc;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 50%;
  cursor: pointer;
}

.controls__button {
    display: flex;
    align-items: center;
}

.play-pause {
    width: 30px;
    height: 30px;
    border-radius: 15px;
    border: 0;
}

.play-pause img {
    width: 100%;
    height: 100%;
}
.voice-message {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    background: #387f92;
    border: 0;
}

.voice-message img {
    width: 100%;
    height: 100%;
}
.visible {
  display: flex !important;
}
#popup .popup__img {
  width: 60%;
}

.popup__img img {
  width: 100%;
}

#popup {
  position: fixed;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.5);
  left: 0;
  top: 0;
  display: none;
  justify-content: center;
  align-items: center;
  overflow-y: scroll;
}
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
  cursor: pointer;
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