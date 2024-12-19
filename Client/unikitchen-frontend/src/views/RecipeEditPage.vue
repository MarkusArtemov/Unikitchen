<template>
  <div class="recipe-edit-page">
    <h1>Rezept bearbeiten</h1>
    <!-- Recipe edit form -->
    <form @submit.prevent="updateRecipeData">
      <!-- Input field for recipe name -->
      <div class="form-group">
        <label for="name">Rezeptname:</label>
        <input type="text" id="name" v-model="recipe.name" required />
      </div>

      <!-- Input field for recipe price -->
      <div class="form-group">
        <label for="price">Preis (in €):</label>
        <input
            type="number"
            id="price"
            v-model="recipe.price"
            required
            step="0.01"
        />
      </div>

      <!-- Input field for recipe duration -->
      <div class="form-group">
        <label for="duration">Dauer (in Minuten):</label>
        <input
            type="number"
            id="duration"
            v-model="recipe.duration"
            required
            step="1"
        />
      </div>

      <!-- Textarea for recipe preparation instructions -->
      <div class="form-group">
        <label for="preparation">Zubereitung:</label>
        <textarea
            id="preparation"
            v-model="recipe.preparation"
            required
        ></textarea>
      </div>

      <!-- Dynamic ingredient list management -->
      <div class="form-group">
        <label>Zutaten:</label>
        <ul>
          <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
            <input v-model="ingredient.name" placeholder="Zutat" required />
            <input
                type="number"
                v-model="ingredient.quantity"
                placeholder="Menge"
                required
                step="0.01"
            />
            <input
                v-model="ingredient.unit"
                placeholder="Einheit (z.B. g, ml)"
                required
            />
            <button
                type="button"
                @click="removeIngredient(index)"
                class="remove-button"
            >
              Entfernen
            </button>
          </li>
        </ul>
        <button type="button" @click="addIngredient" class="add-button">
          Zutat hinzufügen
        </button>
      </div>

      <!-- Dropdown for recipe category -->
      <div class="form-group">
        <label for="category">Kategorie:</label>
        <select id="category" v-model="recipe.category" required>
          <option v-for="cat in categories" :key="cat" :value="cat">
            {{ cat }}
          </option>
        </select>
      </div>

      <!-- Dropdown for recipe difficulty level -->
      <div class="form-group">
        <label for="difficultyLevel">Schwierigkeitsgrad:</label>
        <select id="difficultyLevel" v-model="recipe.difficultyLevel" required>
          <option value="EINFACH">EINFACH</option>
          <option value="MITTEL">MITTEL</option>
          <option value="SCHWIERIG">SCHWIERIG</option>
        </select>
      </div>

      <!-- File input for uploading an updated recipe image -->
      <div class="form-group">
        <label for="image">Bild aktualisieren (optional):</label>
        <input
            type="file"
            id="image"
            @change="onImageChange"
            accept="image/*"
        />
        <div v-if="displayImageSrc" class="image-preview">
          <img :src="displayImageSrc" alt="Aktuelles Bild" />
        </div>
      </div>

      <!-- Submit button for saving changes -->
      <button type="submit" class="submit-button">Änderungen speichern</button>
    </form>
  </div>
</template>

<script>
import {
  fetchRecipeDetails, // Fetches recipe details from the API
  fetchRecipeImage,   // Fetches the recipe image
  updateRecipe,       // Updates recipe details via the API
  uploadRecipeImage,  // Uploads the updated recipe image
} from "@/services/RecipeService";

