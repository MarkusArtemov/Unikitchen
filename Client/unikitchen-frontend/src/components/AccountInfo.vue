<template>
  <div class="section">
    <h3>Kontoinformationen</h3>
    <p>Benutzername: {{ user.username }}</p>

    <div class="bio-section">
      <label for="bio">Bio:</label>
      <textarea id="bio" v-model="updatedBio"></textarea>
      <button @click="updateBio">Bio speichern</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AccountInfo",
  props: {
    user: {
      type: Object,
      required: true,
    },
    token: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      updatedBio: this.user.bio || "",
    };
  },
  watch: {
    "user.bio"(newBio) {
      this.updatedBio = newBio || "";
    },
  },
  methods: {
    async updateBio() {
      try {
        await axios.put(
            "http://localhost:8080/api/users/current-user",
            { bio: this.updatedBio },
            {
              headers: { Authorization: `Bearer ${this.token}` },
            }
        );
        this.$emit("bio-updated", this.updatedBio);
        console.log("Bio updated successfully");
      } catch (error) {
        console.error("Error updating bio:", error);
      }
    },
  },
};
</script>

