<template>
  <div class="detail-page">
    <!-- Recipe Card Container -->
    <div class="recipe-card">
      <!-- Favorite Icon: Allows toggling favorite status -->
      <div
          class="favorite-icon"
          :class="{ filled: isFavorite }"
          :data-tooltip="
          isFavorite ? 'Vom Favoriten entfernen' : 'Zu Favoriten hinzufügen'
        "
          @click.stop="toggleFavorite"
      >
        {{ isFavorite ? "★" : "☆" }}
      </div>

      <!-- Recipe Title -->
      <h1 class="recipe-title">{{ recipe.name }}</h1>

      <!-- Recipe Image Section -->
      <div class="recipe-image">
        <img v-if="recipeImage" :src="recipeImage" alt="Rezeptbild" />
        <div v-else class="recipe-image-placeholder">
          <p>Kein Bild verfügbar</p>
        </div>
      </div>

      <!-- Recipe Description Section -->
      <div class="recipe-description">
        <h2>Beschreibung</h2>
        <p class="recipe-preparation">{{ recipe.preparation }}</p>
      </div>

      <!-- Recipe Information Section -->
      <div class="recipe-info">
        <h2>Informationen</h2>
        <p>
          <strong>Kategorie:</strong>
          <span class="category">{{ recipe.category }}</span>
        </p>
        <p>
          <strong>Schwierigkeitsgrad:</strong>
          <span class="difficulty">{{ recipe.difficultyLevel }}</span>
        </p>
        <p>
          <strong>Dauer:</strong>
          <span class="duration">⏱ {{ recipe.duration }} Minuten</span>
        </p>
        <p v-if="isOwner">
          <strong>Aufrufe:</strong>
          <span class="view-count">{{ recipe.viewCount }}</span>
        </p>
      </div>

      <!-- Ingredients List -->
      <div class="ingredients">
        <h2>Zutaten</h2>
        <ul>
          <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
            <span class="ingredient-icon">•</span>
            <span class="ingredient-text">
              {{ ingredient.name }} {{ ingredient.quantity }} {{ ingredient.unit }}
            </span>
          </li>
        </ul>
      </div>

      <!-- Ratings Overview -->
      <div class="rating-overview">
        <h2>Bewertungen</h2>
        <div v-if="recipe.ratingCount === 0">
          <p class="no-ratings">Noch keine Bewertungen</p>
        </div>
        <div v-else class="rating-display">
          <!-- Star Rating Display -->
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
          <div class="rating-info">
            Durchschnitt: {{ recipe.averageRating }} ({{ recipe.ratingCount }}
            Bewertungen)
          </div>
        </div>
      </div>

      <!-- Rating Submission Section -->
      <div class="ratings" v-if="!isOwner">
        <h2>Bewertung abgeben</h2>
        <div class="star-container-user">
          <!-- Stars for user to submit rating -->
          <span
              v-for="star in 5"
              :key="star"
              class="star selectable"
              :class="{ selected: star <= userRating }"
              @click="submitRatingAction(star)"
          >
            ★
          </span>
        </div>
        <p v-if="userRating > 0 && !ratingSubmitted" class="already-rated-message">
          Sie haben bereits {{ userRating }} Sterne vergeben.
        </p>
        <p v-if="ratingSubmitted" class="success-message">
          Vielen Dank für Ihre Bewertung!
        </p>
      </div>

      <!-- Owner Actions -->
      <div v-if="isOwner" class="owner-actions">
        <!-- Edit Recipe Button -->
        <button @click="goToEditPage" class="edit-button">Bearbeiten</button>
        <!-- Delete Recipe Button -->
        <button @click="deleteRecipeAction" class="delete-button">Löschen</button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  fetchRecipeDetails,
  fetchRecipeImage,
  toggleFavorite,
  fetchUserRating,
  submitRating,
  deleteRecipe,
} from "@/services/RecipeService";
import axios from "axios";

