import { createRouter, createWebHistory } from 'vue-router'
import MainView from "../views/MainView.vue"
import ChatsView from "../views/ChatsView.vue"
import ChatView from "../views/ChatView.vue"
import LoginView from "../views/LoginView.vue"
import RegisterView from "../views/RegisterView.vue"
import HeaderView from "../views/HeaderView.vue"
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: ChatsView
    },
    {
      path: "/chat/:id",
      name: "chat",
      component: ChatView
    },
    {
      path: "/login",
      name: "login",
      component: LoginView
    },
    {
      path: "/register",
      name: "register",
      component: RegisterView
    },
    {
      path: "/header",
      name: "header",
      component: HeaderView
    }
  ]
})

export default router
