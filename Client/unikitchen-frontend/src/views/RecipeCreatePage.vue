<template>
  <div class="recipe-create-page">
    <h1>Neues Rezept erstellen</h1>
    <form @submit.prevent="submitRecipe">
      <div class="form-group">
        <label for="name">Rezeptname:</label>
        <input type="text" id="name" v-model="recipe.name" required />
      </div>

      <div class="form-group">
        <label for="image">Bild-URL:</label>
        <input type="url" id="image" v-model="recipe.image" required />
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

<script></script>

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
input[type="url"],
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
</style>
