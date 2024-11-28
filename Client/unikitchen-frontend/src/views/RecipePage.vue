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
.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px;
}
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
}

.icon {
  font-size: 1.25rem;
}
.error {
  color: red;
  margin-top: 20px;
  text-align: center;
}
</style>


// TODO: Rezepte in der Übersicht größer machen

// TODO: Rezepte hinzufügen können - Auch Kategorien auswählen und Bilder hinzufügen können..