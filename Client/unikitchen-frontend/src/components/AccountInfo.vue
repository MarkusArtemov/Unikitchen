<template>
  <div class="section">
    <h3>Kontoinformationen</h3>
    <!-- Display username -->
    <p>Benutzername: {{ user.username }}</p>

    <div class="bio-section">
      <label for="bio">Bio:</label>
      <!-- Textarea to update bio -->
      <textarea id="bio" v-model="updatedBio"></textarea>
      <button @click="updateBio">Bio speichern</button>

      <!-- Show success message after bio is updated -->
      <transition name="fade">
        <p v-if="showSuccessMessage" class="success-message">
          Bio wurde erfolgreich gespeichert!
        </p>
      </transition>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AccountInfo",
  components: {},
  props: {
    /**
     * The user object that contains the user's data.
     * @type {Object}
     * @required
     */
    user: {
      type: Object,
      required: true,
    },
    /**
     * The authentication token for making API requests.
     * @type {string}
     * @required
     */
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      /**
       * The updated bio text that the user is editing.
       * @type {string}
       */
      updatedBio: this.user.bio || "",

      /**
       * Boolean flag to control the display of the success message.
       * @type {boolean}
       */
      showSuccessMessage: false,

      /**
       * Timeout identifier for controlling the success message display duration.
       * @type {number | null}
       */
      successMessageTimeout: null,
    };
  },
  watch: {
    /**
     * Watcher to sync the bio data if it is updated externally.
     * @param {string} newBio - The new bio value.
     */
    "user.bio"(newBio) {
      this.updatedBio = newBio || "";
    },
  },
  methods: {
    /**
     * Updates the user's bio and shows a success message.
     * @async
     * @returns {Promise<void>}
     */
    async updateBio() {
      try {
        // Send updated bio to the server
        await axios.put(
            "/api/users/current-user",
            {bio: this.updatedBio},
            {
              headers: {Authorization: `Bearer ${this.token}`},
            }
        );

        // Emit an event to notify the parent component about the bio update
        this.$emit("bio-updated", this.updatedBio);

        // Show success message
        this.showSuccessMessage = true;

        // Reset the success message after 3 seconds
        if (this.successMessageTimeout) {
          clearTimeout(this.successMessageTimeout);
        }
        this.successMessageTimeout = setTimeout(() => {
          this.showSuccessMessage = false;
        }, 3000);
      } catch (error) {
        console.error("Error updating bio:", error);
      }
    },
  },
  beforeUnmount() {
    // Cleanup timeout when component is about to be destroyed
    if (this.successMessageTimeout) {
      clearTimeout(this.successMessageTimeout);
    }
  },
};
</script>

<style scoped>
.section {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

h3 {
  margin-bottom: 20px;
  font-size: 1.6em;
  font-weight: 600;
  color: #333;
}

p {
  margin-bottom: 20px;
  font-size: 1.1em;
  color: #555;
}

.bio-section {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin: 0 auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  max-width: 600px;
  text-align: left;
}

.bio-section label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  font-size: 1.1em;
  color: #333;
}

.bio-section textarea {
  width: 100%;
  height: 120px;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  resize: vertical;
  font-size: 1em;
  font-family: inherit;
  margin-bottom: 15px;
  color: #333;
}

.bio-section button {
  padding: 10px 20px;
  background-color: #0069d9;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1em;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.bio-section button:hover {
  background-color: #0053a6;
}

.success-message {
  margin-top: 15px;
  font-size: 1em;
  color: #28a745;
  font-weight: 500;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
