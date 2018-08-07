// api url prefix
// export const API_URL: stirng = 'http://127.0.0.1:8080/api';
export const API_URL = 'api';
export const apiURL = (url) => API_URL + '/' + url;

export const components = {
    loading: {
        subject: 'components.loading.subject',
        message: 'Loading...'
    }
}

export const ajax = {
    timeout: 1000 * 60
}