<template>
  <div class="section">
    <h3>Meine Rezepte</h3>

    <!-- Anzeige der Rezepte -->
    <div class="recipes-grid">
      <MenuCard
        v-for="recipe in myRecipes"
        :key="recipe.id"
        :recipe="recipe"
        :to="{ name: 'Detail', params: { id: recipe.id } }"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import MenuCard from "@/components/MenuCard.vue";

export default {
  name: "AccountMyRecipes",
  components: { MenuCard },
  props: {
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      myRecipes: [],
      loading: true, // Beim Starten ist loading = true
      error: false, // Fehlerzustand zunächst false
    };
  },
  async created() {
    await this.loadMyRecipes();
  },
  methods: {
    async loadMyRecipes() {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/recipes/user",
          {
            headers: { Authorization: `Bearer ${this.token}` },
          }
        );
        this.myRecipes = response.data;

        for (const recipe of this.myRecipes) {
          const imagePath = await this.fetchRecipeImage(recipe.id);
          recipe.imageSrc = imagePath;
        }

        // Nachdem die Daten geladen wurden, loading auf false setzen
        this.loading = false;
      } catch (error) {
        console.error("Fehler beim Laden der eigenen Rezepte:", error);
        // Falls ein Fehler auftritt, loading auf false und error auf true
        this.loading = false;
        this.error = true;
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
