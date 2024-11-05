<template>
  <div class="account-page">
    <div class="profile-section">
      <div class="profile-circle">
        <img v-if="profileImage" :src="profileImage" alt="Profilbild" />
        <div v-else class="default-avatar">🧑‍💻</div>
        <input type="file" @change="onProfileImageChange" />
      </div>
    </div>

    <div class="menu">
      <button @click="setActiveSection('favorites')">Favoriten</button>
      <button @click="setActiveSection('myRecipes')">Meine Rezepte</button>
      <button @click="setActiveSection('account')">Account</button>
      <button @click="setActiveSection('createRecipe')">Neues Rezept</button>
    </div>

    <div class="content">
      <div v-if="activeSection === 'favorites'">
        <h3>Favoriten</h3>
        <ul>
          <li v-for="recipe in favoriteRecipes" :key="recipe.id">{{ recipe.name }}</li>
        </ul>
      </div>

      <div v-else-if="activeSection === 'myRecipes'">
        <h3>Meine Rezepte</h3>
        <ul>
          <li v-for="recipe in myRecipes" :key="recipe.id">{{ recipe.name }}</li>
        </ul>
      </div>

      <div v-else-if="activeSection === 'account'">
        <h3>Kontoinformationen</h3>
        <p>Name: {{ user.name }}</p>
        <p>Vorname: {{ user.firstName }}</p>
        <p>Email: {{ user.email }}</p>
        <button @click="resetPassword">Passwort vergessen (ändern)</button>
      </div>

      <div v-else-if="activeSection === 'createRecipe'">
        <h3>Neues Rezept erstellen</h3>
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
            <textarea id="ingredients" v-model="recipe.ingredients" required></textarea>
          </div>

          <div class="form-group">
            <label for="instructions">Zubereitung:</label>
            <textarea id="instructions" v-model="recipe.instructions" required></textarea>
          </div>
          <button type="submit">Rezept erstellen</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>

</script>

<style scoped>
.account-page {
  padding: 20px;
  text-align: center;
}

.profile-section {
  margin-bottom: 20px;
}

.profile-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  overflow: hidden;
  position: relative;
}

.profile-circle img {
  width: 100%;
  height: auto;
}

.default-avatar {
  font-size: 3em;
}

.menu {
  margin-bottom: 20px;
}

.menu button {
  margin: 0 10px;
  padding: 10px 20px;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 5px;
  cursor: pointer;
}

.menu button:hover {
  background-color: #0056b3;
}

.content {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

form {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

label {
  margin-bottom: 5px;
}

input, textarea, select {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button[type="submit"] {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

button[type="submit"]:hover {
  background-color: #218838;
}
</style>
