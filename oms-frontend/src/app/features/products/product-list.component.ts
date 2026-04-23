import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from './product.service';
import { ProductModel } from '../../core/models';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';

@Component({
  selector: 'app-product-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, CurrencyLocalePipe],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductService);
  
  products = signal<ProductModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadProducts();
  }
  
  private loadProducts() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar productos. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }
}
