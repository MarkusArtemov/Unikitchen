<template>
  <div class="detail-page">
    <div class="recipe-card">
      <!-- Favoriten-Stern -->
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

      <!--<div class="recipe-image">
        <img v-if="recipeImage" :src="recipeImage" alt="Rezeptbild" />
        <div v-else class="recipe-image-placeholder">
          <p>Kein Bild verfügbar</p>
          </div>
        </div>
-->
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
        <p>
          <strong>Aufrufe: </strong>
          <span class="view-count">{{ recipe.viewCount }}</span>
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
        <p v-if="ratingSubmitted" class="success-message">
          Vielen Dank für Ihre Bewertung!
        </p>
      </div>
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
      isFavorite: false, // Favorite status
      userRating: 0, // User-selected rating
      ratingSubmitted: false, // Flag to show success message
      //recipeImage: null;
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

      //this.recipeImage = this.getFullImagePath(this.recipe.recipeImagePath);

      // Fetch favorite status if the user is logged in
      if (this.isLoggedIn()) {
        await this.checkFavoriteStatus();
      }
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
  methods: {
    isLoggedIn() {
      return !!localStorage.getItem("token");
    },
    async checkFavoriteStatus() {
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
    async toggleFavorite() {
      if (!this.isLoggedIn()) {
        alert("Bitte melden Sie sich an, um Favoriten zu nutzen.");
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
        this.isFavorite = !this.isFavorite; // Toggle the favorite state
      } catch (error) {
        console.error("Fehler beim Hinzufügen/Entfernen aus Favoriten:", error);
      }
    },
    async submitRating(star) {
      if (!this.isLoggedIn()) {
        alert("Bitte melden Sie sich an, um eine Bewertung abzugeben.");
        return;
      }

      const recipeId = this.recipe.id;
      const token = localStorage.getItem("token");

      try {
        await axios.post(`/api/ratings/recipe/${recipeId}`, null, {
          params: { ratingValue: star },
          headers: { Authorization: `Bearer ${token}` },
        });
        this.userRating = star;
        this.ratingSubmitted = true;

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
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
}

.recipe-card {
  position: relative;
  width: 90%;
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
  font-weight: bold;
}

.ingredients {
  margin-top: 20px;
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
  font-size: 1rem;
  color: #333;
  display: flex;
  align-items: center;
}

.ratings {
  margin-top: 30px;
  font-size: 1rem;
  color: #666;
}

.star-container {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.star {
  font-size: 2rem;
  color: #ccc;
  cursor: pointer;
  transition: color 0.2s ease, transform 0.2s ease;
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
  color: #fbbf24; /* Gold color when selected */
}

.favorite-icon::after {
  content: attr(data-tooltip);
  position: absolute;
  top: -25px;
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

@media (max-width: 768px) {
  .recipe-card {
    padding: 20px;
  }

  .recipe-title {
    font-size: 2rem;
  }

  .recipe-preparation {
    font-size: 1rem;
  }

  .ingredients h2 {
    font-size: 1.2rem;
  }

  .star {
    font-size: 1.5rem;
  }

  .favorite-icon {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .recipe-card {
    padding: 15px;
  }

  .recipe-title {
    font-size: 1.8rem;
  }

  .recipe-preparation {
    font-size: 0.9rem;
  }

  .ingredients h2 {
    font-size: 1rem;
  }

  .star {
    font-size: 1.2rem;
  }

  .favorite-icon {
    font-size: 1.8rem;
  }
}
</style>
