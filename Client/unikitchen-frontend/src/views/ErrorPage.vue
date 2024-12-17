<template>
  <div class="ErrorPage">
    <h1>404 - Seite nicht gefunden</h1>
    <p>Die von dir angeforderte Seite existiert nicht.
    <router-link to="/">Zurück zur Startseite</router-link>
    </p>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage',
}
</script>

<style scoped>
.not-found {
  text-align: center;
  padding: 50px;
}
h1 {
  font-size: 3em;
  color: black;
}
p {
  font-size: 1.2em;
  text-align: center;
}
</style>
