<template>
  <div class="recipe-create-page">
    <h1>Neues Rezept erstellen</h1>
    <form @submit.prevent="submitRecipe">
      <div class="form-group">
        <label for="name">Rezeptname:</label>
        <input type="text" id="name" v-model="recipe.name" required />
      </div>

      <div class="form-group">
        <label for="image">Bild hochladen:</label>
        <input
          type="file"
          id="image"
          accept="image/*"
          @change="onImageSelected"
          required
        />

        <!-- Bildvorschau -->
        <div v-if="previewImage" class="image-preview">
          <img :src="previewImage" alt="Bildvorschau" />
        </div>

        <!-- Fehlermeldung, wenn kein Bild vorhanden ist -->
        <p v-if="imageError" class="error-message">{{ imageError }}</p>
      </div>

      <div class="form-group">
        <label for="duration">Dauer (in Minuten):</label>
        <input type="number" id="duration" v-model="recipe.duration" required />
      </div>

      <div class="form-group">
        <label for="difficulty">Schwierigkeitsgrad:</label>
        <select id="difficulty" v-model="recipe.difficulty" required>
          <option value="" disabled>Wählen Sie...</option>
          <option value="einfach">Einfach</option>
          <option value="mittel">Mittel</option>
          <option value="schwierig">Schwierig</option>
        </select>
      </div>

      <div class="form-group">
        <label for="ingredients">Zutaten (kommagetrennt):</label>
        <textarea
          id="ingredients"
          v-model="recipe.ingredients"
          required
        ></textarea>
      </div>

      <div class="form-group">
        <label for="instructions">Zubereitung:</label>
        <textarea
          id="instructions"
          v-model="recipe.instructions"
          required
        ></textarea>
      </div>

      <button type="submit">Rezept erstellen</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      recipe: {
        name: "",
        duration: "",
        difficulty: "",
        ingredients: "",
        instructions: "",
        imageFile: null,
      },
      previewImage: null,
      imageError: null,
    };
  },
  methods: {
    onImageSelected(event) {
      const file = event.target.files[0];
      if (file) {
        this.recipe.imageFile = file;
        this.previewImage = URL.createObjectURL(file);
        this.imageError = null;
      } else {
        this.recipe.imageFile = null;
        this.previewImage = null;
      }
    },
    submitRecipe() {
      if (!this.recipe.imageFile) {
        this.imageError = "Bitte wählen Sie ein Bild aus.";
        return;
      }

      const formData = new FormData();
      formData.append("name", this.recipe.name);
      formData.append("duration", this.recipe.duration);
      formData.append("difficulty", this.recipe.difficulty);
      formData.append("ingredients", this.recipe.ingredients);
      formData.append("instructions", this.recipe.instructions);
      formData.append("image", this.recipe.imageFile);

      // Hier Ihren API-Call mittels axios/fetch hinzufügen
    },
  },
};
</script>

<style scoped>
.recipe-create-page {
  padding: 20px;
}

h1 {
  text-align: center;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input[type="text"],
input[type="number"],
select,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

textarea {
  resize: vertical;
}

button {
  display: block;
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
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

.error-message {
  color: red;
  margin-top: 5px;
  font-size: 14px;
}
</style>
