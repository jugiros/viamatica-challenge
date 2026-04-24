import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CategoryService } from './category.service';
import { CategoryModel, CreateCategoryRequest } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';

@Component({
  selector: 'app-category-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.scss'
})
export class CategoryFormComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  
  category = signal<CategoryModel | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  categoryRequest = signal<CreateCategoryRequest>({
    name: '',
    description: ''
  });
  
  ngOnInit() {
    // Initialize form
  }
  
  saveCategory() {
    if (!this.categoryRequest().name) {
      this.errorMessage.set('Por favor, ingrese el nombre de la categoría.');
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    this.categoryService.createCategory(this.categoryRequest()).subscribe({
      next: (category) => {
        this.toastService.showSuccess('Categoría creada exitosamente.');
        this.category.set(category);
        this.successMessage.set('Categoría creada exitosamente.');
        this.isLoading.set(false);
        this.router.navigate(['/categories']);
      },
      error: (error) => {
        this.toastService.showError('Error al crear la categoría. Por favor, intenta nuevamente.');
        this.errorMessage.set('Error al crear la categoría. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }
  
  cancel() {
    this.router.navigate(['/categories']);
  }
}
