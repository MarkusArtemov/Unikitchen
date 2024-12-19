<template>
  <!-- Container for the error page -->
  <div class="ErrorPage">
    <!-- Heading displaying the error message -->
    <h1>404 - Seite nicht gefunden</h1>

    <!-- Informative message and navigation link to redirect user back to the homepage -->
    <p>
      Die von dir angeforderte Seite existiert nicht.
      <router-link to="/">Zurück zur Startseite</router-link>
    </p>
  </div>
</template>


<script>
export default {
  name: "ErrorPage",
};
</script>


<style scoped>
h1 {
  font-size: 3em;
  color: black;
}
p {
  font-size: 1.2em;
  text-align: center;
}
</style>
