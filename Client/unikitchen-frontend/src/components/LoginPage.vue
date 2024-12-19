<template>
  <div class="login-container">
    <h2 class="title">Login</h2>
    <form @submit.prevent="handleLogin" class="form">
      <div class="form-group">
        <!-- Username input field -->
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
        <!-- Password input field -->
        <label for="password" class="label">Password:</label>
        <input
            type="password"
            id="password"
            v-model="password"
            required
            class="input"
            placeholder="Dein Passwort"
        />
      </div>
      <!-- Login button -->
      <button type="submit" class="button">Login</button>
      <!-- Error message display if login fails -->
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

// AuthRequest: {username, password, bio} - bio can be empty
export default {
  name: "LoginPage",
  data() {
    return {
      /**
       * The username used for login.
       * @type {string}
       */
      username: "",

      /**
       * The password used for login.
       * @type {string}
       */
      password: "",

      /**
       * The error message displayed on login failure.
       * @type {string}
       */
      errorMessage: "",
    };
  },
  methods: {
    /**
     * Handles the user login. Sends a request to the API to validate the login
     * and stores the received token and user info in local storage.
     *
     * @async
     * @returns {Promise<void>}
     */
    async handleLogin() {
      try {
        // Authentication request
        const authRequest = {
          username: this.username,
          password: this.password,
          bio: null, // optional bio field, here it is empty
        };

        // Send authentication request to the API
        const response = await axios.post("/api/auth/login", authRequest);

        // Store the token in local storage
        localStorage.setItem("token", response.data.token);

        // Optionally, store user info in local storage
        localStorage.setItem("user", JSON.stringify(response.data.userInfo));

        // Redirect to the homepage after successful login
        this.$router.push("/");
      } catch (error) {
        // On error: Display error message
        this.errorMessage = error.response?.data.message || "Anmeldefehler";
      }
    },
  },
};
</script>

<style scoped>
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
  font-size: 14px;
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
