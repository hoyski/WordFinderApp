<template>
  <div class="container-fluid  p-3 my-3">
		<h1>Word Finder</h1>
		<p>Enter letters to find all English words, three letters or longer, that can be made from the letters.</p>
		<div id="app">
			Letters: <input @input="getWords" v-model="characters" id="charactersInput" class="foundword" />
			<button class="btn btn-outline-dark" @click="clear">Clear</button>
			<br /> <br />
			<div class="error" v-if="errorMsg"><pre>{{ errorMsg }}</pre></div>
			<div v-else>
				<span v-show="totalFound > 0">Words that can be made from these letters: <button class="btn btn-outline-dark" @click.prevent="prev" :disabled="isPrevDisabled">Prev</button></span> 
				<span
					v-show="totalFound > words.length">( {{ indexOfFirst + 1 }} to {{ indexOfFirst + words.length }} of {{ totalFound }} )</span> 
				<span
					v-show="indexOfFirst == 0 && words.length > 0 && totalFound == words.length">( {{ totalFound }} total )</span> 
				<span
					v-show="totalFound > 0"><button class="btn btn-outline-dark" @click.prevent="next" :disabled="isNextDisabled">Next</button></span> <br /> <br />
				<div>
					<span class="text-monospace" v-for="(word, index) in words" :key="index">
						<hr v-if="index > 0 && words[index].length > 3 && words[index - 1].length != words[index].length"/>
						{{ word }}
					</span>
				</div>
			</div>
		</div>
  </div>
</template>

<script>

import wordFinderService from '@/services/WordFinderService.js';

export default {
  name: 'WordFinder',
  data() {
    return {
      characters: "",
      words: [],
      indexOfFirst: 0,
      totalFound: 0,
      maxToReturn: 1000,
      minWordLen: 3,
      badRequest: false,
      errorMsg: ""
    }
  },
  computed: {
		isPrevDisabled: function() {
			return this.indexOfFirst == 0;
		},
		isNextDisabled: function() {
			return !((this.indexOfFirst + this.words.length) < this.totalFound);
		}
	},
  methods: {
		getWords: function () {
			// Remove invalid characters from 'characters'
			var scrubbedChars = "";

			for (var i = 0; i < this.characters.length; ++i) {
				var curChar = this.characters.charAt(i).toUpperCase();
				if (curChar >= 'A' && curChar <= 'Z') {
					scrubbedChars += curChar;
				}
			}

			this.characters = scrubbedChars;

      wordFinderService.getWords(this.characters, this.indexOfFirst, this.maxToReturn)
				.then((response) => {
						this.words = response.data.words;
						this.totalFound = response.data.totalFound;
						this.indexOfFirst = response.data.indexOfFirst;

						// Handle case of number of results getting smaller
						if (this.indexOfFirst > 0 && this.words.length == 0)
						{
							this.indexOfFirst = 0;
							this.getWords();
						}

            // Clear the previously displayed error message, if any
            this.errorMsg = '';
					

					// Right pad with non-breaking spaces each word to be as long as the longest word
					// so that they'll stay aligned when wrapped
					/*

					No longer needed since words are divided into sections by length
					if (this.words && this.words.length > 0) {
						var longestWord = this.words[this.words.length - 1].length;
						for (var i = 0; i < this.words.length; ++i) {
							this.words[i] = this.words[i].padEnd(longestWord, '\xA0');
						}
					}
					*/
				})
        .catch((error) => {
          this.errorMsg = `Error retrieving matching words. Error: ${JSON.stringify(error.toJSON(), null, 2)}`;
        });
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
		clear: function () {
			this.characters = "";
			this.words = [];
			this.indexOfFirst = 0;
			this.totalFound = 0;
			document.getElementById("charactersInput").focus();
		}
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1 {
  margin: 20px 0 0;
}

button {
  margin: 0 10px;
}

.error {
  color: red;
  font-weight: bold;
  font-size: 1.5em;
}

.text-monospace {
  font-family: monospace;
}
</style>
