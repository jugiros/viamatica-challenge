export interface ProductModel {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
  categoryId?: number;
  active?: boolean;
}
