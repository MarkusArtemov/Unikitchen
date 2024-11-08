<!-- src/components/RegisterPage.vue -->
<template>
  <div class="register-container">
    <h2>Registrieren</h2>
    <form @submit.prevent="handleRegister">
      <div>
        <label for="username">Benutzername:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div>
        <label for="password">Passwort:</label>
        <input type="password" id="password" v-model="password" required />
        <small>Passwort muss mindestens 8 Zeichen lang sein.</small>
      </div>
      <div>
        <label for="bio">Bio (optional):</label>
        <textarea id="bio" v-model="bio" placeholder="Kurzbeschreibung..."></textarea>
      </div>
      <button type="submit">Registrieren</button>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'RegisterPage',
  data() {
    return {
      username: '',
      password: '',
      bio: '', // Optional bio field
      errorMessage: '',
    };
  },
  methods: {
    async handleRegister() {
      // Client-side validation for minimum password length
      if (this.password.length < 8) {
        this.errorMessage = 'Das Passwort muss mindestens 8 Zeichen lang sein.';
        return;
      }
      try {
        // API call to register the user
        const response = await axios.post('/api/auth/register', {
          username: this.username,
          password: this.password,
          bio: this.bio, // Include bio in the request
        });

        // Redirect to login on successful registration
        if (response.status === 201) {
          this.$router.push('/login');
        }
      } catch (error) {
        // Display error message from server response
        this.errorMessage = error.response?.data.message || 'Registrierungsfehler';
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
