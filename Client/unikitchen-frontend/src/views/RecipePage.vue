<template>
  <div class="recipe-page">
    <!-- Filter bar for recipe selection -->
    <div class="filter-bar">
      <h2>Alle Rezepte</h2>
      <div class="filter">
        <label for="category">Kategorie:</label>
        <!-- Dropdown to filter recipes by category -->
        <select v-model="selectedCategory" @change="filterRecipes">
          <option value="">Alle</option>
          <option value="KUCHEN">KUCHEN</option>
          <option value="NUDELN">NUDELN</option>
          <option value="REIS">REIS</option>
          <option value="FLEISCH">FLEISCH</option>
          <option value="VEGETARISCH">VEGETARISCH</option>
        </select>

        <!-- Buttons for additional filter types -->
        <div class="button-group">
          <button
              :class="{ active: selectedFilterType === 'cheap' }"
              @click="selectFilter('cheap')"
          >
            Günstig (unter 10€)
          </button>
          <button
              :class="{ active: selectedFilterType === 'quick' }"
              @click="selectFilter('quick')"
          >
            Schnell (unter 30 Minuten)
          </button>
          <button
              :class="{ active: selectedFilterType === 'popular' }"
              @click="selectFilter('popular')"
          >
            Beliebt
          </button>
        </div>
      </div>
    </div>

    <!-- Loading state display -->
    <div v-if="isLoading" class="loading">Lade Rezepte...</div>

    <!-- Recipe grid -->
    <div class="recipe-grid" v-else>
      <MenuCard
          v-for="recipe in recipes"
          :key="recipe.id"
          :recipe="recipe"
          :to="{ name: 'Detail', params: { id: recipe.id } }"
      />
    </div>

    <!-- Error message display -->
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <!-- Pagination controls -->
    <div class="pagination" v-if="!isLoading">
      <button @click="prevPage" :disabled="currentPage === 0">«</button>
      <span>Seite {{ currentPage + 1 }} von {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages - 1">
        »
      </button>
    </div>
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
      /**
       * List of recipes to display.
       * @type {Array<Object>}
       */
      recipes: [],

      /**
       * Selected category for filtering recipes.
       * @type {string}
       */
      selectedCategory: "",

      /**
       * Selected filter type (e.g., "cheap", "quick").
       * @type {string}
       */
      selectedFilterType: "",

      /**
       * Error message to display if fetching fails.
       * @type {string}
       */
      errorMessage: "",

      /**
       * Whether the recipes are being loaded.
       * @type {boolean}
       */
      isLoading: false,

      /**
       * Cache for API responses to avoid redundant requests.
       * @type {Object}
       */
      cache: {},

      /**
       * Current page in pagination.
       * @type {number}
       */
      currentPage: 0,

      /**
       * Number of recipes per page.
       * @type {number}
       */
      pageSize: 10,

      /**
       * Total number of pages available.
       * @type {number}
       */
      totalPages: 1,

      /**
       * Sorting field for recipes.
       * @type {string}
       */
      sortBy: "createdAt",

      /**
       * Sorting direction (e.g., "ASC" or "DESC").
       * @type {string}
       */
      direction: "DESC",
    };
  },
  methods: {
    /**
     * Constructs a unique cache key for the current filter and pagination state.
     * @returns {string} The cache key.
     */
    getCacheKey() {
      return `category:${this.selectedCategory || "ALL"}|filter:${
          this.selectedFilterType || "NONE"
      }|page:${this.currentPage}|size:${this.pageSize}|sortBy:${
          this.sortBy
      }|direction:${this.direction}`;
    },

    /**
     * Fetches recipes based on the current filter and pagination state.
     * Caches the results for efficiency.
     */
    async fetchRecipes() {
      this.isLoading = true;
      this.errorMessage = "";
      const cacheKey = this.getCacheKey();

      // Use cache if available
      if (this.cache[cacheKey]) {
        this.setRecipeDataFromResponse(this.cache[cacheKey]);
        this.isLoading = false;
        return;
      }

      try {
        const token = localStorage.getItem("token");
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          sortBy: this.sortBy,
          direction: this.direction,
        };

        if (this.selectedCategory) {
          params.category = this.selectedCategory.toUpperCase();
        }
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

        this.setRecipeDataFromResponse(response.data);
        this.cache[cacheKey] = response.data;
      } catch (error) {
        this.errorMessage = "Fehler beim Laden der Rezepte.";
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },

    /**
     * Updates the recipe data from the API response.
     * @param {Object} data The API response data.
     */
    setRecipeDataFromResponse(data) {
      const recipeList = data.content || data;
      this.recipes = recipeList;

      this.totalPages = data.totalPages || 1;

      for (const recipe of this.recipes) {
        if (recipe.recipeImagePath !== null) {
          fetchRecipeImage(recipe);
        }
      }
    },

    /**
     * Filters recipes based on the selected category.
     */
    filterRecipes() {
      this.currentPage = 0;
      this.fetchRecipes();
    },

    /**
     * Toggles a filter type (e.g., "cheap", "quick").
     * @param {string} filterType The filter type to toggle.
     */
    selectFilter(filterType) {
      this.selectedFilterType =
          this.selectedFilterType === filterType ? "" : filterType;
      this.filterRecipes();
    },

    /**
     * Navigates to the previous page in the recipe list.
     */
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchRecipes();
      }
    },

    /**
     * Navigates to the next page in the recipe list.
     */
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchRecipes();
      }
    },
  },
  async mounted() {
    await this.fetchRecipes();
  },
};
</script>

<style scoped>
.recipe-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-bar {
  background: #f9f9f9;
  padding: 10px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.filter label {
  font-weight: bold;
}

.button-group button {
  margin: 0 5px;
  padding: 8px 12px;
  border: none;
  background: #eee;
  border-radius: 5px;
  cursor: pointer;
}

.button-group button.active {
  background-color: #333;
  color: #fff;
}

.button-group button:hover {
  background: #ccc;
}

.recipe-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.error {
  color: red;
  margin-top: 20px;
  text-align: center;
}

.loading {
  text-align: center;
  margin: 20px 0;
  font-size: 1.2em;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0;
  gap: 10px;
}

.pagination button {
  border: none;
  background: #333;
  color: #fff;
  padding: 8px 12px;
  border-radius: 50%;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  background: #555;
}

.pagination button:disabled {
  background: #aaa;
  cursor: not-allowed;
}

.pagination span {
  font-weight: bold;
}
</style>