export default {
  name: "RecipeEditPage",
  props: {
    /**
     * The ID of the recipe to be edited.
     * @type {string}
     */
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      /**
       * Authorization token for API requests.
       * @type {string}
       */
      token: localStorage.getItem("token"),

      /**
       * Available recipe categories.
       * @type {string[]}
       */
      categories: ["VEGETARISCH", "FLEISCH", "KUCHEN", "NUDELN", "REIS"],

      /**
       * Current recipe image file or URL.
       * @type {File|string|null}
       */
      recipeImage: null,

      /**
       * Temporary object URL for the image preview.
       * @type {string|null}
       */
      objectURL: null,

      /**
       * Recipe details being edited.
       * @type {Object}
       */
      recipe: {
        id: null,
        name: "",
        price: null,
        duration: null,
        preparation: "",
        ingredients: [],
        category: "",
        difficultyLevel: "",
      },
    };
  },
  async created() {
    // Fetch initial recipe details on component creation
    const details = await fetchRecipeDetails(this.token, this.id);
    Object.assign(this.recipe, details);

    // Load recipe image if available
    await fetchRecipeImage(details);
    if (details.imageSrc) {
      this.recipeImage = details.imageSrc;
    }
  },
  computed: {
    /**
     * Displays the image source for preview.
     * @returns {string|null} The object URL or recipe image URL.
     */
    displayImageSrc() {
      return (
          this.objectURL ||
          (typeof this.recipeImage === "string" ? this.recipeImage : null)
      );
    },
  },
  watch: {
    /**
     * Watches for changes in the recipe image and updates the object URL.
     * @param {File|string} newVal The new value of recipeImage.
     */
    recipeImage(newVal) {
      if (this.objectURL) {
        URL.revokeObjectURL(this.objectURL);
        this.objectURL = null;
      }

      if (newVal instanceof File) {
        this.objectURL = URL.createObjectURL(newVal);
      }
    },
  },
  beforeUnmount() {
    // Revoke any object URLs when the component is destroyed
    if (this.objectURL) {
      URL.revokeObjectURL(this.objectURL);
    }
  },
  methods: {
    /**
     * Adds a new ingredient to the ingredient list.
     */
    addIngredient() {
      this.recipe.ingredients.push({ name: "", quantity: null, unit: "" });
    },

    /**
     * Removes an ingredient from the ingredient list.
     * @param {number} index The index of the ingredient to remove.
     */
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },

    /**
     * Handles the change event for the recipe image file input.
     * @param {Event} event The input change event.
     */
    onImageChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.recipeImage = file;
      }
    },

    /**
     * Updates the recipe data by sending the new details to the API.
     * If an image file is provided, it uploads the image as well.
     */
    async updateRecipeData() {
      const updateRequest = {
        id: this.recipe.id,
        name: this.recipe.name,
        price: this.recipe.price,
        duration: this.recipe.duration,
        difficultyLevel: this.recipe.difficultyLevel,
        category: this.recipe.category,
        preparation: this.recipe.preparation,
        ingredients: this.recipe.ingredients,
      };

      try {
        const updatedRecipe = await updateRecipe(
            this.token,
            this.recipe.id,
            updateRequest
        );

        if (this.recipeImage instanceof File) {
          await uploadRecipeImage(
              this.token,
              updatedRecipe.id,
              this.recipeImage
          );
        }

        alert("Rezept erfolgreich aktualisiert!");
        this.$router.push({ name: "Detail", params: { id: updatedRecipe.id } });
      } catch (error) {
        console.error("Fehler beim Aktualisieren des Rezepts:", error);
        alert("Fehler beim Aktualisieren des Rezepts!");
      }
    },
  },
};
</script>

<style>
.recipe-edit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
h1 {
  text-align: center;
  margin-bottom: 20px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}
.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
textarea {
  resize: vertical;
}
.submit-button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.submit-button:hover {
  background-color: #218838;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
li input {
  margin-right: 10px;
  flex: 1;
}
.add-button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.add-button:hover {
  background-color: #0056b3;
}
.remove-button {
  padding: 5px 10px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.remove-button:hover {
  background-color: #c82333;
}
.image-preview {
  margin-top: 10px;
  text-align: center;
}
.image-preview img {
  max-width: 200px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
</style>
