import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { ProductModel, CreateProductRequest, UpdateProductRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends BaseHttpService {
  getProducts(): Observable<ProductModel[]> {
    return this.get<ProductModel[]>('/products');
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
