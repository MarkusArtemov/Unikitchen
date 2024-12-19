<template>
  <div class="home-container">
    <main>
      <!-- Content displayed when the user is not logged in -->
      <div class="content" v-if="!isUserLoggedIn">
        <div class="text-section">
          <!-- Welcome title and description for Uniküchen -->
          <h1 class="main-title">
            Willkommen bei Uniküchen – Deine Plattform für eine smarte
            Studentenküche!
          </h1>
          <p>
            Du bist Student*in und suchst nach einfachen, günstigen und leckeren
            Rezepten, die perfekt in deinen Alltag passen? Dann bist du hier
            genau richtig!
          </p>
          <h2>Uniküchen bietet dir:</h2>
          <ul>
            <!-- List of features offered by the platform -->
            <li>
              <strong>Kreative Rezeptideen:</strong> Ob für den kleinen
              Geldbeutel oder den schnellen Hunger zwischen Vorlesungen – hier
              findest du Rezepte, die einfach und unkompliziert sind.
            </li>
            <li>
              <strong>Community:</strong> Teile deine Lieblingsrezepte, tausche
              dich aus und lass dich inspirieren.
            </li>
          </ul>
          <p>
            Egal, ob du Anfänger*in oder erfahrener Hobbykoch bist – mit
            Uniküchen wird das Kochen zum Kinderspiel.
          </p>
          <p>
            <strong
            >Mach mit und entdecke die Vielfalt. Jetzt registrieren und
              loslegen!</strong
            >
          </p>
        </div>

        <div class="image-section">
          <!-- Decorative image for the home page -->
          <img src="../assets/style/kitchen_image.jpeg" alt="Studentenküche" />
        </div>
      </div>

      <!-- Section displaying the latest recipes -->
      <section class="current-section">
        <h2>Aktuell:</h2>
        <div class="carousel-container">
          <!-- Button to scroll left in the carousel -->
          <button
              class="scroll-button left"
              @click="scrollLeft"
              :disabled="!canScrollLeft"
          >
            ‹
          </button>

          <!-- Container for recipe cards -->
          <div class="cards-container" ref="carousel" @scroll="onScroll">
            <MenuCard
                v-for="recipe in recipes"
                :key="recipe.id"
                :recipe="recipe"
                :to="{ name: 'Detail', params: { id: recipe.id } }"
            />
          </div>

          <!-- Button to scroll right in the carousel -->
          <button
              class="scroll-button right"
              @click="scrollRight"
              :disabled="!canScrollRight"
          >
            ›
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { fetchLastRecipes, fetchRecipeImage } from "@/services/RecipeService";
import MenuCard from "../components/MenuCard.vue";

export default {
  name: "HomePage",
  components: {
    MenuCard,
  },
  data() {
    return {
      recipes: [],
      canScrollLeft: false,
      canScrollRight: true,
      isUserLoggedIn: false,
    };
  },
  async created() {
    this.checkUserLoggedIn();
    const token = localStorage.getItem("token");
    const lastRecipes = await fetchLastRecipes(token);
    this.recipes = lastRecipes;
    for (const recipe of this.recipes) {
      await fetchRecipeImage(recipe);
    }
  },
  methods: {
    checkUserLoggedIn() {
      const token = localStorage.getItem("token");
      this.isUserLoggedIn = !!token;
    },
    scrollLeft() {
      this.$refs.carousel.scrollBy({
        left: -300,
        behavior: "smooth",
      });
    },
    scrollRight() {
      this.$refs.carousel.scrollBy({
        left: 300,
        behavior: "smooth",
      });
    },
    onScroll() {
      const carousel = this.$refs.carousel;
      const scrollPosition = carousel.scrollLeft;
      const maxScroll = carousel.scrollWidth - carousel.clientWidth;

      this.canScrollLeft = scrollPosition > 0;
      this.canScrollRight = scrollPosition < maxScroll;
    },
  },
};
</script>

<style scoped>
.home-container {
  padding: 0 2em;
  max-width: 1200px;
  margin: 0 auto;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.current-section h2 {
  font-size: 1.4rem;
  margin-bottom: 15px;
}
.carousel-container {
  display: flex;
  align-items: center;
  position: relative;
  max-width: 100%;
  overflow: hidden;
}
.cards-container {
  display: flex;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 10px 0;
  gap: 15px;
  -webkit-overflow-scrolling: touch;
}
.cards-container::-webkit-scrollbar {
  display: none;
}
.menu-card {
  flex: 0 0 250px;
  max-width: 250px;
  min-width: 200px;
  box-sizing: border-box;
}
.scroll-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  width: 30px;
  height: 60px;
  cursor: pointer;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}
.scroll-button.left {
  left: 0;
}
.scroll-button.right {
  right: 0;
}
.scroll-button:disabled {
  background-color: rgba(0, 0, 0, 0.2);
  cursor: not-allowed;
}
.scroll-button:hover:not(:disabled) {
  background-color: rgba(0, 0, 0, 0.8);
}
.content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  gap: 2rem;
  flex-wrap: wrap;
}
.text-section {
  flex: 2;
  max-width: 700px;
}
.text-section h1 {
  font-size: 26px;
  margin-bottom: 1.5rem;
}
.text-section ul {
  list-style: disc;
  margin-left: 1.5rem;
}
.image-section {
  flex: 1;
  max-width: 300px;
}
.image-section img {
  width: 100%;
  height: auto;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
