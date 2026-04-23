import { Component, inject, signal, computed, effect, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from './order.service';
import { ProductService } from '../products/product.service';
import { ProductModel, CreateOrderRequest } from '../../core/models';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';

@Component({
  selector: 'app-order-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, CurrencyLocalePipe],
  templateUrl: './order-form.component.html',
  styleUrl: './order-form.component.scss'
})
export class OrderFormComponent {
  private readonly orderService = inject(OrderService);
  private readonly productService = inject(ProductService);
  
  availableProducts = signal<ProductModel[]>([]);
  orderItems = signal<{ productId: number; quantity: number }[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  // Computed para calcular total en tiempo real
  total = computed(() => {
    return this.orderItems().reduce((sum, item) => {
      const product = this.availableProducts().find(p => p.id === item.productId);
      return sum + (product ? product.price * item.quantity : 0);
    }, 0);
  });
  
  constructor() {
    this.loadProducts();
    
    // Effect para logging/side effects
    effect(() => {
      console.log('Order items changed:', this.orderItems());
      console.log('Total:', this.total());
    });
  }
  
  private loadProducts() {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.availableProducts.set(products.filter(p => p.active && p.stock > 0));
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar productos');
      }
    });
  }
  
  addToOrder(product: ProductModel) {
    const currentItems = this.orderItems();
    const existingItem = currentItems.find(item => item.productId === product.id);
    
    if (existingItem) {
      if (existingItem.quantity < product.stock) {
        this.orderItems.set(
          currentItems.map(item =>
            item.productId === product.id
              ? { ...item, quantity: item.quantity + 1 }
              : item
          )
        );
      }
    } else {
      this.orderItems.set([...currentItems, { productId: product.id, quantity: 1 }]);
    }
  }
  
  removeFromOrder(productId: number) {
    const currentItems = this.orderItems();
    const existingItem = currentItems.find(item => item.productId === productId);
    
    if (existingItem) {
      if (existingItem.quantity > 1) {
        this.orderItems.set(
          currentItems.map(item =>
            item.productId === productId
              ? { ...item, quantity: item.quantity - 1 }
              : item
          )
        );
      } else {
        this.orderItems.set(currentItems.filter(item => item.productId !== productId));
      }
    }
  }
  
  isProductInOrder(productId: number): boolean {
    return this.orderItems().some(item => item.productId === productId);
  }
  
  getProductQuantity(productId: number): number {
    return this.orderItems().find(item => item.productId === productId)?.quantity || 0;
  }
  
  canAddProduct(product: ProductModel): boolean {
    const currentQuantity = this.getProductQuantity(product.id);
    return currentQuantity < product.stock;
  }
  
  submitOrder() {
    if (this.orderItems().length === 0) {
      this.errorMessage.set('La orden no tiene productos');
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    const request: CreateOrderRequest = {
      items: this.orderItems()
    };
    
    this.orderService.createOrder(request).subscribe({
      next: (order) => {
        this.successMessage.set('Orden creada exitosamente');
        this.orderItems.set([]);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al crear orden');
        this.isLoading.set(false);
      }
    });
  }
}
