<template>
  <div class="home-container">
    <main>
      <section class="current-section">
        <h2>Aktuell:</h2>
        <div class="carousel-container">
          <!-- Scroll Button Left -->
          <button
            class="scroll-button left"
            @click="scrollLeft"
            :disabled="!canScrollLeft"
          >
            ‹
          </button>

          <!-- Cards Container -->
          <div class="cards-container" ref="carousel" @scroll="onScroll">
            <MenuCard
              v-for="recipe in recipes"
              :key="recipe.id"
              :recipe="recipe"
            />
          </div>

          <!-- Scroll Button Right -->
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
import axios from "axios";
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
    };
  },
  created() {
    this.fetchRecipes();
  },
  methods: {
    async fetchRecipes() {
      try {
        const response = await axios.get("/api/recipes/lastRecipes", {});
        this.recipes = response.data;

        for (const recipe of this.recipes) {
          this.fetchRecipeImage(recipe);
        }
      } catch (error) {
        console.error("Fehler beim Abrufen der Rezepte:", error);
      }
    },
    async fetchRecipeImage(recipe) {
      try {
        const response = await axios.get(
          `/api/recipes/${recipe.id}/recipe-image`,
          {
            responseType: "arraybuffer",
          }
        );

        const base64Image = btoa(
          new Uint8Array(response.data).reduce(
            (data, byte) => data + String.fromCharCode(byte),
            ""
          )
        );
        recipe.imageSrc = `data:image/jpeg;base64,${base64Image}`;
      } catch (error) {
        console.error("Fehler beim Abrufen des Rezeptbildes:", error);
      }
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
/* Styles bleiben gleich */
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.current-section h2 {
  font-size: 1.4rem;
  margin-bottom: 15px;
}
/* Container für den Karussellbereich */
.carousel-container {
  display: flex;
  align-items: center;
  position: relative;
  max-width: 100%;
  overflow: hidden; /* Verhindert, dass Inhalte nach außen sichtbar werden */
}

/* Cards Container mit flexibler Breite */
.cards-container {
  display: flex;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 10px 0;
  gap: 15px;
  -webkit-overflow-scrolling: touch;
}

.cards-container::-webkit-scrollbar {
  display: none; /* Deaktiviert die Scrollbar für ein saubereres Layout */
}

/* Minimum- und Standardgrößen für die Karten */
.menu-card {
  flex: 0 0 250px; /* Karten haben eine feste Breite von 250px */
  max-width: 250px;
  min-width: 200px; /* Karten werden nicht kleiner als 200px */
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

/* Optionale Animation für Scrollen */
.scroll-button:hover:not(:disabled) {
  background-color: rgba(0, 0, 0, 0.8);
}
</style>
