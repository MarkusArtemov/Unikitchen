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

      <!-- Button-Gruppe für günstig, schnell, beliebt -->
      <div class="button-group">
        <button
          :class="{ active: selectedFilterType === 'cheap' }"
          @click="selectFilter('cheap')"
        >
          Günstig
        </button>

        <button
          :class="{ active: selectedFilterType === 'quick' }"
          @click="selectFilter('quick')"
        >
          Schnell
        </button>

        <button
          :class="{ active: selectedFilterType === 'popular' }"
          @click="selectFilter('popular')"
        >
          Beliebt
        </button>
      </div>
    </div>

    <div class="recipe-grid">
      <MenuCard
        v-for="recipe in recipes"
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
import { fetchRecipeImage } from "@/services/RecipeService";

export default {
  components: {
    MenuCard,
  },
  data() {
    return {
      recipes: [],
      selectedCategory: "",
      selectedFilterType: "", // "cheap", "quick", "popular" oder ""
      errorMessage: "",
    };
  },
  methods: {
    async fetchAllRecipes() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("/api/recipes/allRecipes", {
          headers: { Authorization: `Bearer ${token}` },
        });

        this.recipes = response.data;

        for (const recipe of this.recipes) {
          if (recipe.recipeImagePath !== null) {
            await fetchRecipeImage(recipe);
          }
        }
      } catch (error) {
        this.errorMessage =
          "Fehler beim Laden der Rezepte. Bitte versuchen Sie es später erneut.";
        console.error(error);
      }
    },

    async filterRecipes() {
      try {
        const token = localStorage.getItem("token");

        const params = {
          category: this.selectedCategory || null,
        };

        // Ausgewählten Filtertyp überprüfen
        if (this.selectedFilterType === "cheap") {
          params.cheap = true;
        } else if (this.selectedFilterType === "quick") {
          params.quick = true;
        } else if (this.selectedFilterType === "popular") {
          params.sortBy = "popular";
        }

        const response = await axios.get("/api/recipes/filtered", {
          headers: { Authorization: `Bearer ${token}` },
          params,
        });

        this.recipes = response.data;

        for (const recipe of this.recipes) {
          if (recipe.recipeImagePath !== null) {
            await fetchRecipeImage(recipe);
          }
        }
      } catch (error) {
        this.errorMessage =
          "Fehler beim Anwenden der Filter. Bitte versuchen Sie es später erneut.";
        console.error(error);
      }
    },

    selectFilter(filterType) {
      // Wenn man einen Button klickt, wird dieser aktiv. Wenn derselbe Button nochmal
      // geklickt wird, kann man den Filter auch wieder aufheben.
      this.selectedFilterType =
        this.selectedFilterType === filterType ? "" : filterType;
      this.filterRecipes();
    },
  },
  async mounted() {
    await this.fetchAllRecipes();
  },
};
</script>

<style scoped>
.filter {
  display: flex;
  gap: 10px;
  align-items: center;
}

.button-group {
  display: inline-block;
}

.button-group button {
  margin: 0 5px;
  padding: 8px 12px;
}

.button-group button.active {
  background-color: #333;
  color: #fff;
}
.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px;
}
.error {
  color: red;
  margin-top: 20px;
  text-align: center;
}
</style>
