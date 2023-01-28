import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "src/app/model/user";
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private httpClient: HttpClient) {}

  loginUser(user: User): Observable<object> {
    return this.httpClient.post(`${this.apiServerUrl}/users/login`, user);
  }

  register(user: User): Observable<object> {
    return this.httpClient.post(`${this.apiServerUrl}/users`, user);
  }

  getUsers(): Observable<User[]> {
    return this.httpClient.get<User[]>(`${this.apiServerUrl}/users`);
  }

  confirmRegistrationEmail(): Observable<User[]> {
    return this.httpClient.get<User[]>(`${this.apiServerUrl}/users/confirm`);
  }


  getUserByUsername(username: string): Observable<User> {
    return this.httpClient.get<User>(`${this.apiServerUrl}/users/byUsername/${username}`);
  }
}
