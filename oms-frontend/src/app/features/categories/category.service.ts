import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { CategoryModel, CreateCategoryRequest, UpdateCategoryRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends BaseHttpService {
  getCategories(): Observable<CategoryModel[]> {
    return this.get<any>('/categories?page=0&size=100').pipe(
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

  getCategoryById(id: number): Observable<CategoryModel> {
    return this.get<CategoryModel>(`/categories/${id}`);
  }

  createCategory(request: CreateCategoryRequest): Observable<CategoryModel> {
    return this.post<CategoryModel>('/categories', request);
  }

  updateCategory(id: number, request: UpdateCategoryRequest): Observable<CategoryModel> {
    return this.put<CategoryModel>(`/categories/${id}`, request);
  }

  deleteCategory(id: number): Observable<void> {
    return this.delete<void>(`/categories/${id}`);
  }
}
