import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { ProductModel, CreateProductRequest, UpdateProductRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends BaseHttpService {
  getProducts(): Observable<ProductModel[]> {
    return this.get<any>('/products?page=0&size=100').pipe(
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

  getProductById(id: number): Observable<ProductModel> {
    return this.get<ProductModel>(`/products/${id}`);
  }

  createProduct(request: CreateProductRequest): Observable<ProductModel> {
    return this.post<ProductModel>('/products', request);
  }

  updateProduct(id: number, request: UpdateProductRequest): Observable<ProductModel> {
    return this.put<ProductModel>(`/products/${id}`, request);
  }

  deleteProduct(id: number): Observable<void> {
    return this.delete<void>(`/products/${id}`);
  }
}
