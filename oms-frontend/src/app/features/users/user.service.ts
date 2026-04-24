import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { UserModel } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseHttpService {
  getUsers(page: number = 0, size: number = 100): Observable<UserModel[]> {
    return this.get<any>(`/users?page=${page}&size=${size}`).pipe(
      map(response => {
        if (response && response.content && Array.isArray(response.content)) {
          return response.content;
        }
        if (Array.isArray(response)) {
          return response;
        }
        return [];
      })
    );
  }

  getUserById(id: number): Observable<UserModel> {
    return this.get<UserModel>(`/users/${id}`);
  }

  updateUser(id: number, request: any): Observable<UserModel> {
    return this.put<UserModel>(`/users/${id}`, request);
  }

  deleteUser(id: number): Observable<void> {
    return this.delete<void>(`/users/${id}`);
  }
}
