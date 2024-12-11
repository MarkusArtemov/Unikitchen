<template>
  <div class="login-container">
    <h2 class="title">Login</h2>
    <form @submit.prevent="handleLogin" class="form">
      <div class="form-group">
        <label for="username" class="label">Benutzername:</label>
        <input
          type="text"
          id="username"
          v-model="username"
          required
          class="input"
          placeholder="Dein Benutzername"
        />
      </div>
      <div class="form-group">
        <label for="password" class="label">Passwort:</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          class="input"
          placeholder="Dein Passwort"
        />
      </div>
      <button type="submit" class="button">Einloggen</button>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "LoginPage",
  data() {
    return {
      username: "",
      password: "",
      errorMessage: "",
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.post("/api/auth/login", {
          username: this.username,
          password: this.password,
        });
        localStorage.setItem("token", response.data.token);

        this.$router.push("/");
      } catch (error) {
        this.errorMessage = error.response?.data.message || "Anmeldefehler";
      }
    },
  },
};
</script>

<style scoped>
/* Globale Box-Sizing-Einstellung für Konsistenz */
* {
  box-sizing: border-box;
}

.login-container {
  max-width: 400px;
  margin: 60px auto;
  padding: 30px 25px;
  background-color: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.title {
  text-align: center;
  margin-bottom: 20px;
  color: #333333;
}

.form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

.label {
  display: block;
  margin-bottom: 5px;
  color: #555555;
  font-weight: 500;
}

.input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #cccccc;
  border-radius: 4px;
  transition: border-color 0.3s;
  font-size: 14px;
  /* Sicherstellen, dass das Padding nicht die Gesamtbreite beeinflusst */
}

.input:focus {
  border-color: #007bff;
  outline: none;
}

.button {
  padding: 12px;
  background-color: #007bff;
  color: #ffffff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #0056b3;
}

.error {
  margin-top: 15px;
  color: #d9534f;
  text-align: center;
  font-weight: 500;
}
</style>
