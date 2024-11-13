<template>
  <div class="recipe-page">
    <h2>Alle Rezepte</h2>

    <div class="filter">
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
        <option value="easy">Einfach</option>
        <option value="medium">Mittel</option>
        <option value="hard">Schwierig</option>
      </select>
    </div>

    <div class="recipe-grid">
      <div
        v-for="recipe in filteredRecipes"
        :key="recipe.id"
        class="recipe-card"
      >
        <router-link
          :to="{ name: 'Detail', params: { id: recipe.id } }"
          class="no-link"
        >
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
      selectedDuration: "",
      selectedDifficulty: "",
      errorMessage: "",
    };
  },
  computed: {
    filteredRecipes() {
      return this.recipes.filter((recipe) => {
        const matchesDuration = this.selectedDuration
          ? recipe.durationCategory === this.selectedDuration
          : true;
        const matchesDifficulty = this.selectedDifficulty
          ? recipe.difficultyLevel === this.selectedDifficulty
          : true;
        return matchesDuration && matchesDifficulty;
      });
    },
  },
  methods: {
    async fetchRecipes() {
      try {
        const token = localStorage.getItem("token");
        const params = {};

        if (this.selectedDuration) {
          params.duration = this.selectedDuration;
        }
        if (this.selectedDifficulty) {
          params.difficulty = this.selectedDifficulty;
        }

        const response = await axios.get("/api/recipes/allRecipes", {
          headers: { Authorization: `Bearer ${token}` },
          params: params,
        });

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

    filterRecipes() {},
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
.icon-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  border-radius: 50%;
  background-color: #f8f9fa;
  width: 60px;
  height: 60px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: background-color 0.2s, transform 0.2s;
}

.icon-container:hover {
  background-color: #007bff;
  transform: scale(1.1);
}

.icon {
  color: #333;
  font-size: 1.5rem;
}

.icon-container:hover .icon {
  color: white;
}

@media (max-width: 768px) {
  .icon-container {
    width: 50px;
    height: 50px;
  }

  .icon {
    font-size: 1.25rem;
  }
}
</style>

