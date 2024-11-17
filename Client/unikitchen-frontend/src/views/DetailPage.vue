<template>
  <div>
    <h1>{{ recipe.name }}</h1>
    <p>{{ recipe.preparation }}</p>
    <p><strong>Schwierigkeitsgrad:</strong> {{ recipe.difficultyLevel }}</p>
    <p><strong>Zubereitungszeit:</strong> {{ recipe.duration }} Minuten</p>
    <p><strong>Zutaten:</strong></p>
    <ul>
      <li v-for="ingredient in recipe.ingredients" :key="ingredient.id">
        {{ ingredient.name }} - {{ ingredient.amount }}
      </li>
    </ul>
    <p><strong>Bewertungen:</strong> Noch keine Bewertungen</p>

    <!-- Rezeptbild anzeigen-->
    <div v-if="recipe.recipeImagePath">
      <img :src="`/api/recipes/${recipe.id}/recipe-image`" alt="Rezeptbild" />
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
      selectedImage: null,
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
    } catch (error) {
      console.error("Fehler beim Abrufen der Rezeptdetails:", error);
    }
  },
  methods: {
    async uploadRecipeImage(event) {
      const recipeId = this.$route.params.id;
      const formData = new FormData();
      formData.append("image", event.target.files[0]);

      try {
        const token = localStorage.getItem("token");
        const response = await axios.post(`/api/recipes/${recipeId}/upload-recipe-image`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        });
        console.log(response.data);
        this.recipe.recipeImagePath = response.data;
      } catch (error) {
        console.error("Fehler beim Hochladen des Bildes:", error);
      }
    },
  },
};
</script>


<style scoped>

h1 {
  font-size: 2rem;
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
}

ul li {
  margin-bottom: 10px;
}

p {
  margin-bottom: 10px;
  font-size: 1rem;
}

strong {
  font-weight: bold;
}
</style>