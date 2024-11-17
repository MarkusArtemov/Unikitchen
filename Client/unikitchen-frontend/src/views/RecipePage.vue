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
      <MenuCard
        v-for="recipe in filteredRecipes"
        :key="recipe.id"
        :recipe="recipe"
        :to="{ name: 'Detail', params: { id: recipe.id } }"
      />
    </div>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from "axios";
import MenuCard from "@/components/MenuCard.vue";

export default {
  components: {
    MenuCard,
  },
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

        for (const recipe of this.recipes) {
          await this.fetchRecipeImage(recipe);
        }
      } catch (error) {
        this.errorMessage =
          "Fehler beim Laden der Rezepte. Bitte versuchen Sie es später erneut.";
        console.error(error);
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
        console.error(
          `Fehler beim Laden des Bildes für Rezept ${recipe.id}:`,
          error
        );
        recipe.imageSrc = "";
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

.error {
  color: red;
  margin-top: 20px;
  text-align: center;
}
</style>
