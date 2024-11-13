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
            <div class="card" v-for="recipe in recipes" :key="recipe.id">
              <div class="image-container">
                <img
                  :src="recipe.imageSrc"
                  alt="Rezeptbild"
                  v-if="recipe.imageSrc"
                  class="recipe-image"
                />
              </div>
              <div class="info-container">
                <h3 class="recipe-title">{{ recipe.name }}</h3>
                <div class="recipe-attributes">
                  <span>{{ recipe.category }}</span>
                  <span>{{ recipe.duration }} Min</span>
                  <span>{{ recipe.price }} €</span>
                  <span>{{ recipe.difficultyLevel }}</span>
                </div>
              </div>
            </div>
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

export default {
  name: "HomePage",
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
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
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

.card {
  flex: 0 0 auto;
  width: 200px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.image-container {
  width: 100%;
  height: 120px;
  overflow: hidden;
}

.recipe-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-container {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.recipe-title {
  font-size: 1rem;
  font-weight: bold;
  margin: 0;
  margin-bottom: 5px;
}

.recipe-attributes {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  font-size: 0.8rem;
  color: #555;
}

.recipe-attributes span {
  background-color: #e9ecef;
  padding: 3px 6px;
  border-radius: 12px;
  white-space: nowrap;
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

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
}
</style>
