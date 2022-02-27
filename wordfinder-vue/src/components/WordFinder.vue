<template>
  <div class="container-fluid p-3 my-3">
    <h1>Word Finder</h1>
    <p>
      Enter letters to find all English words that can be made from the letters.
    </p>
    <p>Enter a pattern to find all words that fit the pattern.</p>
    <p>
      Enter both to find word that can be made from the letters that fit the
      pattern.
    </p>
    <div id="app">
      <div id="letters">
        Letters:
        <input
          @input="getWords"
          v-model="characters"
          id="charactersInput"
          class="foundword"
        />
        <button class="btn btn-outline-dark" @click="clearLetters">
          Clear
        </button>
      </div>
      <div id="pattern">
        Pattern:
        <input @input="getWords" v-model="pattern" id="patternInput" />
        <button class="btn btn-outline-dark" @click="clearPattern">
          Clear
        </button>
        <div class="helpertext">Use an underscore to match any letter</div>
      </div>
      <div class="error" v-if="errorMsg">
        <pre>{{ errorMsg }}</pre>
      </div>
      <div id="foundwords" v-else>
        <span v-show="totalFound > 0"
          >Words that can be made from these letters:
          <button
            class="btn btn-outline-dark"
            @click.prevent="prev"
            :disabled="isPrevDisabled"
          >
            Prev
          </button></span
        >
        <span v-show="totalFound > words.length"
          >( {{ indexOfFirst + 1 }} to {{ indexOfFirst + words.length }} of
          {{ totalFound }} )</span
        >
        <span
          v-show="
            indexOfFirst == 0 && words.length > 0 && totalFound == words.length
          "
          >( {{ totalFound }} total )</span
        >
        <span v-show="totalFound > 0"
          ><button
            class="btn btn-outline-dark"
            @click.prevent="next"
            :disabled="isNextDisabled"
          >
            Next
          </button></span
        >
        <br />
        <br />
        <div>
          <span
            class="text-monospace"
            v-for="(word, index) in words"
            :key="index"
          >
            <hr
              v-if="
                index > 0 &&
                words[index].length > 3 &&
                words[index - 1].length != words[index].length
              "
            />
            {{ word }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import wordFinderService from "@/services/WordFinderService.js";

export default {
  name: "WordFinder",
  data() {
    return {
      characters: "",
      words: [],
      indexOfFirst: 0,
      totalFound: 0,
      maxToReturn: 1000,
      minWordLen: 3,
      badRequest: false,
      errorMsg: "",
      pattern: "",
    };
  },
  computed: {
    isPrevDisabled: function () {
      return this.indexOfFirst == 0;
    },
    isNextDisabled: function () {
      return !(this.indexOfFirst + this.words.length < this.totalFound);
    },
  },
  methods: {
    getWords: function () {
      this.characters = this.scrubChars(this.characters);
      this.pattern = this.scrubChars(this.pattern, true);

      if (this.characters.length == 0 && this.pattern.length == 0) {
        // No characters or pattern
        return;
      }

      wordFinderService
        .getWords(
          this.characters,
          this.pattern,
          this.indexOfFirst,
          this.maxToReturn
        )
        .then((response) => {
          this.words = response.data.words;
          this.totalFound = response.data.totalFound;
          this.indexOfFirst = response.data.indexOfFirst;

          // Handle case of number of results getting smaller
          if (this.indexOfFirst > 0 && this.words.length == 0) {
            this.indexOfFirst = 0;
            this.getWords();
          }

          // Clear the previously displayed error message, if any
          this.errorMsg = "";
        })
        .catch((error) => {
          this.errorMsg = error.response.data;
        });
    },
    scrubChars: function (letters, allowUnderscore = false) {
      var scrubbedChars = "";

      for (var i = 0; i < letters.length; ++i) {
        var curChar = letters.charAt(i).toUpperCase();
        if (
          (curChar >= "A" && curChar <= "Z") ||
          (curChar === "_" && allowUnderscore)
        ) {
          scrubbedChars += curChar;
        }
      }

      return scrubbedChars;
    },
    prev: function () {
      this.indexOfFirst -= this.maxToReturn;
      if (this.indexOfFirst < 0) {
        this.indexOfFirst = 0;
      }
      this.getWords();
    },
    next: function () {
      this.indexOfFirst += this.words.length;
      this.getWords();
    },
    clearLetters: function () {
      this.characters = "";
      this.doCommonClear("charactersInput");
    },
    clearPattern: function () {
      this.pattern = "";
      this.doCommonClear("patternInput");
    },
    doCommonClear: function (inputId) {
      this.words = [];
      this.indexOfFirst = 0;
      this.totalFound = 0;
      this.errorMsg = "";
      document.getElementById(inputId).focus();
      this.getWords();
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1 {
  margin: 20px 0 0;
}

button {
  margin: 0 10px;
}

#filter {
  padding-top: 10px;
}

#pattern {
  padding-top: 10px;
}

#foundwords {
  padding-top: 30px;
}

.error {
  color: red;
  font-weight: bold;
  font-size: 1.1em;
}

.helpertext {
  font-style: italic;
  font-size: 80%;
}

.text-monospace {
  font-family: monospace;
}
</style>
