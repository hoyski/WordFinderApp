import axios from 'axios';

export default {

  getDefinition(word) {
    let url = `https://api.dictionaryapi.dev/api/v2/entries/en/${word}`;
    return axios.get(url);
  },

  reduceDefinition(defArray) {

    // Collapse all of the definitions into a single definition
    let definition = {};
    definition.word = defArray[0].word;
    definition.phonetic = defArray[0].phonetic;
    definition.meanings = [];

    for (let def of defArray) {
      for (let meaning of def.meanings) {
        let matchedMeaning = definition.meanings.find((i) => i.partOfSpeech == meaning.partOfSpeech);
        if (matchedMeaning === undefined) {
          // First occurance. Add entire meaning to return value
          definition.meanings.push(meaning);
        } else {
          // Already have this part of speech. Just concatenate the defs to it
          matchedMeaning.definitions = matchedMeaning.definitions.concat(meaning.definitions);
        }
      }
    }

    return definition;
  }

}