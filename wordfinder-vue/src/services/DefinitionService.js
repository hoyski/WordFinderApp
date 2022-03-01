import axios from 'axios';

export default {

  getDefinition(word) {
    let url = `https://api.dictionaryapi.dev/api/v2/entries/en/${word}`;
    return axios.get(url);
  }

}