<template>
  <div class="recipe-page">
    <h2>Alle Rezepte</h2>

    <div class="filter">
      <label for="category">Kategorie:</label>
      <select v-model="selectedCategory" @change="filterRecipes">
        <option value="">Alle</option>
        <option value="KUCHEN">Kuchen</option>
        <option value="NUDELN">Nudeln</option>
        <option value="REIS">Reis</option>
        <option value="FLEISCH">Fleisch</option>
        <option value="VEGETARISCH">Vegetarisch</option>
      </select>

      <label for="duration">Dauer:</label>
      <select v-model="selectedDuration" @change="filterRecipes">
        <option value="">Alle</option>
        <option value="short">Kurz</option>
        <option value="medium">Mittel</option>
        <option value="long">Lang</option>
      </select>

      <label for="difficulty">Schwierigkeitsgrad:</label>
      <select v-model="selectedDifficulty" @change="filterRecipes">
        <option value="">Alle</option>
        <option value="EINFACH">Einfach</option>
        <option value="MITTEL">Mittel</option>
        <option value="SCHWIERIG">Schwierig</option>
      </select>
    </div>

    <div class="recipe-grid">
      <div v-for="recipe in filteredRecipes" :key="recipe.id" class="recipe-card">
        <router-link :to="{ name: 'Detail', params: { id: recipe.id } }" class="no-link">
          <img :src="recipe.image" :alt="recipe.name" />
          <h3>{{ recipe.name }}</h3>
          <p>Dauer: {{ recipe.duration }} Minuten</p>
          <p>Schwierigkeitsgrad: {{ recipe.difficultyLevel }}</p>
        </router-link>
      </div>
    </div>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      recipes: [],
      selectedCategory: "",
      selectedDuration: "",
      selectedDifficulty: "",
      errorMessage: "",
    };
  },
  computed: {
    filteredRecipes() {
      return this.recipes.filter((recipe) => {
        const matchesCategory = this.selectedCategory
            ? recipe.category === this.selectedCategory
            : true;
        const matchesDuration = this.selectedDuration
            ? recipe.durationCategory === this.selectedDuration
            : true;
        const matchesDifficulty = this.selectedDifficulty
            ? recipe.difficultyLevel === this.selectedDifficulty
            : true;
        return matchesCategory && matchesDuration && matchesDifficulty;
      });
    },
  },
  methods: {
    async fetchRecipes() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("/api/recipes/allRecipes", {
          headers: { Authorization: `Bearer ${token}` },
        });

        // Map response data to include a `durationCategory` field
        this.recipes = response.data.map((recipe) => ({
          ...recipe,
          durationCategory: this.getDurationCategory(recipe.duration),
        }));
      } catch (error) {
        this.errorMessage =
            "Fehler beim Laden der Rezepte. Bitte versuchen Sie es später erneut.";
        console.error(error);
      }
    },

    getDurationCategory(duration) {
      if (duration <= 15) return "short";
      if (duration <= 30) return "medium";
      return "long";
    },
  },
  mounted() {
    this.fetchRecipes();
  },
};
</script>

<style scoped>
.recipe-page {
  padding: 20px;
}

.filter {
  margin-bottom: 20px;
}

.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.recipe-card {
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
  text-align: center;
  padding: 10px;
  transition: transform 0.3s;
}

.recipe-card:hover {
  transform: scale(1.05);
}

.recipe-card img {
  width: 100%;
  height: auto;
}

.error {
  color: red;
  margin-top: 20px;
  text-align: center;
}

.no-link {
  color: inherit;
  text-decoration: none;
}
</style>
