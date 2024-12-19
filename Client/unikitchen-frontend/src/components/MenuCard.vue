<template>
  <div class="menu-card" @click="navigateToDetail">
    <div class="image-container">
      <div v-if="!recipe.imageSrc" class="placeholder-container"></div>
      <img
        v-else
        :src="recipe.imageSrc"
        alt="Rezeptbild"
        class="recipe-image"
      />
      <!-- Favorite icon is displayed only if the user is logged in -->
      <div
        v-if="isLoggedIn()"
        class="favorite-icon"
        @click.stop="toggleFavorite"
      >
        {{ localIsFavorite ? "★" : "☆" }}
      </div>
    </div>

    <div class="info-container">
      <h3 class="recipe-title">{{ recipe.name }}</h3>

      <div class="rating-overview">
        <template v-if="recipe.ratingCount === 0">
          <span class="no-ratings">Noch keine Bewertungen</span>
        </template>
        <template v-else>
          <div class="star-row">
            <div v-for="starIndex in 5" :key="starIndex" class="star-container">
              <div class="star-background">★</div>
              <div
                class="star-foreground"
                :style="{ width: getStarWidth(starIndex) }"
              >
                ★
              </div>
            </div>
          </div>
          <span class="rating-info"> ({{ recipe.ratingCount }}) </span>
        </template>
      </div>

      <div class="recipe-attributes">
        <span>{{ recipe.category }}</span>
        <span>⏱ {{ recipe.duration }} Min</span>
        <span>{{ recipe.price }} €</span>
        <span>{{ recipe.difficultyLevel }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { toggleFavorite } from "@/services/RecipeService";

export default {
  name: "MenuCard",
  props: {
    recipe: {
      type: Object,
      required: true,
    },
    to: {
      type: [String, Object],
      required: true,
    },
  },
  data() {
    return {
      localIsFavorite: this.recipe.isFavorite,
    };
  },
  watch: {
    recipe: {
      handler(newVal) {
        this.localIsFavorite = newVal.isFavorite;
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    isLoggedIn() {
      return !!localStorage.getItem("token");
    },
    async toggleFavorite() {
      if (!this.isLoggedIn()) {
        console.warn(
          "Favoritenfunktion ist nur für eingeloggte Benutzer verfügbar."
        );
        return;
      }
      const token = localStorage.getItem("token");
      const res = await toggleFavorite(token, this.recipe.id);
      this.localIsFavorite = res.isFavorite;
    },
    navigateToDetail() {
      if (this.isLoggedIn()) {
        this.$router.push(this.to);
      } else {
        const confirmed = confirm(
          "Bitte melden Sie sich an, um die Rezeptdetails anzusehen."
        );
        if (confirmed) {
          this.$router.push({ name: "Login" });
        }
      }
    },
    getStarWidth(starIndex) {
      const rating = this.recipe.averageRating || 0;
      if (rating <= 0) return "0%";

      const fullStars = Math.floor(rating);
      const fraction = rating - fullStars;

      if (starIndex <= fullStars) {
        return "100%";
      } else if (starIndex === fullStars + 1) {
        return fraction * 100 + "%";
      } else {
        return "0%";
      }
    },
  },
};
</script>

<style scoped>
.menu-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 10px;
  overflow: hidden;
  text-align: center;
  transition: transform 0.3s;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-width: 200px;
}
.menu-card:hover {
  transform: scale(1.05);
}
.image-container {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background-color: #f7f7f7;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  cursor: pointer;
}
.recipe-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.placeholder-container {
  width: 100%;
  height: 100%;
  background-color: #e9ecef;
  min-width: 100px;
  min-height: 100px;
}
.favorite-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 1.5rem;
  color: gold;
  cursor: pointer;
  z-index: 10;
  user-select: none;
}
.favorite-icon:hover {
  transform: scale(1.2);
}
.info-container {
  padding: 10px;
  width: 100%;
  cursor: pointer;
}
.recipe-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0;
  margin-bottom: 10px;
  color: #333;
}
.recipe-attributes {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  font-size: 0.85rem;
  color: #555;
  justify-content: center;
}
.recipe-attributes span {
  background-color: #e9ecef;
  padding: 5px 10px;
  border-radius: 12px;
  white-space: nowrap;
}
.rating-overview {
  margin-bottom: 10px;
  font-size: 0.9rem;
  text-align: center;
}
.rating-overview .no-ratings {
  color: #999;
  font-style: italic;
}
.star-row {
  display: flex;
  justify-content: center;
  margin-bottom: 5px;
}
.star-container {
  position: relative;
  display: inline-block;
  width: 1em;
  height: 1em;
  line-height: 1;
  font-size: 1rem;
  margin: 0 1px;
  overflow: hidden;
}
.star-background {
  color: #ccc;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  text-align: center;
}
.star-foreground {
  color: gold;
  position: absolute;
  top: 0;
  left: 0;
  white-space: nowrap;
  overflow: hidden;
  text-align: center;
}
.rating-info {
  margin-left: 5px;
  color: #333;
  font-size: 0.9rem;
}
</style>
