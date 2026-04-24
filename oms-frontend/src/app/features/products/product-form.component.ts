import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from './product.service';
import { CategoryService } from '../categories/category.service';
import { ProductModel, CategoryModel, CreateProductRequest, UpdateProductRequest } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.scss'
})
export class ProductFormComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly categoryService = inject(CategoryService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  
  categories = signal<CategoryModel[]>([]);
  product = signal<ProductModel | null>(null);
  productId = signal<number | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  productRequest = signal<CreateProductRequest>({
    name: '',
    description: '',
    price: 0,
    stock: 0,
    categoryId: 0
  });
  
  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productId.set(Number(id));
      this.loadProduct(Number(id));
    }
    this.loadCategories();
  }
  
  private loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (categories: CategoryModel[]) => {
        this.categories.set(categories);
      },
      error: (error) => {
        this.toastService.showError('Error al cargar categorías');
        this.errorMessage.set('Error al cargar categorías');
      }
    });
  }
  
  private loadProduct(id: number) {
    this.isLoading.set(true);
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        this.productRequest.set({
          name: product.name,
          description: product.description,
          price: product.price,
          stock: product.stock,
          categoryId: product.categoryId
        });
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar el producto.');
        this.isLoading.set(false);
      }
    });
  }
  
  saveProduct() {
    if (!this.productRequest().name || !this.productRequest().categoryId) {
      this.errorMessage.set('Por favor, complete todos los campos requeridos.');
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    const id = this.productId();
    if (id) {
      this.productService.updateProduct(id, this.productRequest()).subscribe({
        next: (product) => {
          this.toastService.showSuccess('Producto actualizado exitosamente.');
          this.product.set(product);
          this.successMessage.set('Producto actualizado exitosamente.');
          this.isLoading.set(false);
          this.router.navigate(['/products']);
        },
        error: (error) => {
          this.toastService.showError('Error al actualizar el producto.');
          this.errorMessage.set('Error al actualizar el producto.');
          this.isLoading.set(false);
        }
      });
    } else {
      this.productService.createProduct(this.productRequest()).subscribe({
        next: (product) => {
          this.toastService.showSuccess('Producto creado exitosamente.');
          this.product.set(product);
          this.successMessage.set('Producto creado exitosamente.');
          this.isLoading.set(false);
          this.router.navigate(['/products']);
        },
        error: (error) => {
          this.toastService.showError('Error al crear el producto.');
          this.errorMessage.set('Error al crear el producto.');
          this.isLoading.set(false);
        }
      });
    }
  }
  
  cancel() {
    this.router.navigate(['/products']);
  }
}
