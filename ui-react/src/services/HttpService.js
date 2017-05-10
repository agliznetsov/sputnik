class HttpService {

    token;

    get(link) {
        return this.request('GET', link);
    }

    post(link, body) {
        return this.request('POST', link, body);
    }

    delete(link) {
        return this.request('DELETE', link);
    }

    put(link, body) {
        return this.request('PUT', link, body);
    }

    request(method, url, body) {
        url = 'http://localhost:8080' + url;
        let headers = new Headers({'Content-Type': 'application/json', 'Accept': 'application/json'});
        if (this.token) {
            headers.set('Authorization', 'Bearer ' + this.token);
        }
        let options = {
            method: method,
            headers: headers,
            mode: 'cors',
            cache: 'no-cache'
        };
        if (body !== null && body !== undefined) {
            options.body = JSON.stringify(body);
        }
        return fetch(url, options)
            .then(response => {
                if (response.ok) {
                    return response.json().then(data => {
                        return {status: response.status, headers: response.headers, data: data};
                    }, nodata => {
                        return {status: response.status, headers: response.headers};
                    });
                } else {
                    return response.json().then(data => {
                        return Promise.reject({status: response.status, headers: response.headers, data: data});
                    }, nodata => {
                        return Promise.reject({status: response.status, headers: response.headers});
                    });
                }
            });
    }

}

let httpService = new HttpService();

export default httpService;