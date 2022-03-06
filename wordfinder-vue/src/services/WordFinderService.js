import axios from 'axios';

// Use this URL when developing locally and running Vue separately from the service
//axios.defaults.baseURL = 'http://localhost:5000';

// Use this URL when bundling the Vue into the application WAR file
axios.defaults.baseURL = '.';

export default {
    getWords(characters, pattern, indexOfFirst, maxToReturn) {
        let url = `/findwords?characters=${characters}&pattern=${pattern}&minNumChars=3&indexOfFirst=${indexOfFirst}&maxToReturn=${maxToReturn}`;
        return axios.get(url);
    }
}