export default {
  name: "DetailPage", // Component name
  data() {
    return {
      recipe: {}, // Stores recipe details
      isFavorite: false, // Indicates if the recipe is a favorite
      userRating: 0, // Stores the user's rating for the recipe
      ratingSubmitted: false, // Tracks if the user has submitted a rating
      currentUser: null, // Information about the logged-in user
      recipeImage: null, // URL of the recipe image
    };
  },
  computed: {
    /**
     * Checks if the current user is the owner of the recipe.
     * @returns {boolean} True if the current user owns the recipe.
     */
    isOwner() {
      return (
          this.currentUser &&
          this.recipe.ownerUsername === this.currentUser.username
      );
    },
  },
  async created() {
    // Load recipe and user data on component creation
    const recipeId = this.$route.params.id;
    const token = localStorage.getItem("token");
    await this.loadCurrentUser(token);
    this.recipe = await fetchRecipeDetails(token, recipeId);

    await fetchRecipeImage(this.recipe);
    this.recipeImage = this.recipe.imageSrc;

    if (this.isLoggedIn()) {
      this.isFavorite = this.recipe.isFavorite;
      const userRatingRes = await fetchUserRating(token, recipeId);
      if (userRatingRes && userRatingRes.ratingValue) {
        this.userRating = userRatingRes.ratingValue;
      } else {
        this.userRating = 0;
      }
    }
  },
  methods: {
    /**
     * Loads the current user's information.
     * @param {string} token - Authentication token.
     */
    async loadCurrentUser(token) {
      if (!token) return;
      const response = await axios.get("/api/users/current-user", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.currentUser = response.data;
    },
    /**
     * Checks if the user is logged in.
     * @returns {boolean} True if the user is logged in.
     */
    isLoggedIn() {
      return !!localStorage.getItem("token");
    },
    /**
     * Toggles the favorite status of the recipe.
     */
    async toggleFavorite() {
      if (!this.isLoggedIn()) {
        console.warn("Favoritenfunktion nur für eingeloggte Benutzer verfügbar.");
        return;
      }
      const token = localStorage.getItem("token");
      const res = await toggleFavorite(token, this.recipe.id);
      this.isFavorite = res.isFavorite;
    },
    /**
     * Submits a user's rating for the recipe.
     * @param {number} star - The rating value (1-5 stars).
     */
    async submitRatingAction(star) {
      if (!this.isLoggedIn()) {
        alert("Bitte melden Sie sich an, um eine Bewertung abzugeben.");
        return;
      }
      const token = localStorage.getItem("token");
      await submitRating(token, this.recipe.id, star);
      this.userRating = star;
      this.ratingSubmitted = true;
      setTimeout(() => {
        this.ratingSubmitted = false;
      }, 3000);
    },
    /**
     * Calculates the width of a star based on the average rating.
     * @param {number} starIndex - Index of the star (1-5).
     * @returns {string} Width percentage of the star.
     */
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
    /**
     * Navigates to the edit page for the current recipe.
     */
    goToEditPage() {
      this.$router.push({ name: "RecipeEdit", params: { id: this.recipe.id } });
    },
    /**
     * Deletes the current recipe after user confirmation.
     */
    async deleteRecipeAction() {
      if (!confirm("Möchten Sie dieses Rezept wirklich löschen?")) {
        return;
      }
      const token = localStorage.getItem("token");
      await deleteRecipe(token, this.recipe.id);
      alert("Rezept erfolgreich gelöscht!");
      this.$router.push({ name: "Account", query: { section: "myRecipes" } });
    },
  },
};
</script>

<style scoped>
.detail-page {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  min-height: 100vh;
}
.recipe-card {
  position: relative;
  width: 100%;
  max-width: 800px;
  background: #ffffff;
  border-radius: 15px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  padding: 30px;
  font-family: "Roboto", sans-serif;
  margin-top: 20px;
  margin-bottom: 20px;
}
.recipe-title {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
  font-weight: bold;
}
.recipe-image {
  text-align: center;
  margin-bottom: 20px;
}
.recipe-image img {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 10px;
}
.recipe-image-placeholder {
  background-color: #f1f3f5;
  padding: 20px;
  border-radius: 10px;
  color: #777;
}
.recipe-description {
  margin-bottom: 30px;
}
.recipe-description h2 {
  font-size: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: #333;
}
.recipe-preparation {
  font-size: 1.1rem;
  color: #555;
  line-height: 1.6;
}
.recipe-info {
  margin-bottom: 30px;
}
.recipe-info h2 {
  font-size: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: #333;
}
.recipe-info p {
  font-size: 1rem;
  margin-bottom: 10px;
  color: #444;
}
.ingredients {
  margin-bottom: 30px;
}
.ingredients h2 {
  font-size: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: #333;
}
.ingredients ul {
  list-style: none;
  padding: 0;
}
.ingredients li {
  background: #f9fafb;
  padding: 10px;
  margin-bottom: 8px;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
  display: flex;
  align-items: center;
}
.ingredient-icon {
  margin-right: 10px;
  font-size: 1.2rem;
}
.rating-overview {
  margin-bottom: 30px;
}
.rating-overview h2 {
  font-size: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: #333;
}
.rating-overview .no-ratings {
  color: #999;
  font-style: italic;
}
.rating-display {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.star-row {
  display: flex;
}
.star-container {
  position: relative;
  display: inline-block;
  width: 1em;
  height: 1em;
  font-size: 2rem;
  line-height: 1;
  margin-right: 2px;
  overflow: hidden;
  transition: transform 0.2s ease;
}
.star-container:hover {
  transform: scale(1.1);
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
  font-size: 1rem;
  color: #333;
}
.ratings {
  margin-bottom: 30px;
}
.ratings h2 {
  font-size: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 5px;
  margin-bottom: 10px;
  color: #333;
}
.star-container-user {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}
.star.selectable {
  cursor: pointer;
  transition: color 0.2s ease, transform 0.2s ease;
  margin: 0 2px;
  font-size: 2rem;
  color: #ccc;
}
.star:hover,
.star.selected {
  color: gold;
  transform: scale(1.2);
}
.success-message {
  color: green;
  font-size: 1rem;
  text-align: center;
  margin-top: 10px;
}
.favorite-icon {
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 2.5rem;
  color: #d1d5db;
  cursor: pointer;
  transition: color 0.3s ease, transform 0.3s ease;
}
.favorite-icon:hover {
  transform: scale(1.2);
}
.favorite-icon.filled {
  color: #fbbf24;
}
.favorite-icon::after {
  content: attr(data-tooltip);
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 5px 8px;
  border-radius: 5px;
  font-size: 0.8rem;
  white-space: nowrap;
  display: none;
  z-index: 10;
}
.favorite-icon:hover::after {
  display: block;
}
.owner-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
.edit-button,
.delete-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  color: #fff;
}
.edit-button {
  background-color: #007bff;
}
.edit-button:hover {
  background-color: #0056b3;
}
.delete-button {
  background-color: #dc3545;
}
.delete-button:hover {
  background-color: #c82333;
}
.already-rated-message {
  color: #555;
  text-align: center;
  margin-top: 5px;
  font-size: 0.9rem;
}
</style>
