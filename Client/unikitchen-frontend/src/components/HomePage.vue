<template>
  <div class="home-container">
    <header>
      <h1>Willkommen in der Uniküchen!</h1>
      <router-link to="/login" class="login-button">Login/Register</router-link>
    </header>

    <main>
      <section class="current-section">
        <h2>Aktuell:</h2>
        <div class="cards-container">
          <div class="card" v-for="recipe in recipes" :key="recipe.id">
            <h3>{{ recipe.name }}</h3>
            <p>Dauer: {{ recipe.duration }} Minuten</p>
            <p>Preis: {{ recipe.price }} €</p>
            <p>Schwierigkeitsgrad: {{ recipe.difficultyLevel }}</p>
          </div>
        </div>
      </section>

      <section class="text-section">
        <div class="text-container">
          <h2>Text</h2>
        </div>
      </section>

      <section class="categories-section">
        <h2>Kategorien:</h2>
        <div class="cards-container">
          <div class="card" v-for="i in 4" :key="`category-${i}`">
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'HomePage',
  data() {
    return {
      recipes: [],
    };
  },
  created() {
    this.fetchRecipes();
  },
  methods: {
    async fetchRecipes() {
      try {
        const response = await axios.get('/api/recipes');
        this.recipes = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Rezepte:', error);
      }
    },
  },
};
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

h1 {
  margin: 0;
  font-size: 2rem;
}

.login-button {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  text-decoration: none;
  border-radius: 5px;
}

.login-button:hover {
  background-color: #0056b3;
}

section {
  margin-bottom: 40px;
}

h2 {
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.card {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;
  min-height: 150px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.text-section {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.text-container {
  max-width: 800px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .cards-container {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }

  header {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
}
</style>