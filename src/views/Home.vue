<template>
  <div class="home">
    <word-finder />
  </div>
</template>

<script>
// @ is an alias to /src
import WordFinder from '@/components/WordFinder.vue'

export default {
  name: 'Home',
  components: {
    WordFinder
  }
}
</script>
