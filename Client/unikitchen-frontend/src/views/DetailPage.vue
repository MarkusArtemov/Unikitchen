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

      <div class="ratings">
        <p><strong>Bewertungen:</strong> Noch keine Bewertungen</p>
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
      // Überprüfen, ob das Rezept bereits in den Favoriten ist
      await this.checkFavoriteStatus();
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
  methods: {
    // Methode zum Überprüfen des Favoritenstatus
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

    // Methode zum Hinzufügen/Entfernen des Rezepts aus den Favoriten
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

    // Überprüfen, ob der Benutzer eingeloggt ist
    isLoggedIn() {
      return !!localStorage.getItem("token");
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

.recipe-image {
  margin: 20px 0;
  text-align: center;
}

.recipe-image img {
  max-width: 100%;
  border-radius: 8px;
}

.recipe-image-placeholder {
  margin: 20px 0;
  text-align: center;
}

.recipe-image-placeholder img {
  width: 100%;
  max-width: 300px;
  height: auto;
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
