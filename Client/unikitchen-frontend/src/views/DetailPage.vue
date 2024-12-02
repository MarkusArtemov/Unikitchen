<template>
  <div class="detail-page">
    <div class="recipe-card">
      <h1 class="recipe-title">{{ recipe.name }}</h1>
      <p class="recipe-preparation">{{ recipe.preparation }}</p>

      <div class="recipe-info">
        <p>
          <strong>Schwierigkeitsgrad: </strong>
          <span class="difficulty"> {{ recipe.difficultyLevel }}</span>
        </p>
        <p>
          <strong>Zubereitungszeit: </strong>
          <span class="duration"> {{ recipe.duration }} Minuten</span>
        </p>
      </div>

      <div class="ingredients">
        <h2>Zutaten:</h2>
        <ul>
          <li v-for="ingredient in recipe.ingredients" :key="ingredient.id">
            {{ ingredient.name }} : {{ ingredient.quantity
            }}{{ ingredient.unit }}
          </li>
        </ul>
      </div>

      <!-- Rating Submission Section -->
      <div class="ratings">
        <h2>Bewertung abgeben:</h2>
        <div class="star-container">
          <span
              v-for="star in 5"
              :key="star"
              class="star"
              :class="{ selected: star <= userRating }"
              @click="submitRating(star)"
          >
            ★
          </span>
        </div>
        <p v-if="ratingSubmitted" class="success-message">Vielen Dank für Ihre Bewertung!</p>
      </div>

      <!-- Favoriten-Button -->
      <button @click="toggleFavorite" class="favorite-button">
        {{ isFavorite ? "Vom Favoriten entfernen" : "Zu Favoriten hinzufügen" }}
      </button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "DetailPage",
  data() {
    return {
      recipe: {},
      isFavorite: false, // Favoritenstatus
      userRating: 0, // User-selected rating
      ratingSubmitted: false, // Flag to show success message
    };
  },
  async created() {
    const recipeId = this.$route.params.id;
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`/api/recipes/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.recipe = response.data;

      // Check if the recipe is already in favorites
      await this.checkFavoriteStatus();
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
  methods: {
    // Check if the user is logged in
    isLoggedIn() {
      return !!localStorage.getItem("token");
    },

    // Check favorite status
    async checkFavoriteStatus() {
      if (!this.isLoggedIn()) return;

      try {
        const response = await axios.get("/api/favorites/current", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });

        this.isFavorite = response.data.some(
            (favorite) => String(favorite.recipeId) === String(this.recipe.id)
        );
      } catch (error) {
        console.error("Fehler beim Überprüfen des Favoritenstatus:", error);
      }
    },

    // Toggle recipe in favorites
    async toggleFavorite() {
      if (!this.isLoggedIn()) {
        console.warn(
            "Favoritenfunktion ist nur für eingeloggte Benutzer verfügbar."
        );
        return;
      }

      try {
        if (this.isFavorite) {
          await axios.delete(`/api/favorites/current/${this.recipe.id}`, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
        } else {
          await axios.post(`/api/favorites/current/${this.recipe.id}`, null, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
        }
        this.isFavorite = !this.isFavorite;
      } catch (error) {
        console.error(
            "Fehler beim Hinzufügen/Entfernen aus den Favoriten:",
            error
        );
      }
    },

    // Submit user rating
    async submitRating(star) {
      if (!this.isLoggedIn()) {
        alert("Bitte melden Sie sich an, um eine Bewertung abzugeben.");
        return;
      }

      const recipeId = this.recipe.id;
      const token = localStorage.getItem("token");

      try {
        // Submit rating to backend
        await axios.post(
            `/api/ratings/recipe/${recipeId}`,
            null,
            {
              params: { ratingValue: star },
              headers: { Authorization: `Bearer ${token}` },
            }
        );
        this.userRating = star;
        this.ratingSubmitted = true;

        // Hide success message after 3 seconds
        setTimeout(() => {
          this.ratingSubmitted = false;
        }, 3000);
      } catch (error) {
        console.error("Fehler beim Absenden der Bewertung:", error);
        alert("Fehler beim Absenden der Bewertung.");
      }
    },
  },
};
</script>


<style scoped>
.detail-page {
  display: flex;
  justify-content: center;
  padding: 20px;
  background-color: #f8f9fa;
}

.recipe-card {
  max-width: 600px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
  font-family: Arial, sans-serif;
}

.recipe-title {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 10px;
  text-align: center;
}

.recipe-preparation {
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 20px;
  line-height: 1.6;
}

.recipe-info p {
  font-size: 1rem;
  margin-bottom: 10px;
}

.difficulty,
.duration {
  color: #007bff;
}

.ingredients h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #333;
}

.ingredients ul {
  list-style: none;
  padding: 0;
}

.ingredients li {
  background: #f1f3f5;
  padding: 10px;
  margin-bottom: 8px;
  border-radius: 4px;
}

.ratings {
  margin: 20px 0;
  font-size: 1rem;
  color: #666;
}

.star-container {
  display: flex;
  margin-bottom: 10px;
}

.star {
  font-size: 2rem;
  color: #ccc;
  cursor: pointer;
  transition: color 0.2s;
}

.star.selected {
  color: gold;
}

.success-message {
  color: green;
  font-size: 1rem;
}

.favorite-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

.favorite-button:hover {
  background-color: #0056b3;
}
</style>
