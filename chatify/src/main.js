import "./assets/base.css"
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios"
axios.defaults.baseURL = 'http://localhost:8080/chatify2_0/';
const app = createApp(App)

app.use(router)

app.mount('#app')

