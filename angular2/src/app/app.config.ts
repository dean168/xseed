// api url prefix
// export const API_URL: stirng = 'http://127.0.0.1:8080/api';
export const API_URL: string = 'api';
export const apiURL = (url) => API_URL + '/' + url;

// languages
export const LANGUAGE_STORAGE_KEY: string = 'xseed.language';
export const LANGUAGES: [string, string] = ['en', 'zh'];
export const LANGUAGE_DEFAULT = 'en';