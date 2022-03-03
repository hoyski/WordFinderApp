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
        <input @input="getWords" v-model="characters" id="charactersInput" />
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

      <!--
				Definition display
			-->
      <div v-show="showDefinition" id="definition">
        <div id="close-button" @click="showDefinition = false">Close</div>
        <div v-if="definition != undefined">
          <strong>{{ definition.word }}</strong> -
          <i>{{ definition.phonetic }}</i
          ><br />
          <div
            v-for="(meaning, index) in definition.meanings"
            v-bind:key="index"
          >
            <i>{{ meaning.partOfSpeech }}</i
            ><br />
            <ul>
              <li
                v-for="(def, defIndex) in meaning.definitions"
                v-bind:key="defIndex"
              >
                {{ def.definition }}<br />
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!--
				Found words
			-->
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
          <span v-for="(word, index) in words" :key="index">
            <hr
              v-if="
                index > 0 &&
                words[index].length > 3 &&
                words[index - 1].length != words[index].length
              "
            />
            <span @click="getDefinition(word)" class="foundWord">{{
              word
            }}</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import wordFinderService from "@/services/WordFinderService.js";
import definitionService from "@/services/DefinitionService.js";

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
      showDefinition: false,
      definition: undefined,
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
    getDefinition(word) {
      this.definition = undefined;
      definitionService
        .getDefinition(word)
        .then((response) => {
          // Collapse all of the definitions into a single definition
          this.definition = {};
          this.definition.word = response.data[0].word;
          this.definition.meanings = [];
          for (let def of response.data) {
            for (let meaning of def.meanings) {
              this.definition.meanings.push(meaning);
            }
          }
        })
        .catch(() => {
          this.definition = { word: "No definition found for " + word };
        });
      this.showDefinition = true;
      document.getElementById("pattern").scrollIntoView({
        behavior: "smooth",
        block: "end",
        inline: "nearest",
      });
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

#definition {
  border: 1px solid black;
  border-radius: 8px;
  padding: 10px;
}

#close-button {
  font-size: 1.25em;
  float: right;
}

#close-button:hover {
  text-decoration: underline;
}

.foundWord {
  display: inline-block;
  font-family: monospace;
  padding-right: 1em;
}

.foundWord:hover {
  text-decoration: underline;
}
</style>
