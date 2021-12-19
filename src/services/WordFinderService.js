import axios from 'axios';

//axios.defaults.baseURL = 'http://localhost:5000';
axios.defaults.baseURL = '.';

export default {
    getWords(characters, indexOfFirst, maxToReturn) {
        let url = `/findwords?characters=${characters}&minNumChars=3&indexOfFirst=${indexOfFirst}&maxToReturn=${maxToReturn}`;
        return axios.get(url);
    }
}