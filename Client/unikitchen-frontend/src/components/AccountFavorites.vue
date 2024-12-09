<template>
  <div class="section">
    <h3>Favoriten</h3>
    <div class="recipes-grid">
      <MenuCard
        v-for="favorite in favoriteRecipes"
        :key="favorite.recipeId"
        :recipe="{
          id: favorite.recipeId,
          name: favorite.recipeName,
          price: favorite.price,
          duration: favorite.duration,
          difficultyLevel: favorite.difficultyLevel,
          category: favorite.category,
          imageSrc: favorite.imageSrc,
          averageRating: favorite.averageRating,
          ratingCount: favorite.ratingCount
        }"
        :to="{ name: 'Detail', params: { id: favorite.recipeId } }"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import MenuCard from "@/components/MenuCard.vue";

export default {
  name: "AccountFavorites",
  components: { MenuCard },
  props: {
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      favoriteRecipes: [],
    };
  },
  async created() {
    await this.loadFavoriteRecipes();
  },
  methods: {
    async loadFavoriteRecipes() {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/favorites/current",
          {
            headers: { Authorization: `Bearer ${this.token}` },
          }
        );
        this.favoriteRecipes = response.data;

        for (const favorite of this.favoriteRecipes) {
          const imagePath = await this.fetchRecipeImage(favorite.recipeId);
          favorite.imageSrc = imagePath;
        }
      } catch (error) {
        console.error("Fehler beim Laden der Favoriten:", error);
      }
    },
    async fetchRecipeImage(recipeId) {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/recipes/${recipeId}/recipe-image`,
          {
            responseType: "arraybuffer",
          }
        );
        return `data:image/jpeg;base64,${btoa(
          new Uint8Array(response.data).reduce(
            (data, byte) => data + String.fromCharCode(byte),
            ""
          )
        )}`;
      } catch (error) {
        console.error(
          `Fehler beim Laden des Bildes für Rezept ${recipeId}:`,
          error
        );
        return null;
      }
    },
  },
};
</script>
