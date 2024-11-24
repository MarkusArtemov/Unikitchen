import { createRouter, createWebHistory } from "vue-router";
import HomePage from "@/views/HomePage.vue";
import LoginPage from "@/components/LoginPage.vue";
import AccountPage from "@/views/AccountPage.vue";
import DetailPage from "@/views/DetailPage.vue";
import RecipePage from "@/views/RecipePage.vue";
import RecipeCreatePage from "@/views/RecipeCreatePage.vue";
import RegisterPage from "@/components/RegisterPage.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: HomePage,
  },
  {
    path: "/login",
    name: "Login",
    component: LoginPage,
  },
  {
    path: "/account",
    name: "Account",
    component: AccountPage,
  },
  {
    path: "/detail/:id",
    name: "Detail",
    component: DetailPage,
    props: true,
  },
  {
    path: "/recipe",
    name: "Recipe",
    component: RecipePage,
  },
  {
    path: "/recipeCreate",
    name: "RecipeCreate",
    component: RecipeCreatePage,
  },
  {
    path: "/register",
    name: "Register",
    component: RegisterPage,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
