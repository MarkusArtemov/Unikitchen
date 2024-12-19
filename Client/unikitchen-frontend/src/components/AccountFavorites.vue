<template>
  <div class="section">
    <h3>Favoriten</h3>
    <div class="recipes-grid">
      <MenuCard
        v-for="favorite in favoriteRecipes"
        :key="favorite.id"
        :recipe="favorite"
        :to="{ name: 'Detail', params: { id: favorite.id } }"
      />
    </div>
  </div>
</template>

<script>
import { fetchFavorites, fetchRecipeImage } from "@/services/RecipeService";
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
    // Load favorites
    const favorites = await fetchFavorites(this.token);
    this.favoriteRecipes = favorites;

    // Fetch images for each recipe
    for (const fav of this.favoriteRecipes) {
      await fetchRecipeImage(fav);
    }
  },
};
</script>

<style scoped>
.section {
  padding: 20px;
}
h3 {
  text-align: center;
}
.recipes-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}
</style>
