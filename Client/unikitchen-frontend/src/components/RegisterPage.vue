<template>
  <div class="register-container">
    <h2 class="title">Registrieren</h2>
    <form @submit.prevent="handleRegister" class="form">
      <div class="form-group">
        <label for="username" class="label">Benutzername:</label>
        <input
          type="text"
          id="username"
          v-model="username"
          required
          class="input"
          placeholder="Wähle einen Benutzernamen"
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
          placeholder="Mindestens 8 Zeichen"
        />
        <small class="helper-text">
          Passwort muss mindestens 8 Zeichen lang sein.
        </small>
      </div>
      <div class="form-group">
        <label for="bio" class="label">Bio (optional):</label>
        <textarea
          id="bio"
          v-model="bio"
          class="textarea"
          placeholder="Kurzbeschreibung..."
          rows="4"
        ></textarea>
      </div>
      <button type="submit" class="button">Registrieren</button>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "RegisterPage",
  data() {
    return {
      username: "",
      password: "",
      bio: "", // Optional bio field
      errorMessage: "",
    };
  },
  methods: {
    async handleRegister() {
      // Client-side validation for minimum password length
      if (this.password.length < 8) {
        this.errorMessage = "Das Passwort muss mindestens 8 Zeichen lang sein.";
        return;
      }
      try {
        // API call to register the user
        const response = await axios.post("/api/auth/register", {
          username: this.username,
          password: this.password,
          bio: this.bio, // Include bio in the request
        });

        // Redirect to login on successful registration
        if (response.status === 201) {
          this.$router.push("/login");
        }
      } catch (error) {
        // Display error message from server response
        this.errorMessage =
          error.response?.data.message || "Registrierungsfehler";
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

.register-container {
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

.input,
.textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #cccccc;
  border-radius: 4px;
  transition: border-color 0.3s;
  font-size: 14px;
}

.input:focus,
.textarea:focus {
  border-color: #28a745;
  outline: none;
}

.textarea {
  resize: vertical;
}

.helper-text {
  display: block;
  margin-top: 5px;
  color: #777777;
  font-size: 12px;
}

.button {
  padding: 12px;
  background-color: #28a745;
  color: #ffffff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #218838;
}

.error {
  margin-top: 15px;
  color: #d9534f;
  text-align: center;
  font-weight: 500;
}
</style>
