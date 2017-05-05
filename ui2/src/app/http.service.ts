import {Injectable} from '@angular/core';
import {Headers, Http, RequestOptions, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class HttpService {

  public token;

  constructor(private http: Http) {
  }

  public get(link) {
    return this.request('GET', link);
  }

  public post(link, body) {
    return this.request('POST', link, body);
  }

  public delete(link) {
    return this.request('DELETE', link);
  }

  public put(link, body) {
    return this.request('PUT', link);
  }

  private request(method, url, body?): Observable<Response> {
    url = 'http://localhost:8080' + url;
    let headers = new Headers({'Content-Type': 'application/json', 'Accept': 'application/json'});
    if (this.token) {
      headers.set('Authorization', 'Bearer ' + this.token);
    }
    let options = new RequestOptions({headers: headers});
    if (method === 'GET') {
      return this.http.get(url, options);
    } else if (method === 'POST') {
      return this.http.post(url, body, options);
    } else if (method === 'PUT') {
      return this.http.put(url, body, options);
    } else if (method === 'DELETE') {
      return this.http.delete(url, options);
    }
  }


}
